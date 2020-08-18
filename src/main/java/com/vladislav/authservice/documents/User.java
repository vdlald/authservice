package com.vladislav.authservice.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(collection = "users")
public class User {

    @MongoId
    private UUID id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private List<Role> roles;

    public enum Role {
        USER,
        ADMIN
    }

}
