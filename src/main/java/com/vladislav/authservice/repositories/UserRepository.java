package com.vladislav.authservice.repositories;

import com.vladislav.authservice.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByRefreshTokens_RefreshToken(UUID refreshToken);
}
