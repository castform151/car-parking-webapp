package com.group.CarParking.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.group.CarParking.model.Booking;
import com.group.CarParking.model.SlotModel;
import com.group.CarParking.model.UserModel;

import java.util.Arrays;
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

/**
 * Provides methods to write to the database for bookings (and waiting lists) of
 * parking slots
 */
public class SlotBookingService {
  private static final String SLOT_COLLECTION_NAME = "parking-slots";
  private static final String USERS_COLLECTION_NAME = "users";
  private static final String SERVICES_COLLECTION_NAME = "services";
  private static Firestore db = FirestoreClient.getFirestore();

  /**
   * Books a user for the given slot in the `booking` parameter.
   * 
   * @param userId
   * @param slotId
   * @param booking Must have startTime and endTime
   * @throws BookingError         Thrown if user already has booked a slot.
   * @throws InterruptedException
   * @throws ExecutionException
   * @see #enrolUserInWaitingList(String, String, Booking)
   */
  public static void bookUserForSlot(String userId, String slotId, Booking booking)
      throws BookingError, InterruptedException, ExecutionException, IllegalStateException {
    // if (!booking.fieldsAreNotNull())
    // throw new IllegalStateException("Booking requested is not complete. User and
    // Slot IDs must be provided");
    booking.setSlot(db.collection(SLOT_COLLECTION_NAME).document(slotId));
    // booking.setSlot(db.collection(SLOT_COLLECTION_NAME).document(userId));
    DocumentReference docRef = db.collection(USERS_COLLECTION_NAME).document(userId);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();
    UserModel userModel = document.toObject(UserModel.class);
    // if (userModel.getCurrentBooking() != null)
    // throw new BookingError("User has already one slot booked");

    // Save the booking slot ID in User
    userModel.addToBookings(booking);
    System.out.println("SlotModel bookings are" + userModel.getCurrentBookingReferences().get(1));
    userModel.update(userId);
    // Add booking slot in the slot document
    // docRef = db.collection(SLOT_COLLECTION_NAME).document(booking.getSlot());
    docRef = booking.getSlot();
    future = docRef.get();
    document = future.get();
    SlotModel targetSlotModel = document.toObject(SlotModel.class);
    boolean isAvailable = SlotService.checkSlotAvailability(booking, booking.getStartTime(), targetSlotModel);
    if (!isAvailable)
      throw new BookingError("Slots not available, enrol in waiting list instead");
    ApiFuture<WriteResult> arrayUnion = docRef.update("bookings", FieldValue.arrayUnion(booking.toMap()));
  }

  /**
   * Call this method if slots are not available.
   * 
   * @param userId
   * @param booking slotId is not required here
   * @param slotId
   * @throws BookingError
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public static void enrolUserInWaitingList(String userId, String slotId, Booking booking)
      throws BookingError, InterruptedException, ExecutionException {
    DocumentReference docRef = db.collection(USERS_COLLECTION_NAME).document(userId);
    docRef.update("waitingList", FieldValue.arrayUnion(booking.toMap()));
    docRef = db.collection(SLOT_COLLECTION_NAME).document(slotId);
    docRef.update("waitingList", FieldValue.arrayUnion(booking.toMap()));
  }

  public static void cancelBooking(String userId, String slotId, Booking booking)
      throws BookingError, InterruptedException, ExecutionException {
    // if (!booking.fieldsAreNotNull())
    // throw new BookingError("Booking object not complete");
    var now = Timestamp.now();
    if (booking.getStartTime().getSeconds() - now.getSeconds() >= 3600) {
      db.collection(SLOT_COLLECTION_NAME).document(slotId).update("bookings", FieldValue.arrayRemove(booking.toMap()));
      db.collection(USERS_COLLECTION_NAME).document(userId).update("bookings", FieldValue.arrayRemove(booking.toMap()));
      // Refund with credits
      var future = db.collection(SLOT_COLLECTION_NAME).document(slotId).get();
      var doc = future.get();
      SlotModel targetSlotModel = doc.toObject(SlotModel.class);
      db.collection(USERS_COLLECTION_NAME).document(userId).update("credits",
          FieldValue.increment(targetSlotModel.getCostPerHour()
              * ((booking.getEndTime().getSeconds() - booking.getStartTime().getSeconds()) % 3600)));
    } else
      throw new BookingError("Booking cannot be cancelled within 2 hours");
  }
}
