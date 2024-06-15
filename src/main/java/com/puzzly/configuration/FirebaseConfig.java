package com.puzzly.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseConfig {

    @PostConstruct
    private void init(){
        try{
            InputStream serviceAccount = FirebaseConfig.class.getClassLoader()
                    .getResourceAsStream("firebase/serviceKey.json");

            if (serviceAccount == null) {
                throw new IOException("firebase/serviceKey.json not found");
            }
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
