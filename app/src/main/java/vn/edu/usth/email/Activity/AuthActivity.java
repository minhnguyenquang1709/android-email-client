package vn.edu.usth.email.Activity;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;

import java.util.UUID;

import vn.edu.usth.email.R;

public class AuthActivity extends AppCompatActivity {
    private LinearLayout boxAddAddress;

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
//            Toast.makeText(this, "Unexpected type of credential", Toast.LENGTH_SHORT).show();
        }

//        Log.i("AuthActivity", "credential: "+credential.getType());
        if(credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)){
            // convert the credential object into a GoogleIdTokenCredential
            GoogleIdTokenCredential idTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
            // extract ti GoogleIdTokenCredential ID and validate it
            Log.i("AuthActivity", "id: " + idTokenCredential.getId()); // the email
            Log.i("AuthActivity", "idToken: " + idTokenCredential.getIdToken()); // JWT token
            Log.i("AuthActivity", "credential: " + credential); // JWT token
        }
    }

    // Handle errors during credential retrieval
    private void handleFailure(GetCredentialException e) {
        //Toast.makeText(this, "Credential retrieval failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("AuthActivtiy Error", "Credential retrieval failed: " + e);
        // Implement fallback logic: show a sign-in form
    }
}