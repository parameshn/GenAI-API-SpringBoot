package com.example.genaiapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateContentResponse {
    private List<Candidate> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Candidate {
        private Content content;

        @JsonProperty("finishReason")
        private String finishReason;

        private Integer index;

        @JsonProperty("safetyRatings")
        private List<SafetyRating> safetyRatings;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;

        @JsonProperty("functionCall") 
        private FunctionCall functionCall;
        

        @JsonProperty("executableCode")
        private ExecutableCode executableCode;

        @JsonProperty("codeExecutionResult")
        private CodeExecutionResult codeExecutionResult;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionCall {
        private String name;
        private Map<String, Object> args;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutableCode {
        private String language;
        private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeExecutionResult {
        private String outcome;
        private String output;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageMetadata {
        @JsonProperty("promptTokenCount")
        private Integer promptTokenCount;

        @JsonProperty("candidatesTokenCount")
        private Integer candidatesTokenCount;

        @JsonProperty("totalTokenCount")
        private Integer totalTokenCount;

    }
}
