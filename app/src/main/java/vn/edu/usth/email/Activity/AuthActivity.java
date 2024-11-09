package vn.edu.usth.email.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.Credential;
//import com.google.api.client.auth.oauth2.Credential;
import androidx.credentials.CredentialManager;

import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import vn.edu.usth.email.Helper.GmailServiceHelper;
import vn.edu.usth.email.R;

public class AuthActivity extends AppCompatActivity {
    private LinearLayout boxAddAddress;

    private static final int REQUEST_AUTHORIZE = 1001;

    private ArrayList<GoogleIdTokenCredential> credentialList;

    private ArrayList<String> accessTokenList;

    private Gmail service;

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

        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                // executed in the main thread
                String content = msg.getData().toString();
                Toast.makeText(AuthActivity.this, content, Toast.LENGTH_SHORT).show();
                Log.i("Handler", content);
            }
        };

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

        }


        if(credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)){
            // convert the credential object into a GoogleIdTokenCredential
            GoogleIdTokenCredential idTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());

            credentialList.add(idTokenCredential);

            runOnUiThread(()-> updateEmailList());
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
    private void authorize(Handler handler){
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
                                String accessToken = authorizationResult.getAccessToken();
                                Log.i("AuthActivity", "Authorization successful");
                                Log.i("AuthActivity", "accessToken: " + accessToken);
                                accessTokenList.add(accessToken);

                            }

                            String accessToken = accessTokenList.get(0);
                            try {
                                service = GmailServiceHelper.getService(accessToken);

                                Log.i("Gmail", "Initialized Gmail service instance");

                                if(service == null){
                                    Log.i("Gmail Service Instance", "No service instance");
                                }else{
                                    GoogleIdTokenCredential credential = credentialList.get(0);
                                    String userId = credential.getId();
                                    startSearchActivity(userId, accessToken);
                                }
                            } catch (GeneralSecurityException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .addOnFailureListener(e -> Log.e("AuthActivity", "Failed to authorize", e));
    }

    // request email data
//    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
//        Log.i("GmailAPI", "start getting email data...");
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
//        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();;
//
//        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
//            request.setInterceptor(httpRequest->{
//                // set the access token in the request header
//                httpRequest.getHeaders().setAuthorization("Bearer " + accessToken);
//            });
//        }).build();
//
//        return service;
//    }
    private void startSearchActivity(String userId, String accessToken) {
        Intent intent = new Intent(AuthActivity.this, SearchActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

}