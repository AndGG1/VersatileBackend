package com.example.demo.ControllerTesting;

import com.example.demo.buisnessUsage.embeddings.structure.EmbedResponse;
import com.example.demo.buisnessUsage.embeddings.structure.EmbeddingController;
import com.example.demo.buisnessUsage.embeddings.structure.EmbeddingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@WebMvcTest(controllers = EmbeddingController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class EmbeddingControllerTesting {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmbeddingService embeddingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_POSTMethod_PositiveCase() throws Exception {
        //Arrange
        List<String> inputs = List.of("sentence_1", "sentence_2");
        List<List<Double>> embeddings = List.of(List.of(.1, .1), List.of(.2, .2));

        Mockito.when(embeddingService.embed(Mockito.any(List.class))).thenReturn(new EmbedResponse(embeddings));
        Map<String, Object> mappedRequestBody = Map.of("inputs", inputs);

        //Act
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/versatile_api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mappedRequestBody)));

        //Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void test_POSTMethod_NegativeCase() throws Exception {
        //Arrange
        List<String> inputs = List.of("sentence_1", "sentence_2");

        Mockito.when(embeddingService.embed(Mockito.any(List.class))).thenReturn(new EmbedResponse(List.of()));
        Map<String, Object> mappedRequestBody = Map.of("inputs", inputs);

        //Act
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/versatile_api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mappedRequestBody)));

        //Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void test_POSTMethod_ErrorCase() {
        //Arrange
        Mockito.when(embeddingService.embed(Mockito.any(List.class))).thenCallRealMethod();

        //Act & Assert
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/versatile_api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "  \"inputs\": [\n" +
                            "    \n" +
                            "  ]\n" +
                            "}"));
        });
    }
}
