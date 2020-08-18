package com.vladislav.authservice.services;

import com.proto.auth.*;
import com.vladislav.authservice.documents.User;
import com.vladislav.authservice.repositories.UserRepository;
import com.vladislav.authservice.utils.jwt.JwtUtils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {
        final String username = request.getUsername().toLowerCase();
        final User user = new User()
                .setId(UUID.randomUUID())
                .setUsername(username)
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setRoles(List.of(User.Role.USER));

        if (userRepository.findByUsername(username).isPresent()) {
            final StatusRuntimeException exception = Status.ALREADY_EXISTS
                    .withDescription("User already exist.")
                    .asRuntimeException();
            responseObserver.onError(exception);
            return;
        }

        final User savedUser = userRepository.save(user);

        final RegisterUserResponse response = RegisterUserResponse.newBuilder()
                .setUserId(savedUser.getId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void authenticateUser(AuthenticateUserRequest request, StreamObserver<AuthenticateUserResponse> responseObserver) {
        final String username = request.getUsername().toLowerCase();
        final String password = request.getPassword();

        final Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                final String jwt = jwtUtils.createUserJwt(user);
                final AuthenticateUserResponse response = AuthenticateUserResponse.newBuilder().setJwt(jwt).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                final StatusRuntimeException exception = Status.UNAUTHENTICATED
                        .withDescription(String.format("Failed authenticate user with username: %s", username))
                        .asRuntimeException();
                responseObserver.onError(exception);
            }
        } else {
            final StatusRuntimeException exception = Status.NOT_FOUND
                    .withDescription(String.format("User with username: %s not found", username))
                    .asRuntimeException();
            responseObserver.onError(exception);
        }
    }
}
