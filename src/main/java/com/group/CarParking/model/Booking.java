package com.group.CarParking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.group.CarParking.service.SlotService;

/**
 * A Booking Object that contains the startTime, endTime and the userId of the
 * user who booked the slot
 */
public class Booking {
  private Timestamp startTime, endTime;

  private DocumentReference user, slot;
  private String location;
  private ArrayList<DocumentReference> services;

  public void setWorkers(String[] workerStrings) {
    if (services == null) {
      services = new ArrayList<DocumentReference>();
    }
    CollectionReference docRef = SlotService.db.collection("workers");
    for (int i = 0; i < workerStrings.length; i++) {
      if (!workerStrings[i].isEmpty())
        services.add(docRef.document(workerStrings[i]));
    }
  }

  public boolean fieldsAreNotNull() {
    return this.startTime != null && this.endTime != null && this.user != null && this.slot != null;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public ArrayList<DocumentReference> getServices() {
    return this.services;
  }

  public void setServices(ArrayList<DocumentReference> s) {
    this.services = s;
  }

  /**
   * Create a <code>Booking</code> object to be used in booking slots.
   * 
   * @param startTime Booking check-in time
   * @param endTime   Booking check-out time
   * @param user      The document reference of the user who is booking the slot
   * @param slot      The document reference of the slot which is being booked.
   * @apiNote `slot` means <code>SlotModel</code>
   */
  public Booking(Timestamp startTime, Timestamp endTime, DocumentReference user, DocumentReference slot) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.user = user;
    this.slot = slot;
  }

  /**
   * Default constructor to be used by Firebase to convert database map to a
   * <code>Booking</code> Object.
   * 
   * @deprecated <strong>Do not use this yourself</strong>. Fields are not meant
   *             to be <code>null</code>
   */
  public Booking() {
  }

  /**
   * The ID of the booked Slot. May be <code>null</code> if this object was
   * created by a <code>SlotModel</code>.
   * 
   * @return ID of this booked slot.
   */
  public DocumentReference getSlot() {
    return slot;
  }

  public void setSlot(DocumentReference slot) {
    this.slot = slot;
  }

  /**
   * @param startTime
   * @param endTime
   * @param user
   */
  public Booking(Timestamp startTime, Timestamp endTime, DocumentReference user) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.user = user;
  }

  public DocumentReference getUser() {
    return user;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public Timestamp getEndTime() {
    return endTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(Timestamp endTime) {
    this.endTime = endTime;
  }

  public void setUser(DocumentReference user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "[Booking from " + startTime + " to " + endTime + " by " + (user != null ? user.getId() : "") + "]";
  }

  public String toHTML() {
    Date sd = startTime.toDate();
    return sd.getDate() + "/" + sd.getMonth() + "/" + sd.getYear() + " " + sd.getHours() + ":" + sd.getMinutes();
  }

  /**
   * Get the Map representation in order to update a document in Firebase
   * 
   * @throws IllegalStateException If any fields are <code>null</code>
   */
  public Map<String, Object> toMap() throws IllegalStateException {
    // if (this.user == null || this.startTime == null || this.endTime == null)
    // throw new IllegalStateException("something is null here hehe");
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("startTime", this.startTime);
    map.put("endTime", this.endTime);
    map.put("user", this.user);
    map.put("slot", this.slot);
    map.put("duration", (int) ((this.endTime.getSeconds() - this.startTime.getSeconds()) / 3600));
    map.put("services", this.services);
    map.put("location", this.location);
    return map;
  }
}
