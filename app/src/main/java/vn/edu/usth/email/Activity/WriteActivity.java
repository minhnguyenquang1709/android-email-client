package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.edu.usth.email.R;

public class WriteActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;

    private ImageButton sendEmailButton;
    private ImageButton addFileButton;
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
        addFileButton = findViewById(R.id.add_files_button);
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
                }
                else{
                    Log.e("Compose Error", "Couldn't make MIME message!");
                }
            }
        });
        addFileButton.setOnClickListener(v -> openFilePicker());
        moreOptionsButton.setOnClickListener(v -> showMoreOptions());

        // Set listeners for the dropdown arrow buttons
        fromArrowButton.setOnClickListener(v -> showEmailDropdown(fromArrowButton, fromEmailField));
        toArrowButton.setOnClickListener(v -> showEmailDropdown(toArrowButton, toEmailField));
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

//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_write, container, false);
//
//        // Initialize fields and buttons
//        sendEmailButton = view.findViewById(R.id.send_email_button);
//        addFileButton = view.findViewById(R.id.add_files_button);
//        moreOptionsButton = view.findViewById(R.id.more_options_button);
//        fromArrowButton = view.findViewById(R.id.from_arrow_button);
//        toArrowButton = view.findViewById(R.id.to_arrow_button);
//        fromEmailField = view.findViewById(R.id.input_email);
//        toEmailField = view.findViewById(R.id.input_to_email);
//        subjectField = view.findViewById(R.id.input_subject);
//        bodyField = view.findViewById(R.id.input_email_body);
//
//        // Set listeners for the buttons
////        sendEmailButton.setOnClickListener(v -> sendEmail());
//        addFileButton.setOnClickListener(v -> openFilePicker());
//        moreOptionsButton.setOnClickListener(v -> showMoreOptions());
//
//        // Set listeners for the dropdown arrow buttons
//        fromArrowButton.setOnClickListener(v -> showEmailDropdown(fromArrowButton, fromEmailField));
//        toArrowButton.setOnClickListener(v -> showEmailDropdown(toArrowButton, toEmailField));
//
//        return view;
//    }

//    private void sendEmail() {
//        // Retrieve the input data from the fields
//        String fromEmail = fromEmailField.getText().toString();
//        String toEmail = toEmailField.getText().toString();
//        String subject = subjectField.getText().toString();
//        String body = bodyField.getText().toString();
//        String password = "your_password_or_app_password"; // Replace with the actual password or retrieve it from user input securely
//
//        // Start AsyncTask to send email in the background
//        new SendEmailTask(fromEmail, toEmail, subject, body, password).execute();
//    }

//    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
//        private String fromEmail;
//        private String toEmail;
//        private String subject;
//        private String body;
//        private String password;
//        private String errorMessage;
//
//        public SendEmailTask(String fromEmail, String toEmail, String subject, String body, String password) {
//            this.fromEmail = fromEmail;
//            this.toEmail = toEmail;
//            this.subject = subject;
//            this.body = body;
//            this.password = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            try {
//                // Set up properties for the mail session
//                Properties properties = new Properties();
//                properties.put("mail.smtp.auth", "true");
//                properties.put("mail.smtp.starttls.enable", "true");
//                properties.put("mail.smtp.host", "smtp.gmail.com");
//                properties.put("mail.smtp.port", "587");
//
//                // Create a new session with an authenticator
//                Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//                    @Override
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(fromEmail, password);
//                    }
//                });
//
//                // Create a new email message
//                MimeMessage message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(fromEmail));
//                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
//                message.setSubject(subject);
//                message.setText(body);
//
//                // Attach the file if selected (implement file attachment logic as needed)
//                if (selectedFileUri != null) {
//                    // Add handling for attachment if necessary
//                }
//
//                // Send the email
//                Transport.send(message);
//                return true;
//
//            } catch (MessagingException e) {
//                e.printStackTrace();
//                errorMessage = e.getMessage();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            if (success) {
//                Toast.makeText(WriteActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(WriteActivity.this, "Error sending email: " + errorMessage, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(WriteActivity.this, "File selected: " + selectedFileUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(WriteActivity.this, moreOptionsButton);
        popupMenu.getMenu().add("Setting");
        popupMenu.getMenu().add("Discard Email");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Setting":
                    openSettings();
                    return true;
                case "Discard Email":
                    discardEmail();
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void openSettings() {
        Toast.makeText(WriteActivity.this, "Opening settings...", Toast.LENGTH_SHORT).show();
        // Implement actual settings logic here
    }

    private void discardEmail() {
        fromEmailField.setText("");
        toEmailField.setText("");
        subjectField.setText("");
        bodyField.setText("");
        Toast.makeText(WriteActivity.this, "Email discarded!", Toast.LENGTH_SHORT).show();
    }

    private void showEmailDropdown(View anchor, EditText emailField) {
        PopupMenu popupMenu = new PopupMenu(WriteActivity.this, anchor);

        // Example dynamic fetching of email addresses (replace with actual email retrieval logic)
        List<String> emails = getUserEmailAddresses();
        for (String email : emails) {
            popupMenu.getMenu().add(email);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            emailField.setText(item.getTitle());
            return true;
        });

        popupMenu.show();
    }

    // Method to fetch user emails using Gmail API (replace with actual logic to get user emails)
    private List<String> getUserEmailAddresses() {
        // Example hardcoded emails - implement Gmail API logic to get real data
        return List.of("email1@example.com", "email2@example.com", "email3@example.com");
    }
}
