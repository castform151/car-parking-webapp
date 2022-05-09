package com.group.CarParking.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.group.CarParking.service.SlotService;
import com.group.CarParking.service.UserService;

/**
 * Parking slot model
 * 
 * @author Akshat-Oke
 */
public class SlotModel {
  /**
   * Creates a new document in Firestore with this current SlotModel
   * 
   * @return The ID of the document created
   */
  public String save() throws InterruptedException, ExecutionException {
    return SlotService.createSlot(this);
  }

  public DocumentReference getId() {
    return id;
  }

  public void setId(DocumentReference id) {
    this.id = id;
  }

  public SlotModel() {
  }

  public List<Worker> getWorkersModelList() {
    return workersModelList;
  }

  public void setWorkersModelList(List<Worker> workersModelList) {
    this.workersModelList = workersModelList;
  }

  public double getCostPerHour() {
    return costPerHour;
  }

  public void setCostPerHour(double costPerHour) {
    this.costPerHour = costPerHour;
  }

  public List<Booking> getWaitingList() {
    return waitingList;
  }

  public void setWaitingList(List<Booking> waitingList) {
    this.waitingList = waitingList;
  }

  public SlotModel(String location, int maxVehicles, double ratings, List<DocumentReference> workers) {
    this.setLocation(location);
    this.maxVehicles = maxVehicles;
    this.ratings = ratings;
    this.workers = workers;
  }

  public Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
  }

  public List<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }

  public double getTotalRatings() {
    return totalRatings;
  }

  public void setTotalRatings(double totalRatings) {
    this.totalRatings = totalRatings;
  }

  public UserModel getOwnerUserModel() {
    return ownerUserModel;
  }

  public void setOwnerUserModel(UserModel ownerUserModel) {
    this.ownerUserModel = ownerUserModel;
  }

  public DocumentReference getOwner() {
    return owner;
  }

  public void setOwner(DocumentReference owner) {
    this.owner = owner;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  private String location;

  private int maxVehicles;
  private List<Booking> bookings;
  private Timestamp startDate;
  private Timestamp endDate;
  private double costPerHour;
  private double ratings;
  private double totalRatings;
  private DocumentReference id;
  // private List<CarParkService> workers;
  private List<DocumentReference> workers;
  private List<Booking> waitingList;
  private List<Worker> workersModelList;
  private DocumentReference owner;
  private UserModel ownerUserModel;

  public List<DocumentReference> getWorkers() {
    if (this.workers == null)
      return new ArrayList<DocumentReference>();
    return this.workers;
  }

  public void setWorkers(List<DocumentReference> w) {
    this.workers = w;
  }

  public int getMaxVehicles() {
    return maxVehicles;
  }

  public void setMaxVehicles(int maxVehicles) {
    this.maxVehicles = maxVehicles;
  }

  public double getRatings() {
    return ratings;
  }

  public void setRatings(double ratings) {
    this.ratings = ratings;
  }

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    ClassLoader classLoader = SlotModel.class.getClassLoader();
    File file = new File(Objects.requireNonNull(classLoader.getResource("firebase-service-key.json")).getFile());
    InputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
    FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
    FirebaseApp.initializeApp(options);
    java.util.Date date = new java.util.Date();
    Timestamp timestamp = Timestamp.now();
    System.out.println(UserService.getUserWaitingList("test"));
    // System.out.println(SlotService.getSlotsWithLocation("Somewhere"));
    // Firestore db = FirestoreClient.getFirestore();
    // DocumentReference docRef =
    // db.collection("parking-slots").document("AmNiuoFL23rBKiV1E6Uk");
    // ApiFuture<DocumentSnapshot> future = docRef.get();
    // DocumentSnapshot document = future.get();
    // SlotModel slotModel;
    // if (document.exists()) {
    // slotModel = document.toObject(SlotModel.class);
    // System.out.println(slotModel);
    // System.out.println(slotModel.getOwner());
    // } else {
    // System.out.println("Not found object");
    // }
  }

  public HashMap<String, Object> toJSON() {
    var json = new HashMap<String, Object>();
    json.put("location", this.location);
    json.put("bookings", this.bookings);
    json.put("startDate", this.startDate);
    json.put("endDate", this.endDate);
    json.put("workers", this.workers);
    json.put("ratings", this.ratings);
    json.put("totalRatings", this.totalRatings);
    json.put("owner", this.owner);
    return json;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("{loc: " + location);
    if (bookings != null)
      for (Booking map : bookings) {
        stringBuilder.append(map.toString());
      }
    stringBuilder.append(" startDate" + startDate + "}");
    return stringBuilder.toString();
  }
  
  public String sdHTML() {
    if(this.startDate==null) return "01/01/2021";
    java.util.Date sd = startDate.toDate();
    return sd.getDate() + "/" + sd.getMonth() + "/2021";
  }
  public String edHTML() {
    if(this.endDate == null) return "01/01/2021";
    java.util.Date sd = endDate.toDate();
    return sd.getDate() + "/" + sd.getMonth() + "/2021";
  }
}
