package com.practicecode.client.controller;

import com.practicecode.client.entity.PasswordModel;
import com.practicecode.client.entity.User;
import com.practicecode.client.entity.VerficationToken;
import com.practicecode.client.event.RegisterationCompleteEvent;
import com.practicecode.client.model.UserModel;
import com.practicecode.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping()
@Slf4j
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
            return "Bad User";
        }
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerficationToken(@RequestParam("token") String oldToken, HttpServletRequest request)
    {
        VerficationToken verficationToken = userService.generateNewVerificationToken(oldToken);
        User user = verficationToken.getUser();
        resendVerficationTokenMail(user, applicationUrl(request), verficationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request)
    {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user!=null)
        {
            String token = UUID.randomUUID().toString();
            userService.createPasswordRestTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel)
    {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid"))
        {
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent())
        {
            userService.changePassword(user.get(),passwordModel.getNewPassword());
            return "Password Reset Successfully";
        }
        else {
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel)
    {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if(user != null)
        {
            if(!userService.checkIfValidPassword(user, passwordModel.getOldPassword()))
            {
                return "Invalid Old Password";
            }
            userService.changePassword(user, passwordModel.getNewPassword());
            return "Password Changed Successfully";
        }
        return "Invalid User/ User Not Exists";

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl
                + "/savePassword?token="
                + token;

        log.info("Click the link to Reset your Password: {}", url);
        return url;
    }

    private void resendVerficationTokenMail(User user, String applicationUrl, VerficationToken verficationToken) {
        String url = applicationUrl
                + "/verifyRegisteration?token="
                + verficationToken.getToken();

        log.info("Click the link to verify your account: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":"+
                request.getServerPort() +
                request.getContextPath();
    }
}
