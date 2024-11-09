package vn.edu.usth.email.Activity;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
//import com.google.api.client.auth.oauth2.Credential;
import androidx.credentials.CredentialManager;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.GetPasswordOption;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.Scope;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.api.services.gmail.GmailScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import vn.edu.usth.email.R;

public class AuthActivity extends AppCompatActivity {
    private LinearLayout boxAddAddress;

    private static final int REQUEST_AUTHORIZE = 1001;

    private ArrayList<GoogleIdTokenCredential> credentialList;

    private ArrayList<String> accessTokenList;

    private final Handler handler;

    public AuthActivity() {
        Log.i("AuthActivity", "AuthActivity constructor");
        this.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                // executed in the main thread
                String content = msg.getData().toString();
                Toast.makeText(AuthActivity.this, content, Toast.LENGTH_SHORT).show();
                Log.i("Handler", content);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        credentialList = new ArrayList<>();

        accessTokenList = new ArrayList<>();

        // initialize a CredentialManager object
        CredentialManager credentialManager = CredentialManager.create(this);

        // Retrieves the user's saved password for your app from their
        // password provider.
        GetPasswordOption getPasswordOption = new GetPasswordOption();

        String nonce = UUID.randomUUID().toString();
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(getString(R.string.client_id))
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build();

        // retrieve user's Google ID Token
        GetSignInWithGoogleOption getSignInWithGoogleOption = new GetSignInWithGoogleOption.Builder(getString(R.string.client_id))
            .setNonce(nonce)
            .build();

        GetCredentialRequest getCredRequest = new GetCredentialRequest.Builder()
            .addCredentialOption(getSignInWithGoogleOption)
            .build();

        // click on the view -> start authentication
        boxAddAddress = findViewById(R.id.box_add_address);
        boxAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AuthActivity.this, "start adding an email address", Toast.LENGTH_SHORT).show();

                // start authentication
                credentialManager.getCredentialAsync(
                    // Use activity based context to avoid undefined
                    // system UI launching behavior
                    AuthActivity.this,
                    getCredRequest,
                    new CancellationSignal(),
                    Runnable::run,
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>(){
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleSignIn(result);
                            authorize(handler);
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            handleFailure(e);
                        }
                    }
                );
            }
        });
    }

    // Start the Google Sign-In intent
    private void handleSignIn(GetCredentialResponse result) {
        // Handle the successfully returned credential.
        Credential credential = result.getCredential();

//        if (credential instanceof PublicKeyCredential) {
//            String responseJson = ((PublicKeyCredential) credential).getAuthenticationResponseJson();
//            Toast.makeText(this, "PublicKeyCredential received: " + responseJson, Toast.LENGTH_LONG).show();
//        } else if (credential instanceof PasswordCredential) {
//            String username = ((PasswordCredential) credential).getId();
//            String password = ((PasswordCredential) credential).getPassword();
//            Toast.makeText(this, "Username: " + username + ", Password: " + password, Toast.LENGTH_LONG).show();
//        } else {
//            // Catch any unrecognized credential type here.
//            Log.i("AuthActivity", "Unexpected type of credential: " + result.toString());
//
//        }

        if(credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)){
            Log.i("Auth", "GoogleIdTokenCredential");
            // convert the credential object into a GoogleIdTokenCredential
            GoogleIdTokenCredential idTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
            // extract GoogleIdTokenCredential
            // Log.i("AuthActivity", "id: " + idTokenCredential.getId()); // the email
            // Log.i("AuthActivity", "idToken: " + idTokenCredential.getIdToken()); // JWT token

            Log.i("Auth", idTokenCredential.toString());

            credentialList.add(idTokenCredential);
        }
    }

    // update UI
    private void updateEmailList(){
        LinearLayout container = findViewById(R.id.email_list);
        container.removeAllViews();
        for(int i = 0; i < credentialList.size(); i++){
            TextView tv = new TextView(AuthActivity.this);
            tv.setWidth(container.getWidth());
            tv.setText(credentialList.get(i).getId());
            tv.setTextColor(getColor(R.color.blue_text));
            container.addView(tv);

            String accessToken = null;

            try {
                accessToken = accessTokenList.get(i);
            }catch (IndexOutOfBoundsException e){
                Log.e("Auth Error", "No corresponding access token: " + accessToken);
            }

            if(accessToken != null){
                String accessTok = accessToken; // for calling from inner class
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = tv.getText().toString();
                        Intent intent = new Intent(AuthActivity.this, WriteActivity.class);
                        intent.putExtra("userId", email);
                        intent.putExtra("accessToken", accessTok);
                        startActivity(intent);
                    }
                });
            }else{
                // if unable to get access token, run authorization again when pressing on email
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = tv.getText().toString();
                        authorize(handler);
                    }
                });
            }
        }
    }

    // Handle errors during credential retrieval
    private void handleFailure(GetCredentialException e) {
        Log.e("AuthActivtiy Error", "Credential retrieval failed: " + e);
    }

    // authorize google user
    private void  authorize(Handler handler){
        List<Scope> requestedScopes = Arrays.asList(
                new Scope(GmailScopes.GMAIL_LABELS),
                new Scope(GmailScopes.MAIL_GOOGLE_COM));
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .requestOfflineAccess(getString(R.string.client_id))
                .build();

        Identity.getAuthorizationClient(this)
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if(authorizationResult.hasResolution()){
                                // access needs to be granted by the user
                                Log.i("Auth","access needs to be granted by the user");
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                                            REQUEST_AUTHORIZE, null, 0, 0, 0, null);
                                }catch (IntentSender.SendIntentException e){
                                    Log.e("AuthActivity Error", "Couldn't start Authorization UI: " + e.getLocalizedMessage());
                                }
//                                String accessToken = authorizationResult.getAccessToken();
//                                Log.i("AuthActivity", "accessToken: " + accessToken);
                                runOnUiThread(()-> updateEmailList());
                            }else{
                                // access already granted, get access token
                                String accessToken = authorizationResult.getAccessToken();
                                Log.i("AuthActivity", "Authorization successful");
//                                Log.i("AuthActivity", "accessToken: " + accessToken);
//                                Log.i("AuthActivity", "server auth code: " + authorizationResult.getServerAuthCode());
                                accessTokenList.add(accessToken);
                                runOnUiThread(()-> updateEmailList());
                            }
                        }
                )
                .addOnFailureListener(e -> Log.e("AuthActivity", "Failed to authorize", e));
    }
}