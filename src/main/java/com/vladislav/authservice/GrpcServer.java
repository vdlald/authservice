package com.vladislav.authservice;

import com.vladislav.authservice.manufacture.annotations.InjectLogger;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GrpcServer implements ApplicationRunner {

    @InjectLogger
    private Logger logger;

    @Value("${app.grpc.server.port}")
    private Integer port;

    private final List<BindableService> services;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting gRPC server...");

        final Server server = createServer();

        server.start();
        logger.info("gRPC server started");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Received shutdown request");
            server.shutdown();
            logger.info("Successfully stopped the server");
        }));
        server.awaitTermination();
    }

    private Server createServer() {
        final ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        for (BindableService service : services) {
            serverBuilder.addService(service);
        }
        return serverBuilder.build();
    }
}
