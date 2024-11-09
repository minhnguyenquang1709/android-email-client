package vn.edu.usth.email.Helper;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.Model.Email;

public class GmailServiceHelper {
    private static Gmail gmailService;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Method to get or initialize the Gmail service with the access token
    public static Gmail getService(String accessToken) throws GeneralSecurityException, IOException {
        if (gmailService == null) {
            gmailService = initializeGmailService(accessToken);
        }
        return gmailService;
    }

    // Private method to initialize the Gmail service
    private static Gmail initializeGmailService(String accessToken) throws GeneralSecurityException, IOException {
        final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }).setApplicationName("YourAppName").build();
    }
    public static Gmail initializeGmailApiService(String accessToken) throws Exception {
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                .setApplicationName("YourAppName")
                .build();
    }

    public static List<Email> fetchEmails(Gmail service, String userId, String query) throws IOException {
        List<Email> emailList = new ArrayList<>();
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        if (response.getMessages() != null) {
            for (Message message : response.getMessages()) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                String snippet = fullMessage.getSnippet();
                String senderName = getSenderName(fullMessage);
                String iconText = senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase();
                String time = ""; // Placeholder for time if needed

                emailList.add(new Email(iconText, senderName, snippet, time, fullMessage.getId()));
            }
        }
        return emailList;
    }

    private static String getSenderName(Message message) {
        for (MessagePartHeader header : message.getPayload().getHeaders()) {
            if (header.getName().equalsIgnoreCase("From")) {
                return header.getValue();
            }
        }
        return "Unknown Sender";
    }
}
