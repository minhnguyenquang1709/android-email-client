package vn.edu.usth.email.Activity;

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

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;

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
        GetSignInWithGoogleOption

        GetCredentialRequest getCredRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(getPasswordOption)
                .build();

        // click on the view -> start authentication
        boxAddAddress = findViewById(R.id.box_add_address);
        boxAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AuthActivity.this, "start adding an email address", Toast.LENGTH_SHORT).show();

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
            Log.e("AuthActivity", "Unexpected type of credential");
            Toast.makeText(this, "Unexpected type of credential", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle errors during credential retrieval

    private void handleFailure(GetCredentialException e) {
        //Toast.makeText(this, "Credential retrieval failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        Log.i("AuthActivtiy", "Credential retrieval failed: " + e.getMessage());
        // Implement fallback logic, such as showing a sign-in form
    }
}