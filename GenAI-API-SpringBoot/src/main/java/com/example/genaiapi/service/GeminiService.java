package com.example.genaiapi.service;


import com.example.genaiapi.config.GeminiConfig;
import com.example.gemini.model.GenerateContentRequest;
import com.example.gemini.model.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {
    

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GeminiConfig geminiConfig;

    private static final String MODEL_NAME = "gemini-2.0-flash";

    /**
     * Basic text generation
     */
    public GenerateContentResponse generateText(String prompt) {
        String url = buildUrl("generateContent");
        /*user defined */

        GenerateContentRequest request = new GenerateContentRequest();
        request.setContents(
                Listof(new GenerateContentRequest.Content("user", List.of(new GenerateContentRequest.Part(prompt)))));

        return makeRequest(url, request);
        
    }
    
    /**
     * Text generation with system instructions
     */
    public GenerateContentResponse generateTextWithSystemInstruction(String prompt, String systemInstruction) {
        String url = buildUrl("generateContent");

        GenerateContentRequest request = new GenerateContentRequest();
        request.setContents(
                List.of(new GenerateContentRequest.Content("user", List.of(new GenerateContentRequest.Part(prompt)))));
        return makeRequest(url, request);
    }


    private String buildUrl(String endpoint) {
        return String.format("%s/model/%s:%s?key=%s", geminiConfig.getBaseUrl(), MODEL_NAME, endpoint,
                geminiConfig.getApiKey());
    }

    

    
     
}
