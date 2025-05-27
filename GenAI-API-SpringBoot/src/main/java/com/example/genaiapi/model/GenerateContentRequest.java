package com.example.genaiapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateContentRequest {

    private List<Content> contents;

    @JsonProperty("generationConfig")
    private GenerationConfig generationConfig;

    @JsonProperty("systemInstruction")
    private SystemInstruction systemInstruction;

    private List<Tool> tools;

    // Custom constructor for contents-only initialization
    public GenerateContentRequest(List<Content> contents) {
        this.contents = contents;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        private String text;

        @JsonProperty("inlineData")
        private InlineData inlineData;

        // Custom constructor for text-only initialization
        public Part(String text) {
            this.text = text;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InlineData {
        @JsonProperty("mime_type")
        private String mimeType;
        private String data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenerationConfig {
        private Double temperature;

        @JsonProperty("maxOutputTokens")
        private Integer maxOutputTokens;

        @JsonProperty("topP")
        private Double topP;

        @JsonProperty("topK")
        private Integer topK;

        @JsonProperty("stopSequences")
        private List<String> stopSequences;

        @JsonProperty("responseMimeType")
        private String responseMimeType;

        @JsonProperty("responseSchema")
        private Map<String, Object> responseSchema;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemInstruction {
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Tool {
        @JsonProperty("functionDeclarations")
        private List<FunctionDeclaration> functionDeclarations;

        @JsonProperty("codeExecution")
        private Map<String, Object> codeExecution;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FunctionDeclaration {
        private String name;
        private String description;
        private Map<String, Object> parameters;
    }
}

/*
 * what @JsonProperty does and why it's used
 * 
 * The primary purpose of @JsonProperty is to control the mapping between a Java
 * field name and its corresponding JSON property name during serialization
 * (Java object to JSON) and deserialization (JSON to Java object).
 * 
 * Why it's used:
 * 
 * Different Naming Conventions: Java typically uses camelCase for field names
 * (e.g., maxOutputTokens). JSON often uses camelCase too, but sometimes it uses
 * snake_case (e.g., mime_type) or other conventions, especially when
 * interacting with external APIs that have their own naming
 * standards. @JsonProperty allows you to reconcile these differences without
 * changing your Java field names.
 * 
 * Example from your code:
 * Java
 * 
 * @JsonProperty("mime_type")
 * private String mimeType; // In JSON, this will be "mime_type", not "mimeType"
 * And:
 * Java
 * 
 * @JsonProperty("maxOutputTokens")
 * private Integer maxOutputTokens; // In JSON, this will be "maxOutputTokens",
 * explicitly defined
 * Explicit Naming: Even if the Java field name and the desired JSON property
 * name are the same, using @JsonProperty can sometimes make the mapping
 * explicit, which can be helpful for clarity or when dealing with complex
 * inheritance or polymorphism.
 * 
 * Read-Only/Write-Only Properties: You can configure @JsonProperty to control
 * whether a property is included during serialization (Access.WRITE_ONLY) or
 * deserialization (Access.READ_ONLY), or both.
 * 
 * How it works (in your GenerateContentRequest example):
 * 
 * When Jackson processes your GenerateContentRequest object to convert it into
 * a JSON string to be sent to the Gemini API, it looks for @JsonProperty
 * annotations.
 * 
 * If a field has @JsonProperty("someName"), Jackson will use "someName" as the
 * key in the JSON output, instead of the actual Java field name.
 * If a field doesn't have @JsonProperty, Jackson will default to using the Java
 * field name as the JSON property name.
 * Similarly, when Jackson receives a JSON string from the Gemini API and tries
 * to convert it into a GenerateContentRequest object, it uses @JsonProperty to
 * match the incoming JSON keys to the correct Java fields.
 */

/*
 * When you don't explicitly initialize a field in a Java object, its value
 * defaults to a specific "zero-equivalent" value based on its data type:
 * 
 * Object types (like inlineData, generationConfig, systemInstruction, tools,
 * contents, parts lists, functionDeclarations lists, codeExecution map,
 * parameters map, responseSchema map): They will be initialized to null.
 * Primitive numeric types (int, double, etc.): They will be initialized to 0 or
 * 0.0.
 * Boolean types: They will be initialized to false.
 * What happens during JSON serialization (e.g., when sending to Gemini API):
 * 
 * When Jackson (the library Spring Boot uses for JSON) serializes an object, it
 * generally omits fields that are null by default. So, if you don't set a value
 * for inlineData in a Part object, it remains null, and Jackson will not
 * include "inlineData": null in the generated JSON. This is desirable, as the
 * Gemini API expects these fields to be present only when they contain actual
 * data.
 * 
 * For example, if you create a Part with only text:
 * 
 * Java
 * 
 * GenerateContentRequest.Part textPart = new
 * GenerateContentRequest.Part("Hello");
 * // inlineData field is not set, so it remains null
 * The serialized JSON for this Part will be:
 * 
 * JSON
 * 
 * {
 * "text": "Hello"
 * }
 * The inlineData field is simply omitted because it's null. This prevents
 * sending unnecessary or empty fields to the API.
 */

/*
 * GenerateContentRequest.java Explained (Theory)
 * This Java class acts as a Data Transfer Object (DTO) or Plain Old Java Object
 * (POJO). Its sole purpose is to model the structure of the JSON payload that
 * you send to the Google Gemini API's generateContent endpoint.
 * 
 * Key Design Principles:
 * 
 * Object-Oriented Mapping: Instead of building raw JSON strings, you create
 * Java objects. This provides:
 * 
 * Type Safety: The compiler helps catch errors if you try to assign incorrect
 * types (e.g., a number where a string is expected).
 * Readability: The code is much easier to understand and maintain than
 * manipulating large JSON strings.
 * Encapsulation: The data structure is clearly defined within the class.
 * Hierarchical Structure (Nested Classes): The Gemini API's request body is
 * complex and nested. Java's static nested classes are a perfect fit for
 * mirroring this hierarchy. Each nested class represents a distinct object
 * within the JSON structure. Using static for nested classes means they don't
 * require an instance of the outer class to be created, which is typical for
 * DTOs.
 * 
 * Lombok for Conciseness: The @Data, @AllArgsConstructor,
 * and @NoArgsConstructor annotations from the Lombok library are used
 * extensively.
 * 
 * @Data: Automatically generates getters, setters, equals(), hashCode(), and
 * toString() methods for all fields. This drastically reduces boilerplate code.
 * 
 * @AllArgsConstructor: Generates a constructor with parameters for all fields.
 * 
 * @NoArgsConstructor: Generates a default, no-argument constructor. These are
 * essential for frameworks like Spring and libraries like Jackson to correctly
 * instantiate and populate your objects during serialization/deserialization.
 * 
 * @JsonProperty for JSON Mapping: The @JsonProperty("json_field_name")
 * annotation from Jackson is crucial. It tells Jackson how to map a Java field
 * name (which often follows camelCase) to the corresponding JSON property name
 * (which might be snake_case or just explicitly defined camelCase). Without
 * this, Jackson would assume the JSON field name is identical to the Java field
 * name.
 * 
 * Core Sections of the Request:
 * 
 * contents: This is where you put the actual conversational turns or prompts
 * for the model. It's a list because you can send multiple turns for multi-turn
 * conversations.
 * generationConfig: This allows you to fine-tune the model's behavior, like its
 * creativity (temperature), output length (maxOutputTokens), and diversity
 * (topP, topK).
 * systemInstruction: Provides high-level guidance to the model about its
 * persona or overall behavior.
 * tools: This is for advanced capabilities like Function Calling (where the
 * model can suggest calling external functions) or Code Execution (where the
 * model generates and executes code).
 * GenerateContentRequest.java with Demo Data
 * Let's illustrate how you would create an instance of this class to send a
 * request to the Gemini API for different scenarios.
 * 
 * Scenario 1: Basic Text Generation
 * You want to ask "Tell me a joke."
 * 
 * Java Code to build the Request:
 * 
 * Java
 * 
 * // Assuming you have the GenerateContentRequest class imported
 * 
 * // 1. Create a Part for the user's prompt
 * GenerateContentRequest.Part userPromptPart = new
 * GenerateContentRequest.Part("Tell me a joke."); [cite: 30, 31]
 * 
 * // 2. Create a Content object for the user's turn
 * GenerateContentRequest.Content userContent = new
 * GenerateContentRequest.Content("user", List.of(userPromptPart)); [cite: 23,
 * 24]
 * 
 * // 3. Create the main GenerateContentRequest object with the content
 * GenerateContentRequest request = new
 * GenerateContentRequest(List.of(userContent)); [cite: 13, 14]
 * 
 * // The 'request' object is now ready to be sent to the Gemini API
 * Equivalent JSON Payload that Jackson would generate:
 * 
 * JSON
 * 
 * {
 * "contents": [
 * {
 * "role": "user",
 * "parts": [
 * {
 * "text": "Tell me a joke."
 * }
 * ]
 * }
 * ]
 * }
 * Scenario 2: Text Generation with System Instruction and Configuration
 * You want a creative short story about a brave knight, limiting its length and
 * making it more imaginative.
 * 
 * Java Code to build the Request:
 * 
 * Java
 * 
 * // 1. User prompt part
 * GenerateContentRequest.Part userPromptPart = new
 * GenerateContentRequest.Part("Write a short story about a brave knight.");
 * 
 * // 2. User content
 * GenerateContentRequest.Content userContent = new
 * GenerateContentRequest.Content("user", List.of(userPromptPart));
 * 
 * // 3. System Instruction part
 * GenerateContentRequest.Part systemInstructionPart = new
 * GenerateContentRequest.
 * Part("You are a master storyteller. Focus on vivid descriptions and emotional depth."
 * );
 * 
 * // 4. System Instruction object
 * GenerateContentRequest.SystemInstruction systemInstruction = new
 * GenerateContentRequest.SystemInstruction(List.of(systemInstructionPart));
 * [cite: 63, 64]
 * 
 * // 5. Generation Configuration
 * GenerateContentRequest.GenerationConfig generationConfig = new
 * GenerateContentRequest.GenerationConfig();
 * generationConfig.setTemperature(0.9); // More creative [cite: 48, 49]
 * generationConfig.setMaxOutputTokens(200); // Limit to ~200 tokens [cite: 50,
 * 51]
 * generationConfig.setTopP(0.95); // Higher diversity [cite: 52, 53]
 * 
 * // 6. Main request object
 * GenerateContentRequest request = new GenerateContentRequest();
 * request.setContents(List.of(userContent)); [cite: 14, 15]
 * request.setSystemInstruction(systemInstruction); [cite: 18, 19]
 * request.setGenerationConfig(generationConfig); [cite: 16, 17]
 * 
 * // The 'request' object is now ready to be sent
 * Equivalent JSON Payload:
 * 
 * JSON
 * 
 * {
 * "contents": [
 * {
 * "role": "user",
 * "parts": [
 * {
 * "text": "Write a short story about a brave knight."
 * }
 * ]
 * }
 * ],
 * "systemInstruction": {
 * "parts": [
 * {
 * "text":
 * "You are a master storyteller. Focus on vivid descriptions and emotional depth."
 * }
 * ]
 * },
 * "generationConfig": {
 * "temperature": 0.9,
 * "maxOutputTokens": 200,
 * "topP": 0.95
 * }
 * }
 * Scenario 3: Image Analysis (Multimodal Input)
 * You want to describe an image.
 * 
 * Java Code to build the Request:
 * 
 * Java
 * 
 * // Assume 'base64EncodedImageData' is a String of your image data
 * // Assume 'imageMimeType' is "image/jpeg" or "image/png"
 * 
 * // 1. Text prompt part
 * GenerateContentRequest.Part promptPart = new
 * GenerateContentRequest.Part("Describe this image in detail.");
 * 
 * // 2. InlineData for the image
 * GenerateContentRequest.InlineData imageData = new
 * GenerateContentRequest.InlineData(imageMimeType, base64EncodedImageData);
 * [cite: 36, 37]
 * 
 * // 3. Part for the image
 * GenerateContentRequest.Part imagePart = new GenerateContentRequest.Part();
 * imagePart.setInlineData(imageData); [cite: 34, 35]
 * 
 * // 4. Content object with both text and image parts
 * GenerateContentRequest.Content userContent = new
 * GenerateContentRequest.Content("user", List.of(promptPart, imagePart));
 * [cite: 23, 24]
 * 
 * // 5. Main request object
 * GenerateContentRequest request = new
 * GenerateContentRequest(List.of(userContent)); [cite: 13, 14]
 * 
 * // The 'request' object is now ready to be sent
 * Equivalent JSON Payload (simplified base64EncodedImageData):
 * 
 * JSON
 * 
 * {
 * "contents": [
 * {
 * "role": "user",
 * "parts": [
 * {
 * "text": "Describe this image in detail."
 * },
 * {
 * "inlineData": {
 * "mime_type": "image/jpeg",
 * "data": "SUJBOlJPU0UgMTAwIFNSR0IuLi4=" // This would be the actual Base64
 * string
 * }
 * }
 * ]
 * }
 * ]
 * }
 */