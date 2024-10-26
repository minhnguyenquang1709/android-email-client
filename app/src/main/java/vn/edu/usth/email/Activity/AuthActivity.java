package vn.edu.usth.email.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;

import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CancellationSignal;
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
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.Scope;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailRequest;
import com.google.api.services.gmail.GmailScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import vn.edu.usth.email.R;

public class AuthActivity extends AppCompatActivity {
    private LinearLayout boxAddAddress;

    private static final int REQUEST_AUTHORIZE = 1001;

    private ArrayList<GoogleIdTokenCredential> credentialList;

    private Gmail service;
    private JsonFactory JSON_FACTORY;


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

        // this must be the only option in GetCredentialRequest
        // retrieve user's Google ID Token
        GetSignInWithGoogleOption getSignInWithGoogleOption = new GetSignInWithGoogleOption.Builder(getString(R.string.client_id))
            .setNonce(nonce)
            .build();

        GetCredentialRequest getCredRequest = new GetCredentialRequest.Builder()
            .addCredentialOption(getSignInWithGoogleOption)
            .build();

        // build a new authorized API client service
        try {
            initializeGmailApiService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }


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

//    private void initializeGmailApiService() throws IOException, GeneralSecurityException{
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        JSON_FACTORY = GsonFactory.getDefaultInstance();
//        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, )
//    }

    // Start the Google Sign-In intent
    private void handleSignIn(GetCredentialResponse result) {
        // Handle the successfully returned credential.
        Credential credential = result.getCredential();
        if (credential instanceof PublicKeyCredential) {
            String responseJson = ((PublicKeyCredential) credential).getAuthenticationResponseJson();
            Toast.makeText(this, "PublicKeyCredential received: " + responseJson, Toast.LENGTH_LONG).show();
            // Share responseJson i.e. a GetCredentialResponse on your server to validate and authenticate
        } else if (credential instanceof PasswordCredential) {
            String username = ((PasswordCredential) credential).getId();
            String password = ((PasswordCredential) credential).getPassword();
            Toast.makeText(this, "Username: " + username + ", Password: " + password, Toast.LENGTH_LONG).show();
            // Use id and password to send to your server to validate and authenticate
        } else {
            // Catch any unrecognized credential type here.
            Log.i("AuthActivity", "Unexpected type of credential: " + result.toString());
//            Toast.makeText(this, "Unexpected type of credential", Toast.LENGTH_SHORT).show(); // must call Looper.prepare()
        }

//        Log.i("AuthActivity", "credential: "+credential.getType());
        if(credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)){
            // convert the credential object into a GoogleIdTokenCredential
            GoogleIdTokenCredential idTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
            // extract GoogleIdTokenCredential
            Log.i("AuthActivity", "id: " + idTokenCredential.getId()); // the email
            Log.i("AuthActivity", "idToken: " + idTokenCredential.getIdToken()); // JWT token

            credentialList.add(idTokenCredential);

            runOnUiThread(()->{
                updateEmailList();
            });

            authorize();
        }
    }

    // update UI
    private void updateEmailList(){
        LinearLayout container = findViewById(R.id.email_list);
        credentialList.forEach(credential->{
            TextView tv = new TextView(AuthActivity.this);
            tv.setWidth(container.getWidth());
            tv.setText(credential.getId());
            tv.setTextColor(getColor(R.color.blue_text));
            container.addView(tv);
        });
    }

    // Handle errors during credential retrieval
    private void handleFailure(GetCredentialException e) {
        //Toast.makeText(this, "Credential retrieval failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("AuthActivtiy Error", "Credential retrieval failed: " + e);
        // Implement fallback logic: show a sign-in form
    }

    // authorize google user
    private void authorize(){
        List<Scope> requestedScopes = Arrays.asList(
                new Scope(GmailScopes.GMAIL_READONLY),
                new Scope(GmailScopes.GMAIL_LABELS),
                new Scope(GmailScopes.GMAIL_COMPOSE) );
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .build();

        Identity.getAuthorizationClient(this)
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if(authorizationResult.hasResolution()){
                                // access needs to be granted by the user
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                                            REQUEST_AUTHORIZE, null, 0, 0, 0, null);
                                }catch (IntentSender.SendIntentException e){
                                    Log.e("AuthActivity Error", "Couldn't start Authorization UI: " + e.getLocalizedMessage());
                                }
                            }else{
                                // access already granted, continue with user action
                                // logic to get data
                                Log.i("AuthActivity", "Authorization successful");
                                Log.i("AuthActivity", "accessToken: " + authorizationResult.getAccessToken());
                            }
                        }
                )
                .addOnFailureListener(e -> Log.e("AuthActivity", "Failed to authorize", e));
    }

    // request email data
    private void createGmailService(String accessToken){
        // create
        GoogleCredential credential;
        Gmail service = new Gmail.Builder()
    }
}