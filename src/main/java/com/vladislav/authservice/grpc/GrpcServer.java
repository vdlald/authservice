package com.vladislav.authservice.grpc;

import io.grpc.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GrpcServer implements ApplicationRunner {

    private final Server server;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
        server.awaitTermination();
    }

    private void startServer() throws IOException {
        log.info("Starting gRPC server...");
        server.start();
        log.info("gRPC server started");
    }

    private void stopServer() {
        log.info("Received shutdown request");
        server.shutdown();
        log.info("Successfully stopped the server");
    }
}
