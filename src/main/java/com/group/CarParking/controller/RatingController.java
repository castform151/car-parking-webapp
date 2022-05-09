package com.group.CarParking.controller;

import java.util.concurrent.ExecutionException;

import com.group.CarParking.model.SlotModel;
import com.group.CarParking.service.SlotService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rating")
public class RatingController {
  @GetMapping
  public String form(Model model, @RequestParam("id") String slotId) {
    model.addAttribute("slotId", slotId);
    return "rating";
  }

  @GetMapping("/{slotId}")
  public String rate(@PathVariable("slotId") String slotId, @RequestParam("st") String stars,
      @RequestParam("fd") String feedback) throws InterruptedException, ExecutionException {
    SlotModel slotModel = SlotService.getOneParkingSlot(slotId);
    slotModel.setRatings(Double.parseDouble(stars));
    SlotService.updateSlot(slotId, slotModel);
    // var g = SlotService.db.collection("feedback")
    return "success-rating";
  }
}
