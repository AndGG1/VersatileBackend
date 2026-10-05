package com.example.demo.ServiceTesting;

import com.example.demo.buisnessUsage.vectorDb.structure.QdrantRepository;
import com.example.demo.buisnessUsage.vectorDb.structure.QdrantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QdrantServiceTesting {
    @Mock
    private QdrantRepository qdrantRepository;
    @InjectMocks
    private QdrantService qdrantService;

    @Test
    public void testUpsert_Success() {

    }
}