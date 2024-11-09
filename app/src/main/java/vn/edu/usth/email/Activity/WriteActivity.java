package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.edu.usth.email.R;

public class WriteActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;

    private ImageButton sendEmailButton;
    private ImageButton backButton;
    private ImageButton moreOptionsButton;
    private ImageButton fromArrowButton;
    private ImageButton toArrowButton;
    private EditText fromEmailField;
    private EditText toEmailField;
    private EditText subjectField;
    private EditText bodyField;
    private Uri selectedFileUri;
    private String accessToken;
    private String userId;
    private Gmail service;
    private Handler handler;

    public WriteActivity(){
        accessToken = null;
        userId = null;
        service = null;

        this.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                // executed in the main thread
                String content = msg.getData().toString();
                Toast.makeText(WriteActivity.this, content, Toast.LENGTH_SHORT).show();
                Log.i("Handler", content);
            }
        };;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_write), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        // retrieve data from previous Activity
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        userId = intent.getStringExtra("userId");
        try {
            service = initializeGmailApiService(accessToken);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendEmailButton = findViewById(R.id.send_email_button);
        backButton = findViewById(R.id.back_arrow);
        moreOptionsButton = findViewById(R.id.more_options_button);
        fromArrowButton = findViewById(R.id.from_arrow_button);
        toArrowButton = findViewById(R.id.to_arrow_button);
        fromEmailField = findViewById(R.id.input_email);
        toEmailField = findViewById(R.id.input_to_email);
        subjectField = findViewById(R.id.input_subject);
        bodyField = findViewById(R.id.input_email_body);

        // Set listeners for the buttons
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = toEmailField.getText().toString();
                String from = userId;
                String subject = subjectField.getText().toString();
                String body = bodyField.getText().toString();
                MimeMessage message = null;
                try {
                    message = createEmail(to, from, subject, body);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

                if (message != null){
                    try {
                        sendEmail(service, userId, message, handler);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    finish();
                }
                else{
                    Log.e("Compose Error", "Couldn't make MIME message!");
                }
            }

        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
        Log.i("GmailAPI", "start getting email data...");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();;

        Gmail service;
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.setInterceptor(httpRequest->{
                // set the access token in the request header
                httpRequest.getHeaders().setAuthorization("Bearer " + accessToken);
            });
        }).build();

        return service;
    }

    // build a MIME message
    private MimeMessage createEmail(String to, String from, String subject, String messageBody) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(to)));
        email.setSubject(subject);
        email.setText(messageBody);

        return email;
    }

    private com.google.api.services.gmail.model.Message createGmailMessage(MimeMessage emailContent) throws MessagingException, IOException{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] rawEmail = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawEmail);

        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private void sendEmail(Gmail service, String userId, MimeMessage emailContent, Handler handler) throws MessagingException, IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.gmail.model.Message message = null;
                try {
                    message = createGmailMessage(emailContent);
                    service.users().messages().send(userId, message).execute();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Send email message", "done");
                    }
                });
            }
        });
        t.start();
    }
}
