package com.vladislav.authservice.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(collection = "users")
public class User {

    @MongoId
    private UUID id = UUID.randomUUID();

    @Indexed(unique = true)
    private String username;

    private String password;

    private List<Role> roles = new ArrayList<>(1) {{
        add(Role.USER);
    }};

    private List<RefreshToken> refreshTokens = new ArrayList<>();

    public enum Role {
        USER,
        ADMIN
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class RefreshToken {
        private ObjectId id = ObjectId.get();
        private UUID refreshToken = UUID.randomUUID();
        private LocalDateTime createdAt = LocalDateTime.now();
    }
}
