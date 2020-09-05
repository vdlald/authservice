package com.vladislav.authservice.services;

import com.proto.auth.*;
import com.vladislav.authservice.documents.User;
import com.vladislav.authservice.exceptions.RefreshTokenExpired;
import com.vladislav.authservice.repositories.UserRepository;
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
    private final JwtService jwtService;

    @Override
    public void registerUser(
            UserRegistrationRequest request, StreamObserver<UserRegistrationResponse> responseObserver
    ) {
        final String username = request.getUsername().toLowerCase();
        final User user = new User()
                .setUsername(username)
                .setPassword(passwordEncoder.encode(request.getPassword()));

        if (userRepository.findByUsername(username).isPresent()) {
            final StatusRuntimeException exception = Status.ALREADY_EXISTS
                    .withDescription("User already exist.")
                    .asRuntimeException();
            responseObserver.onError(exception);
            return;
        }

        final User savedUser = userRepository.save(user);

        final UserRegistrationResponse response = UserRegistrationResponse.newBuilder()
                .setUserId(savedUser.getId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void authenticateUser(
            UserAuthenticationRequest request, StreamObserver<UserAuthenticationResponse> responseObserver
    ) {
        final String username = request.getUsername().toLowerCase();
        final String password = request.getPassword();

        final Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                final String jwt = jwtService.createUserAccessJwt(user);
                final User.RefreshToken userRefreshToken = jwtService.createUserRefreshToken();

                user.getRefreshTokens().add(userRefreshToken);
                userRepository.save(user);

                final UserAuthenticationResponse response = UserAuthenticationResponse.newBuilder()
                        .setJwt(jwt)
                        .setRefreshToken(userRefreshToken.getRefreshToken().toString())
                        .build();

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

    @Override
    public void refreshSession(RefreshSessionRequest request, StreamObserver<RefreshSessionResponse> responseObserver) {
        final UUID refreshToken = UUID.fromString(request.getRefreshToken());
        final Optional<User> optionalUser = userRepository.findByRefreshTokens_RefreshToken(refreshToken);

        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            final List<User.RefreshToken> refreshTokens = user.getRefreshTokens();
            for (User.RefreshToken token : refreshTokens) {
                if (token.getRefreshToken().equals(refreshToken)) {
                    try {
                        jwtService.updateRefreshToken(token);
                        userRepository.save(user);

                        responseObserver.onNext(RefreshSessionResponse.newBuilder()
                                .setRefreshToken(token.getRefreshToken().toString())
                                .setJwt(jwtService.createUserAccessJwt(user))
                                .build());
                        responseObserver.onCompleted();
                    } catch (RefreshTokenExpired e) {
                        responseObserver.onError(Status.UNAUTHENTICATED
                                .withDescription(e.getMessage())
                                .asRuntimeException());
                    }
                    break;
                }
            }
        } else {
            responseObserver.onError(Status.UNAUTHENTICATED.asRuntimeException());
        }
    }
}
