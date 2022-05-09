package com.group.CarParking;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarParkingApplication {

	public static void main(String[] args) throws IOException {
		// Do not modify this.
		// Ensure that your /resources folder has the firebase-service-key.json file
		ClassLoader classLoader = CarParkingApplication.class.getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource("firebase-service-key.json")).getFile());
		InputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
		GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
		FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
		FirebaseApp.initializeApp(options);
		SpringApplication.run(CarParkingApplication.class, args);
	}

}
