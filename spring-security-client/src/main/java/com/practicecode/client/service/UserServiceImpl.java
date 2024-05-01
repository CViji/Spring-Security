package com.practicecode.client.service;

import com.practicecode.client.entity.User;
import com.practicecode.client.entity.VerficationToken;
import com.practicecode.client.model.UserModel;
import com.practicecode.client.repository.UserRepository;
import com.practicecode.client.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerficationToken verficationToken = new VerficationToken(user,token);
        verificationTokenRepository.save(verficationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerficationToken verficationToken = verificationTokenRepository.findByToken(token);
        if (verficationToken == null) {
            return "invalid";
        }

        User user = verficationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (verficationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0)
        {
            verificationTokenRepository.delete(verficationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerficationToken generateNewVerificationToken(String oldToken) {
        VerficationToken verficationToken = verificationTokenRepository.findByToken(oldToken);
        verficationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verficationToken);
        return verficationToken;
    }

}
