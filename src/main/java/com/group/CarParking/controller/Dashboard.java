package com.group.CarParking.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.cloud.Timestamp;
import com.group.CarParking.model.SlotModel;
import com.group.CarParking.service.SlotService;
import com.group.CarParking.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class Dashboard {
  // @GetMapping("/mail")
  // @ResponseBody
  // public String mail() throws MessagingException, IOException {
  // // MailSender.sendmail();
  // return "sent email";
  // }

  @GetMapping
  public String dashboString(HttpServletRequest request, Model model) throws InterruptedException, ExecutionException {
    try {
      var cookies = request.getCookies();
      String userId = null;
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("userId")) {
          userId = cookies[i].getValue();
        }
      }
      System.out.println("User id is cookie=" + userId);
      if (userId != null) {
        var b = UserService.getUserBookings(userId);
        var w = UserService.getUserWaitingList(userId);
        System.out.println(b);
        System.out.println(w);
        System.out.println("Added things");
        model.addAttribute("waitingList", w.get("bookings"));
        model.addAttribute("bookings", b.get("bookings"));
        model.addAttribute("userId", userId);
      }
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    } finally {
      var slots = SlotService.getSlots();
      model.addAttribute("slotModels", slots);
    }
    return "dashboard";
  }

  @GetMapping("/search")
  public String search(@RequestParam(required = false) String date, @RequestParam String location,
      @RequestParam(required = false) String startTime,
      @RequestParam(required = false, defaultValue = "1") Integer duration, Model model)
      throws InterruptedException, ExecutionException, ParseException {
    System.out.println("date=" + date + " startTime=" + startTime + "duration=" + duration);
    List<SlotModel> slots;
    // Fix blank list error
    if (date.isEmpty()) {
      slots = SlotService.getSlotsWithLocation(location);
    } else if (startTime.isEmpty()) {
      // Time parsing
      DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
      Date startDate = df.parse(date);
      slots = SlotService.getSlotsWithDate(location, startDate);
    } else {
      DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
      Date startDate = df.parse(date + " " + startTime);
      Timestamp timestamp = Timestamp.of(startDate);
      slots = SlotService.getSlotsWithDateAndTime(location, startDate, timestamp, duration);
    }

    model.addAttribute("slotModels", slots);
    return "dashboard";
  }

  // TODO Slot details page controller
  @GetMapping("/slot/{slotId}")
  public String details(@PathVariable String slotId, HttpServletRequest request, Model model)
      throws InterruptedException, ExecutionException {
    Cookie[] cookies = request.getCookies();
    String userId = null;
    if (cookies != null)
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("userId")) {
          userId = cookies[i].getValue();
        }
        System.out.println(cookies[i].getName() + "=" + cookies[i].getValue());
      }
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
    var slotModel = SlotService.getOneParkingSlot(slotId);
    model.addAttribute("slotModel", slotModel);
    return "slot-details";
  }
}
