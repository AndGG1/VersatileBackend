package com.example.demo.ServiceTesting;

import com.example.demo.buisnessUsage.embeddings.structure.EmbedResponse;
import com.example.demo.buisnessUsage.embeddings.structure.EmbeddingService;
import com.example.demo.buisnessUsage.embeddings.errorHandlers.ServiceResponseErrorHandlerKt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@RestClientTest(EmbeddingService.class)
@Import(ServiceResponseErrorHandlerKt.class)
public class EmbeddingServiceTesting {
    @Autowired
    private EmbeddingService service;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private final String requestUri =
            "https://router.huggingface.co/hf-inference/models/dumitrescustefan/bert-base-romanian-cased-v1/pipeline/feature-extraction";

    @Test
    public void test_EmbedWords_PositiveCase() throws JsonProcessingException {
        //Arrange
        List<String> inputs = List.of(
                "sentence_1",
                "sentence_2",
                "sentence_3"
        );

        List<List<List<Double>>> response = List.of(
                List.of(List.of(.1, .1)),
                List.of(List.of(.2, .2)),
                List.of(List.of(.3, .3))

        );
        final String jsonResponseFormat = objectMapper.writeValueAsString(response);

        //Act
        server.expect(
                requestTo(requestUri)
        ).andRespond(withSuccess(jsonResponseFormat, MediaType.APPLICATION_JSON));

        EmbedResponse result = service.embed(inputs);

        //Arrange
        server.verify();
        assertNotNull(result);
        assertEquals(3, result.getEmbeddings().size());
    }

    @Test
    public void test_EmbedWords_NegativeCase1() throws JsonProcessingException {
        //Arrange
        List<String> inputs = List.of();
        List<List<List<Double>>> response = List.of();

        final String jsonResponseFormat = objectMapper.writeValueAsString(response);

        //Act
        server.expect(
                requestTo(requestUri)
        ).andRespond(withSuccess(jsonResponseFormat, MediaType.APPLICATION_JSON));

        EmbedResponse result = service.embed(inputs);

        //Arrange
        server.verify();
        assertNotNull(result);
        assertEquals(0, result.getEmbeddings().size());

        assertTrue(result.getEmbeddings().isEmpty());
        assertEquals(0, result.getEmbeddings().size());
    }

    @Test
    public void test_EmbedWords_NegativeCase2() throws JsonProcessingException {
        //Arrange
        List<String> inputs = List.of("valid_sentence", "invalid_sentence");
        List<List<List<Double>>> response = List.of(
                List.of(List.of(.1, .1)),
                List.of()
        );

        final String jsonResponseFormat = objectMapper.writeValueAsString(response);

        //Act
        server.expect(
                requestTo(requestUri)
        ).andRespond(withSuccess(jsonResponseFormat, MediaType.APPLICATION_JSON));

        EmbedResponse result = service.embed(inputs);

        //Arrange
        server.verify();
        assertNotNull(result);
        assertEquals(2, result.getEmbeddings().size());

        assertEquals(List.of(.1, .1), result.getEmbeddings().get(0));
        assertEquals(List.of(), result.getEmbeddings().get(1));
        assertEquals(0, result.getEmbeddings().get(1).size());
    }

    @Test
    public void test_EmbedWords_NegativeCase3() throws JsonProcessingException {
        //Arrange
        List<String> inputs = List.of("example_sentence");

        //Act
        server.expect(
                requestTo(requestUri)
        ).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        EmbedResponse result = service.embed(inputs);

        //Arrange
        server.verify();
        assertNotNull(result);
        assertEquals(0, result.getEmbeddings().size());
    }
}
