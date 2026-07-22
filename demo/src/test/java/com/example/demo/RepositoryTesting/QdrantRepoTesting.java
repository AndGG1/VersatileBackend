//package com.example.demo.RepositoryTesting;
//
//import com.example.demo.QdrantContainerConfig;
//import com.example.demo.vectorDb.structure.QdrantRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
////Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.
////Informative Comment: This is an integration Test.
//
//@SpringBootTest
//@Testcontainers
//public class QdrantRepoTesting extends QdrantContainerConfig {
//    @Autowired
//    private QdrantRepository repository;
//
//    @Test
//    public void test_Save() {
//        //Initial Config.
//        repository.deleteAll()
//    }
//}