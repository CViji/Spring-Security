package com.practicecode.client.event;

import com.practicecode.client.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@Getter
@Setter
public class RegisterationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public RegisterationCompleteEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
