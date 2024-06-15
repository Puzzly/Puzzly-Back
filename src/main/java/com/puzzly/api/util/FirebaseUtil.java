package com.puzzly.api.util;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FirebaseUtil {

    public void sendNotification(String title, String body, String token) {
        // Create a notification message
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        try {
            // Send the message
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public void sendTest(){
        /////test
        Firestore db = getFirestore();

        // Define your collection and document
        String collectionName = "notify";
        String documentId = "msg";

        // Create a map of data to add
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", "admin");
        userData.put("lastName", "test");
        userData.put("email", "admin.puzzly.com");
        userData.put("title", "알람");
        userData.put("msg", "알림테스트");

        // Add a document to the collection
        addDocument(db, collectionName, documentId, userData);

    }

    public void addDocument(Firestore db, String collectionName, String documentId, Map<String, Object> data) {
        // Get a reference to the collection
        CollectionReference collection = db.collection(collectionName);

        // Get a reference to the document
        DocumentReference document = collection.document(documentId);

        // Asynchronously write data
        ApiFuture<WriteResult> result = document.set(data);

        // Add a callback to handle success or failure
        result.addListener(() -> {
            try {
                System.out.println("Update time : " + result.get().getUpdateTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Runnable::run);
    }

}
