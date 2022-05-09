package com.group.CarParking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.group.CarParking.model.Booking;
import com.group.CarParking.model.SlotModel;
import com.group.CarParking.model.Worker;

/**
 * Provides static methods to read the parking-slots database See.
 * {@link SlotBookingService} for write methods to the databse for bookings.
 * 
 * @author Akshat Oke
 */
public class SlotService {
  public static final String COLLECTION_NAME = "parking-slots";
  public static Firestore db = FirestoreClient.getFirestore();

  public static void main(String[] args) {
    Timestamp timestamp = Timestamp.now();
    System.out.println(timestamp.getSeconds());
  }

  /**
   * Get details about a parking slot using the document ID
   * 
   * @param id The document ID
   * @return SlotModel with fields populated from database
   */
  public static SlotModel getOneParkingSlot(String id) throws InterruptedException, ExecutionException {
    DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();
    SlotModel slotModel = null;
    if (document.exists()) {
      slotModel = document.toObject(SlotModel.class);
      slotModel.setId(document.getReference());
      // Populate the workers
      ArrayList<Worker> workersArrayList = new ArrayList<>();
      if (!slotModel.getWorkers().isEmpty()) {
        for (DocumentReference workerRef : slotModel.getWorkers()) {
          future = workerRef.get();
          document = future.get();
          var w = document.toObject(Worker.class);
          w.setId(document.getId());
          workersArrayList.add(w);
        }
      }
      slotModel.setWorkersModelList(workersArrayList);
      // docRef = db.collection("users").document(slotModel.getOwner().getId());
      // future = docRef.get();
      // document = future.get();
      // if (document.exists()) {
      // slotModel.setOwnerUserModel(document.toObject(UserModel.class));
      // }
    }
    return slotModel;
  }

  /**
   * Get <i>all</i> slots from firebase.
   * 
   * @deprecated Only meant for testing. Do not use this in a controller.
   */
  public static ArrayList<SlotModel> getSlots() throws InterruptedException, ExecutionException {
    ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    var arrList = new ArrayList<SlotModel>();
    for (QueryDocumentSnapshot document : documents) {
      var slotModel = document.toObject(SlotModel.class);
      slotModel.setId(document.getReference());
      arrList.add(slotModel);
    }
    return arrList;
  }

  /**
   * Get all slots available at a location.
   * 
   * @param location The location of the slot required for booking
   * @return A <code>SlotModel</code> list
   */
  public static ArrayList<SlotModel> getSlotsWithLocation(String location)
      throws InterruptedException, ExecutionException {
    // final var timeStamp = Timestamp.of(date);
    CollectionReference ref = db.collection(COLLECTION_NAME);
    Query query = ref.whereEqualTo("location", location);// .whereGreaterThan("startDate", timeStamp);
    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> list = future.get().getDocuments();
    ArrayList<SlotModel> arrayList = new ArrayList<SlotModel>();
    for (DocumentSnapshot document : list) {
      var slotModel = document.toObject(SlotModel.class);
      slotModel.setId(document.getReference());
      // if (slotModel.getEndDate().compareTo(timeStamp) >= 0)
      arrayList.add(slotModel);
    }
    return arrayList;
  }

  /**
   * Get all slots available on a particular date and at a location.
   * 
   * @param location The location of the slot required for booking
   * @param date     The <code>java.util.Date</code>
   * @return A <code>SlotModel</code> list
   */
  public static ArrayList<SlotModel> getSlotsWithDate(String location, java.util.Date date)
      throws InterruptedException, ExecutionException {
    final var timeStamp = Timestamp.of(date);
    CollectionReference ref = db.collection(COLLECTION_NAME);
    Query query = ref.whereEqualTo("location", location).whereLessThan("startDate", timeStamp);
    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> list = future.get().getDocuments();
    ArrayList<SlotModel> arrayList = new ArrayList<SlotModel>();
    for (DocumentSnapshot document : list) {
      var slot = document.toObject(SlotModel.class);
      slot.setId(document.getReference());
      if (slot.getEndDate().compareTo(timeStamp) >= 0)
        arrayList.add(slot);
    }
    return arrayList;
  }

