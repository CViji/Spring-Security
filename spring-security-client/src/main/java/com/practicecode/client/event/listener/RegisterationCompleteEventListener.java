package com.practicecode.client.event.listener;

import com.practicecode.client.entity.User;
import com.practicecode.client.event.RegisterationCompleteEvent;
import com.practicecode.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.UUID;

@Slf4j
public class RegisterationCompleteEventListener implements ApplicationListener<RegisterationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegisterationCompleteEvent event) {
        // Create the verification token for the User with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);

        // Send the mail to the User
        String url = event.getApplicationUrl()
                + "verifyRegisteration?token="
                + token;

        // Send VerficationEmail()
        log.info("Click the link to verfy your account: {}", url);
    }
}
