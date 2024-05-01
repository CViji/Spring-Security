package com.practicecode.client.controller;

import com.practicecode.client.entity.User;
import com.practicecode.client.event.RegisterationCompleteEvent;
import com.practicecode.client.model.UserModel;
import com.practicecode.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping()
public class RegisterationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegisterationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegisteration")
    public String verifyRegisteration(@RequestParam("token") String token)
    {
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid"))
        {
            return "User Verified Successfully";
        }
        else
        {
            return "Bad User/Token Expired";
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":"+
                request.getServerPort() +
                request.getContextPath();
    }
}
