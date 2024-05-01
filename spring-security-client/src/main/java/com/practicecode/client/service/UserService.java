package com.practicecode.client.service;

import com.practicecode.client.entity.User;
import com.practicecode.client.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);
}
