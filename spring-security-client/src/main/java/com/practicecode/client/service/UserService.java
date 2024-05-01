package com.practicecode.client.service;

import com.practicecode.client.entity.User;
import com.practicecode.client.entity.VerficationToken;
import com.practicecode.client.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerficationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordRestTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);
}
