package com.vladislav.authservice.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GrpcConfiguration {

    @Bean
    Server server(
            @Value("${app.grpc.server.port}") Integer port,
            @Autowired(required = false) List<BindableService> services
    ) {
        final ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        if (services != null) {
            services.forEach(serverBuilder::addService);
        }
        return serverBuilder.build();
    }
}
