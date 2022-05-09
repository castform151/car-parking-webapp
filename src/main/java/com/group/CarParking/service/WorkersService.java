package com.group.CarParking.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.group.CarParking.model.Booking;
import com.group.CarParking.model.CarParkService;
import com.group.CarParking.model.SlotModel;
import com.group.CarParking.model.UserModel;
import com.group.CarParking.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

public class WorkersService {
  private static Firestore db = FirestoreClient.getFirestore();
  private static final String WORKER_COLLECTION_NAME = "workers";

  public static String addWorker(Worker worker) throws InterruptedException, ExecutionException {
    var json = worker.toJSON();
    ApiFuture<DocumentReference> addedDocRef = db.collection(WORKER_COLLECTION_NAME).add(json);
    return addedDocRef.get().getId();
  }

  public static List<Worker> getWorkers() throws InterruptedException, ExecutionException {
    ApiFuture<QuerySnapshot> future = db.collection(WORKER_COLLECTION_NAME).get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    var arrList = new ArrayList<Worker>();
    for (QueryDocumentSnapshot document : documents) {
      var slotModel = document.toObject(Worker.class);
      slotModel.setId(document.getReference().getId());
      arrList.add(slotModel);
    }
    return arrList;
  }

  public static boolean updateWorker(String id, Worker worker) throws InterruptedException, ExecutionException {
    DocumentReference docRef = db.collection("workers").document(id);
    ApiFuture<WriteResult> future = docRef.update(worker.toMap());
    try {
      WriteResult result = future.get();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static void deleteW(String id) {
    var docRef = db.collection(WORKER_COLLECTION_NAME).document(id);
    docRef.delete();
  }
  // public static ArrayList<CarParkService>
}
