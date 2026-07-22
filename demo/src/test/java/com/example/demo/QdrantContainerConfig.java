package com.example.demo;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.qdrant.QdrantContainer;

public abstract class QdrantContainerConfig {
    @Container
    @ServiceConnection
    public static final QdrantContainer databaseConn = new QdrantContainer("qdrant/qdrant:latest");
}