  /**
   * Get slots on the given date and hours. Hours are to be given as startTime and
   * duration (not the endTime)
   * 
   * @param location       Location as a String. Must be chosen from a shown
   *                       dropdown in the frontend
   * @param date           The date of the booking
   * @param startTimestamp Start time of the booking. Ex. 12:00, 14:30, 9:40 etc.
   *                       Use a <code>Timestamp</code> to represent the time.
   * @param duration       Duration of the booking in hours
   * @return ArrayList of the available <code>SlotModel</code>'s
   * @see <a href=
   *      "https://firebase.google.com/docs/reference/android/com/google/firebase/Timestamp">Timestamp</a>
   */
  public static ArrayList<SlotModel> getSlotsWithDateAndTime(String location, java.util.Date date,
      Timestamp startTimestamp, int duration) throws InterruptedException, ExecutionException {
    long milliseconds = startTimestamp.getSeconds() * 1000;
    milliseconds += duration * 3600000;
    Timestamp endTimestampRequested = Timestamp.of(new java.util.Date(milliseconds));
    final Booking attemptBooking = new Booking(startTimestamp, endTimestampRequested, null, null);
    final var dateTimeStamp = Timestamp.of(date);
    CollectionReference ref = db.collection(COLLECTION_NAME);
    Query query = ref.whereEqualTo("location", location).whereLessThan("startDate", dateTimeStamp);
    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> list = future.get().getDocuments();
    ArrayList<SlotModel> arrayList = new ArrayList<>();
    for (DocumentSnapshot document : list) {
      SlotModel docSlot = document.toObject(SlotModel.class);
      docSlot.setId(document.getReference());
      boolean isAvailable = checkSlotAvailability(attemptBooking, dateTimeStamp, docSlot);
      if (isAvailable)
        arrayList.add(docSlot);
    }
    return arrayList;
  }

  /**
   * 
   * @param attemptBooking The Booking that the user wants
   * @param dateTimeStamp  Date of the booking
   * @param docSlot        The SlotModel of the slot the user wants to book
   * @return If the slot is available
   */
  static boolean checkSlotAvailability(final Booking attemptBooking, final Timestamp dateTimeStamp, SlotModel docSlot) {
    List<Booking> bookings = docSlot.getBookings();
    boolean isAvailable = docSlot.getEndDate().compareTo(dateTimeStamp) >= 0;

    if (isAvailable && bookings != null)
      for (Booking booking : bookings) {
        isAvailable = doesNotOverlap(booking.getStartTime(), booking.getEndTime(), attemptBooking);
      }
    return isAvailable;
  }

  private static boolean doesNotOverlap(Timestamp startTime, Timestamp endTime, Booking booking) {
    // return (startTime.compareTo(booking.getStartTime()) >= 0 &&
    // startTime.compareTo(booking.getEndTime()) < 0)
    // || (endTime.compareTo(booking.getStartTime()) > 0 &&
    // endTime.compareTo(booking.getEndTime()) <= 0);
    return (startTime.compareTo(booking.getStartTime()) >= 0 && booking.getEndTime().compareTo(startTime) <= 0)
        || (endTime.compareTo(booking.getStartTime()) <= 0 && booking.getEndTime().compareTo(endTime) <= 0);
  }
  // location
  // number of hours

  /**
   * Create a slot from a provided <code> SlotModel</code> object. Use the
   * {@link SlotModel#save()} method instead for a clean look.
   * 
   * @param slotModel SlotModel to save to Firebase
   * @return ID of the document created.
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public static String createSlot(SlotModel slotModel) throws InterruptedException, ExecutionException {
    var json = slotModel.toJSON();
    ApiFuture<DocumentReference> addedDocRef = db.collection(COLLECTION_NAME).add(json);
    return addedDocRef.get().getId();
  }

  /**
   * Update a slot, provided the Firestore ID of the slot document
   * 
   * @param id        The ID of the document
   * @param slotModel <code>SlotModel</code> to update
   * @return The update time as a string
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public static String updateSlot(String id, SlotModel slotModel) throws InterruptedException, ExecutionException {
    DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
    var result = docRef.set(slotModel.toJSON());
    return result.get().getUpdateTime().toString();
  }

  public static void deleteSlot(String id) {
    var docRef = db.collection(COLLECTION_NAME).document(id);
    docRef.delete();
  }
}
