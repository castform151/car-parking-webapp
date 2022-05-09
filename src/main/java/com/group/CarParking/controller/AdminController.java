package com.group.CarParking.controller;

import com.group.CarParking.service.WorkersService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.group.CarParking.model.SlotModel;
import com.group.CarParking.model.Worker;
import com.group.CarParking.service.SlotService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
  @GetMapping
  public String homeAdmin(Model model) {
    return "admin";
  }

  // DONE Book slot function post method
  // TODO Booked Slots view on slot details page
  // TODO waiting list alert
  // DONE Create enrol to waiting list controller
  // TODO Worker-service UI
  // TODO Almost done Landing page animation
  @GetMapping("/slots")
  public String getSlots(Model model) throws InterruptedException, ExecutionException {
    var slots = SlotService.getSlots();
    for (SlotModel slotModel : slots) {
      if (!slotModel.getWorkers().isEmpty()) {
        ArrayList<Worker> workers = new ArrayList<>();
        for (DocumentReference workerRef : slotModel.getWorkers()) {
          var future = workerRef.get();
          var document = future.get();
          var w = document.toObject(Worker.class);
          w.setId(document.getId());
          workers.add(w);
        }
        slotModel.setWorkersModelList(workers);
      }
    }
    model.addAttribute("slotModels", slots);
    var w = WorkersService.getWorkers();
    model.addAttribute("workers", w);
    return "slots";
  }

  @GetMapping("/workers")
  public String w(Model model) throws InterruptedException, ExecutionException {
    var w = WorkersService.getWorkers();
    model.addAttribute("workers", w);
    return "workers";
  }

  @GetMapping("/workers/create")
  @ResponseBody
  public String cw(@RequestParam("name") String name, @RequestParam("services") String services,
      @RequestParam("cost") String cost) throws InterruptedException, ExecutionException {
    int costInt = Integer.parseInt(cost);
    Worker worker = new Worker(name, List.of(services.split(",")), costInt);
    WorkersService.addWorker(worker);
    return "created";
  }

  @GetMapping("/workers/edit/{id}")
  @ResponseBody
  public String editE(@PathVariable("id") String wId, @RequestParam("name") String name,
      @RequestParam("services") String services,
      @RequestParam("cost") String cost) throws InterruptedException, ExecutionException {
    int costInt = Integer.parseInt(cost);
    Worker worker = new Worker(name, List.of(services.split(",")), costInt);
    WorkersService.updateWorker(wId, worker);
    return "edited";
  }

  @GetMapping("/workers/remove/{id}")
  @ResponseBody
  public String reW(@PathVariable("id") String id) {
    WorkersService.deleteW(id);
    return "deleted";
  }

  @GetMapping("/slots/remove/{id}")
  @ResponseBody
  public String reS(@PathVariable("id") String id) {
    SlotService.deleteSlot(id);
    return "deleted";
  }

  @GetMapping("/slots/edit/{id}")
  @ResponseBody
  public String editC(@PathVariable("id") String slotId, @RequestParam("startDate") String startDateStr,
      @RequestParam("location") String location,
      @RequestParam("endDate") String endDateStr) throws ParseException, InterruptedException, ExecutionException {
    DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    Date date = df.parse(startDateStr + " 00:00 AM");
    Timestamp startTS = Timestamp.of(date);
    SlotModel sModel = new SlotModel();
    sModel.setLocation(location);
    sModel.setStartDate(startTS);
    date = df.parse(endDateStr + " 00:00 AM");
    Timestamp endTS = Timestamp.of(date);
    sModel.setEndDate(endTS);
    SlotService.updateSlot(slotId, sModel);
    return "edited";
  }

  @GetMapping("/create")
  @ResponseBody
  public String createSlot(@RequestParam("startDate") String startDateStr, @RequestParam("location") String location,
      @RequestParam("endDate") String endDateStr, @RequestParam("workers") String workers)
      throws ParseException, InterruptedException, ExecutionException {
    DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    Date date = df.parse(startDateStr + " 00:00 AM");
    Timestamp startTS = Timestamp.of(date);
    SlotModel sModel = new SlotModel();
    sModel.setLocation(location);
    sModel.setStartDate(startTS);
    sModel.setWorkers(
        Arrays.asList(workers.split(",")).stream().map(w -> SlotService.db.collection("workers").document(w)).toList());
    date = df.parse(endDateStr + " 00:00 AM");
    Timestamp endTS = Timestamp.of(date);
    sModel.setEndDate(endTS);
    SlotService.createSlot(sModel);
    return "hey";
  }
}
