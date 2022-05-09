package com.group.CarParking.controller;

import java.util.ArrayList;
import java.util.List;

import com.group.CarParking.model.SlotModel;
import com.group.CarParking.model.UserModel;
import com.group.CarParking.service.SlotService;
import com.group.CarParking.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    @GetMapping("/confirm-email")
    @ResponseBody
    public String confirm() {
        return "Please confirm your email first.";
    }

    static String getContent() {
        return "Some content here";
    }

    @GetMapping
    public String welcome(@RequestParam(required=false, defaultValue = "false") String logout, Model model) {
        // String data = "Thymeleaf test";
        // String content = getContent();
        // model.addAttribute("title", data);
        // model.addAttribute("content", content);
        model.addAttribute("logout", logout);
        return "register1";
        // return "register";
    }

    @GetMapping("/users")
    @ResponseBody
    public String hehe() {
        try {
            return SlotService.getSlots().get(0).getLocation();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public String register(@RequestBody RegisterData registerData) {
        // String email = registerData.get("email");
        // String password = registerData.get("password").toString();
        // String name = registerData.get("name").toString();
        // String email = registerData.getEmail();
        // String name = registerData.getName();
        // String password = registerData.getPassword();
        // String address = registerData.getAddress();
        String res = UserService.createUser(UserModel.fromRegister(registerData));
        // model.addAttribute("slotList", list<SlotModel>);
        return res;
    }
}
