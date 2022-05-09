package com.group.CarParking.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.group.CarParking.controller.RegisterData;
import com.group.CarParking.service.UserService;

public class UserModel {
    public String getEmail() {
        return email;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public List<Booking> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<Booking> waitingList) {
        this.waitingList = waitingList;
    }

    public List<Booking> getCurrentBookingReferences() {
        return currentBookingReferences;
    }

    public void setCurrentBookingReferences(List<Booking> currentBookingReferences) {
        this.currentBookingReferences = currentBookingReferences;
    }

    public void addToBookings(Booking booking) {
        ArrayList<Booking> list = new ArrayList<Booking>();
        list.addAll(this.currentBookingReferences);
        System.out.println("booking = " + booking);
        list.add(booking);
        this.currentBookingReferences = list;
    }

    public List<DocumentReference> getSlots() {
        return slots;
    }

    public void setSlots(List<DocumentReference> slots) {
        this.slots = slots;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String a) {
        this.address = a;
    }

    public String getAddress() {
        return this.address;
    }

    private String email, name, uid, password, address, phoneNumber, carnum;
    private double credits;
    private List<Booking> currentBookingReferences;
    private List<Booking> waitingList;
    private List<DocumentReference> slots;

    public UserModel() {
    }

    public UserModel(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
    }

    // factory
    /**
     * @param email
     * @param name
     * @param password
     * @return
     */
    public static UserModel fromRegister(RegisterData rData) {
        UserModel userModel = new UserModel(rData.getEmail(), rData.getName(), "");
        userModel.setPassword(rData.getPassword());
        userModel.setAddress(rData.getAddress());
        userModel.setPhoneNumber(rData.getMnumber());
        userModel.setCarnum(rData.getCarnum());
        return userModel;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", this.email);
        map.put("password", this.password);
        map.put("currentBookingReferences",
                this.currentBookingReferences.stream().map(booking -> booking.toMap()).toList());
        map.put("waitingList", this.waitingList.stream().map(booking -> booking.toMap()).toList());
        map.put("address", this.address);
        map.put("phoneNumber", this.phoneNumber);
        return map;
    }

    public boolean update(String id) throws InterruptedException, ExecutionException {
        return UserService.updateUser(id, this);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
