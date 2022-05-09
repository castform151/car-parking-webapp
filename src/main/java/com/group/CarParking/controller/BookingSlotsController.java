package com.group.CarParking.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.group.CarParking.Util.RestService;
import com.group.CarParking.model.Booking;
import com.group.CarParking.service.BookingError;
import com.group.CarParking.service.SlotBookingService;
import com.group.CarParking.service.SlotService;
import com.group.CarParking.service.UserService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/booking")
public class BookingSlotsController {
  @GetMapping(value = "/{slotId}")
  public String getSlotDetails(@PathVariable("slotId") String slotId, HttpServletRequest request,
      Model model)
      throws InterruptedException, ExecutionException {
    var slot = SlotService.getOneParkingSlot(slotId);
    model.addAttribute("slotModel", slot);
    model.addAttribute("slotId", slotId);
    Cookie[] cookies = request.getCookies();
    // if (cookies != null && cookies.length > 0) {
    // for (int i = 0; i < cookies.length; i++) {
    // if (cookies[i].getName().equals("userId")) {
    // var userId = cookies[i].getValue();
    // }
    // }
    // }
    String userId = null;
    if (cookies != null)
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("userId")) {
          userId = cookies[i].getValue();
        }
        System.out.println(cookies[i].getName() + "=" + cookies[i].getValue());
      }
    model.addAttribute("userId", userId);
    boolean promoCode = false;
    if (userId != null) {
      var userModel = UserService.getUserDetails(userId);
      if (userModel.getCurrentBookingReferences() != null && userModel.getCurrentBookingReferences().size() > 5) {
        promoCode = true;
      }
    }
    if (promoCode) {
      model.addAttribute("promoCode", "QWDASC");
    }
    return "slot-details";
  }

  /*
   * @PostMapping(consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
   * MediaType.APPLICATION_XML_VALUE,
   * MediaType.APPLICATION_JSON_VALUE })
   * 
   * @ResponseBody
   * public String register(@RequestBody RegisterData registerData) {
   */
  @GetMapping(value = "/{slotId}/{userId}"/*
                                           * , consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                                           * MediaType.APPLICATION_XML_VALUE,
                                           * MediaType.APPLICATION_JSON_VALUE }
                                           */)
  public String bookSlot(@PathVariable("slotId") String slotId, @PathVariable("userId") String userId,
      @RequestParam(required = false, defaultValue = "") String ws,
      @RequestParam(required = false, defaultValue = "13/11/21") String date,
      @RequestParam String location,
      @RequestParam(required = true) String startTime,
      @RequestParam(required = false, defaultValue = "1") Integer duration, Model model)
      throws ParseException, IllegalStateException, BookingError, InterruptedException, ExecutionException {
    DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    Date startDate = df.parse(date + " " + startTime);
    Timestamp timestamp = Timestamp.of(startDate);
    DocumentReference slot = SlotService.db.collection(SlotService.COLLECTION_NAME).document(slotId);
    DocumentReference user = SlotService.db.collection("users").document(userId);
    long milliseconds = timestamp.getSeconds() * 1000;
    milliseconds += duration * 3600000;
    Timestamp endTimestamp = Timestamp.of(new java.util.Date(milliseconds));
    Booking booking = new Booking(timestamp, endTimestamp, user, slot);
    booking.setLocation(location);
    // Get the workers
    String[] workers = ws.split(",");
    System.out.println(ws + Arrays.toString(workers));
    booking.setWorkers(workers);
    // try to book user
    try {
      SlotBookingService.bookUserForSlot(userId, slotId, booking);
      model.addAttribute("bookingDone", true);
      var future = user.get();
      var res = future.get();
      System.out.println("Datataaatata" + res.getData());
      if (res.getData().get("email") != null)
        RestService.sendEmail((String) res.getData().get("email"), slotId);
    } /* else enrol in waiting list */catch (BookingError bookingError) {
      booking.setSlot(slot);
      SlotBookingService.enrolUserInWaitingList(userId, slotId, booking);
      model.addAttribute("enrolledInWaitingList", true);
    } finally {
      // to display waiting list and bookings of the user in the dashboard
      model.addAttribute("bookings", UserService.getUserBookings(userId));
      model.addAttribute("waitingList", UserService.getUserWaitingList(userId));
    }
    return "redirect:/dashboard";
  }
}
