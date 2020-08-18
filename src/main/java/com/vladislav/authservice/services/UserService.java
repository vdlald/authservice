package com.vladislav.authservice.services;

import com.vladislav.authservice.documents.User;
import com.vladislav.authservice.exceptions.UserAuthenticateException;
import com.vladislav.authservice.exceptions.UserExistException;
import com.vladislav.authservice.exceptions.UserNotFoundException;
import com.vladislav.authservice.repositories.UserRepository;
import com.vladislav.authservice.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public User registerNewUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserExistException();
        }

        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(User.Role.USER));

        return userRepository.save(user);
    }

    public String authenticateUser(String username, String password)
            throws UserNotFoundException, UserAuthenticateException {
        final Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (passwordEncoder.encode(password).equals(user.getPassword())) {
                return jwtUtils.createUserJwtToken(user);
            } else {
                throw new UserAuthenticateException(username);
            }
        } else {
            throw new UserNotFoundException(username);
        }
    }

}
