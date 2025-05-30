package com.example.genaiapi.service;


import com.example.genaiapi.config.GeminiConfig;
import com.example.gemini.model.GenerateContentRequest;
import com.example.gemini.model.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
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

    /**
     * Text generation with configuration parameters
     */

    public GenerateContentResponse generateTextWithConfig(String prompt, Double temperature, Integer maxTokens,
            Double topP, Integer topK) {
        String url = buildUrl("generateContent");

        GenerateContentRequest request = new GenerateContentRequest();

        request.setContents(
                List.of(new GenerateContentRequest.Content("user", List.of(new GnerateContentRequest.Part(prompt)))));
        GenerateContentRequest.GenerationConfig config = new GenerateContentRequest.GenerateionConfig();
        config.setTemperature(temperature);
        config.setMaxOutputTokens(maxTokens);
        config.setTopP(topP);
        config.setTopK(topK);
        request.setGenerationConfig(config);

        return makeRequest(url, request);

    }

    /**
     * Structured JSON output generation
     */

    public GenerateContentResponse generateStructuredJson(String prompt) {
        // 1. Construct the API Endpoint URL
        String url = buildUrl("generateContent");
        // Calls your custom buildUrl method to get the full URL for the generateContent
        // API.

        // 2. Prepare the Basic Request with the User Prompt
        GenerateContentRequest request = new GenerateContentRequest();
        request.setContents(List.of(
                new GenerateContentRequest.Content("user",
                        List.of(new GenerateContentRequest.Part(prompt)))));
        // This sets up the 'contents' field of the request, containing a single "user"
        // turn
        // with the text from the 'prompt' parameter.

        // 3. Define the JSON Schema for the Desired Output
        Map<String, Object> schema = Map.of(
                "type", "ARRAY", // We expect the top-level JSON to be an array
                "items", Map.of( // Each item in the array will be an object
                        "type", "OBJECT",
                        "properties", Map.of( // Properties for each object in the array
                                "recipeName", Map.of("type", "STRING"), // A string property for the recipe name
                                "ingredients", Map.of( // An array property for ingredients
                                        "type", "ARRAY",
                                        "items", Map.of("type", "STRING") // Each item in the ingredients array is a
                                                                                           // string
                                )),
                        "propertyOrdering", List.of("recipeName", "ingredients") // Optional: Suggests preferred order
                ));
        // This is the most crucial part for structured JSON. You're building a Java Map
        // that represents the JSON schema you want the model to adhere to for its
        // response.
        // In this case, it's an array of objects, where each object has 'recipeName'
        // (string)
        // and 'ingredients' (array of strings).

        // 4. Configure Generation for JSON Output
        GenerateContentRequest.GenerationConfig config = new GenerateContentRequest.GenerationConfig();
        config.setResponseMimeType("application/json"); // Tells the model to output JSON
        config.setResponseSchema(schema); // Provides the schema for the expected JSON structure
        request.setGenerationConfig(config);
        // This creates a GenerationConfig object, sets its responseMimeType to
        // "application/json"
        // (which is mandatory for structured JSON output), and then attaches your
        // defined schema.

        // 5. Make the API Request
        return makeRequest(url, request);
        // Finally, it calls your makeRequest helper method, passing the constructed URL
        // and the
        // GenerateContentRequest object that now includes all the necessary JSON schema
        // and mime type configurations.
    }
    
    /**
     * Function calling example
     */

    public GenerateContentResponse functionCalling(String prompt) {
        // 1. Construct the API Endpoint URL
        String url = buildUrl("generateContent");
        // Calls your custom buildUrl method to get the full URL for the generateContent
        // API.

        // 2. Prepare the Basic Request with the User Prompt
        GenerateContentRequest request = new GenerateContentRequest();
        request.setContents(List.of(
                new GenerateContentRequest.Content("user",
                        List.of(new GenerateContentRequest.Part(prompt)))));
        // This sets up the 'contents' field of the request, containing a single "user"
        // turn
        // with the text from the 'prompt' parameter.

        // 3. Define the Function Declaration
        Map<String, Object> functionParams = Map.of(
                "type", "object",
                "properties", Map.of( // Defines the properties (arguments) of the function
                        "location", Map.of(
                                "type", "string",
                                "description", "The city and state, e.g. San Francisco, CA"),
                        "unit", Map.of( // Optional parameter with enum values
                                "type", "string",
                                "enum", List.of("celsius", "fahrenheit"),
                                "description", "Temperature unit")),
                "required", List.of("location") // Specifies which parameters are mandatory
        );
        // This Map<String, Object> defines the schema for the function's parameters,
        // following OpenAPI Specification v3.0 format. This tells the model what
        // arguments
        // the 'get_current_weather' function expects.

        GenerateContentRequest.FunctionDeclaration function = new GenerateContentRequest.FunctionDeclaration(
                "get_current_weather", // The name of the function
                "Get the current weather in a given location", // A description for the model
                functionParams // The parameter schema defined above
        );
        // This creates a FunctionDeclaration object, which formally defines the
        // function
        // (its name, description, and expected parameters) to the Gemini model.

        // 4. Enable Tooling (Function Calling) in the Request
        GenerateContentRequest.Tool tool = new GenerateContentRequest.Tool();
        tool.setFunctionDeclarations(List.of(function));
        // This creates a Tool object and adds your 'get_current_weather' function
        // declaration to it.
        // The Gemini API expects function declarations within a 'tools' array.

        request.setTools(List.of(tool));
        // This sets the 'tools' field of your main GenerateContentRequest. By including
        // tool definitions here, you inform the model that it has access to these
        // functions
        // and can suggest calling them if appropriate.

        // 5. Make the API Request
        return makeRequest(url, request);
        // Finally, it calls your makeRequest helper method, passing the constructed URL
        // and the
        // GenerateContentRequest object that now includes the defined tools.
    }

    /*
     * Take a prompt (e.g., "What's the weather like in New York?").
     * Define a specific function (get_current_weather in this case) with its
     * description and parameter requirements.
     * Pass this function definition to the Gemini API via the tools field in the
     * GenerateContentRequest.
     * When you send a prompt like "What's the weather in London?", the Gemini
     * model, aware of the get_current_weather tool, might respond with a
     * functionCall object (instead of text) indicating that it wants to call
     * get_current_weather with location: "London". Your application would then need
     * to execute that function and send the result back to the model for it to
     * formulate a natural language answer.
     */

     /*
      * You (Your Application) Send the Prompt + Tool Definitions:
      * 
      * You prepare a GenerateContentRequest that includes the user's prompt (e.g.,
      * "What's the weather in London?").
      * Crucially, you also include tools (specifically functionDeclarations) in this
      * request, defining the functions the model can call (like
      * get_current_weather).
      * Gemini Model Parses and Suggests a Function Call:
      * 
      * The model receives your request.
      * It analyzes the prompt and the provided functionDeclarations.
      * If it determines that calling one of your defined functions is the best way
      * to answer the prompt, it does NOT directly call the function itself.
      * Instead, it generates a response containing a functionCall object (a Part
      * within its content), telling your application which function to call and with
      * what arguments (e.g., get_current_weather(location="London")).
      * You (Your Application) Execute the Function:
      * 
      * Your application receives the GenerateContentResponse from Gemini.
      * It checks if the response contains a functionCall Part.
      * If it does, your application then parses the functionCall object to get the
      * function name and its args.
      * Your application then calls the actual external function (e.g., your
      * get_current_weather API or database query) using the extracted arguments.
      * You (Your Application) Send the Function's Result Back to Gemini:
      * 
      * Once your application has executed the external function and obtained its
      * result (e.g., { "temperature": "15C", "condition": "Cloudy" }).
      * You construct a new GenerateContentRequest for the next turn in the
      * conversation.
      * This new request includes:
      * The entire prior conversation history (the original user prompt, and the
      * model's functionCall response).
      * A new Part with role: "function" and functionResponse containing the result
      * of your external function call.
      * Gemini Model Uses the Function Result to Formulate a Natural Language
      * Response:
      * 
      * The model receives this new request with the function's output.
      * It then uses that information to generate a natural language answer to the
      * original user's prompt (e.g.,
      * "The current weather in London is 15 degrees Celsius and cloudy.").
      * This final natural language response is what you present to the user.
      * In short, it's a two-step process involving your application as the
      * intermediary:
      * 
      * Prompt + Tool Definitions
      * (Your App) -----------------> (Gemini Model)
      * |
      * | Model suggests: functionCall(...)
      * V
      * (Gemini Model) <---------- (Your App)
      * ^ |
      * | | Your App executes the actual function
      * | | Your App sends functionResponse(...) + (optional new user prompt)
      * | V
      * (Gemini Model) ---------> (Your App)
      * |
      * | Model provides natural language answer
      * V
      * (Your App) ---------------> (End User)
      */

      /*
       * Your functionCalling Method and Function Declaration:
       * Java
       * 
       * // Your function declaration for get_current_weather
       * Map<String, Object> functionParams = Map.of(
       * "type", "object",
       * "properties", Map.of(
       * "location", Map.of(
       * "type", "string",
       * "description", "The city and state, e.g. San Francisco, CA"
       * ),
       * "unit", Map.of(
       * "type", "string",
       * "enum", List.of("celsius", "fahrenheit"),
       * "description", "Temperature unit"
       * )
       * ),
       * "required", List.of("location") // Crucially, 'location' is required
       * );
       * GenerateContentRequest.FunctionDeclaration function = new
       * GenerateContentRequest.FunctionDeclaration(
       * "get_current_weather", // Function Name
       * "Get the current weather in a given location", // Function Description
       * functionParams // Function Parameters Schema
       * );
       * 
       * // This function is then passed to the model via request.setTools(...)
       * The Step-by-Step Flow with Your get_current_weather Function:
       * You (Your Application) Send the Prompt + Your Function Declaration:
       * 
       * Your Action: You call
       * geminiService.functionCalling("What's the weather like in London?").
       * What is sent to Gemini: Your GenerateContentRequest will include:
       * contents: [{ "role": "user", "parts": [{ "text":
       * "What's the weather like in London?" }] }]
       * tools: This will contain the declaration for your get_current_weather
       * function (name, description, and the location and unit parameters). The model
       * now "knows" this function exists and what arguments it needs.
       * Gemini Model Parses and Suggests Your get_current_weather Call:
       * 
       * Model's Internal Logic: The Gemini model receives your prompt and the
       * get_current_weather function declaration. It analyzes the prompt
       * ("What's the weather like in London?") and matches the user's intent
       * ("get weather") to the function's description
       * ("Get the current weather in a given location").
       * It identifies "London" as the value for the location parameter, which is a
       * required parameter in your functionParams. It doesn't find a unit specified,
       * so it would typically omit that optional parameter.
       * What Gemini Sends Back to Your App: Instead of a direct text response, Gemini
       * sends a GenerateContentResponse containing a functionCall part:
       * JSON
       * 
       * {
       * "candidates": [{
       * "content": {
       * "parts": [{
       * "functionCall": {
       * "name": "get_current_weather", // The name of *your* function
       * "args": {
       * "location": "London" // Arguments extracted from your prompt
       * }
       * }
       * }],
       * "role": "model"
       * }
       * }]
       * }
       * You (Your Application) Execute the Actual get_current_weather Function:
       * 
       * Your Action: Your application receives the above GenerateContentResponse. It
       * detects the functionCall for get_current_weather.
       * You would then, in your application's backend logic, invoke your actual
       * implementation of a get_current_weather function (e.g., call out to a
       * third-party weather API, query a database, etc.) using the location: "London"
       * argument that the model provided.
       * Hypothetical Result: Let's say your get_current_weather("London") function
       * returns:
       * JSON
       * 
       * {
       * "city": "London",
       * "temperature": "18°C",
       * "condition": "Partly Cloudy",
       * "humidity": "65%"
       * }
       * You (Your Application) Send the Function's Result Back to Gemini:
       * 
       * Your Action: You construct a new GenerateContentRequest. This request must
       * include the full conversation history (your initial prompt, Gemini's
       * functionCall response) plus the result of your function call.
       * What is sent to Gemini (Next Turn):
       * JSON
       * 
       * {
       * "contents": [
       * // Previous turn: User prompt
       * { "role": "user", "parts": [{ "text": "What's the weather like in London?" }]
       * },
       * // Previous turn: Model's functionCall suggestion
       * { "role": "model", "parts": [{ "functionCall": { "name":
       * "get_current_weather", "args": { "location": "London" } } }] },
       * // NEW turn: Result of your executed function
       * { "role": "function", "parts": [{
       * "functionResponse": {
       * "name": "get_current_weather", // Must match the called function name
       * "response": { // The actual data returned by your function
       * "city": "London",
       * "temperature": "18°C",
       * "condition": "Partly Cloudy",
       * "humidity": "65%"
       * }
       * }
       * }] }
       * // Optionally, if the user had another question in mind, you could add
       * another user turn here.
       * ]
       * }
       * Gemini Model Uses Your Function Result to Formulate a Natural Language
       * Response:
       * 
       * Model's Internal Logic: The model now has the prompt, its own suggestion, and
       * the actual result from your get_current_weather function. It uses this
       * information to synthesize a human-readable answer.
       * What Gemini Sends Back to Your App: Gemini responds with a
       * GenerateContentResponse containing a textual response:
       * JSON
       * 
       * {
       * "candidates": [{
       * "content": {
       * "parts": [{
       * "text":
       * "The current weather in London is 18°C and partly cloudy, with 65% humidity."
       * }],
       * "role": "model"
       * }
       * }]
       * }
       * Your Action: Your application displays this final textual response to the
       * end-user.
       * This multi-step "loop" ensures that the Gemini model acts as a powerful
       * orchestrator, leveraging your defined tools while keeping your application in
       * control of the actual external actions.
       */

       /**
       * Code execution example
       */

       public GenerateContentResponse codeExecution(String prompt) {
           String url = buildUrl("generateContent");

           GenerateContentRequest request = new GenerateContentRequest();
           request.setConents(List
                   .of(new GenerateContentRequest.Content("user", List.of(new GenerateContentRequest.Part(prompt)))));
           GenerateContentRequest.Tool tool = new GenerateCOntentRequest.Tool();
           tool.setCodeExecution(Map.of());
           request.setTools(List.of(tool));

           return makeRequest(url, request);
       }


    private String buildUrl(String endpoint) {
        return UriComponentsBuilder.fromUriString(geminiConfig.getBaseUrl())
                .path("/models/")
                .path(MODEL_NAME)
                .path(":")
                .path(endpoint)
                .queryParam("key", geminiConfig.getApiKey())
                .toUriString();
    }
    /*
     * URL Encoding: Automatically handles URL encoding of path segments and query
     * parameters, which is crucial for building valid and safe URLs.
     * Readability: Can be more readable for complex URL structures as you chain
     * methods (.path(), .queryParam(), etc.).
     * Flexibility: Easily allows adding/removing path segments or query parameters
     * conditionally.
     */
    /*
     * private String buildUrl(String endpoint) {
     * return String.format("%s/models/%s:%s?key=%s",
     * geminiConfig.getBaseUrl(), MODEL_NAME, endpoint, geminiConfig.getApiKey());
     * }
     */

    private GenerateContentResponse makeRequest(String url, GenerateContentRequest request) {
        // 1. Prepare HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Sets the "Content-Type" header to "application/json"

        // 2. Create the HTTP Entity (Request Body + Headers)
        HttpEntity<GenerateContentRequest> entity = new HttpEntity<>(request, headers);
        // 'request' is your GenerateContentRequest object, which Jackson will convert
        // to JSON.
        // 'headers' are the HTTP headers you just defined.

        // 3. Make the HTTP POST request and get the full ResponseEntity
        ResponseEntity<GenerateContentResponse> response = restTemplate.postForEntity(
                url, // The full URL of the Gemini API endpoint
                entity, // The request body (JSON) and headers
                GenerateContentResponse.class // The expected class for the API's JSON response
        );

        // 4. Extract and Return the Response Body
        return response.getBody(); // Retrieves the GenerateContentResponse object from the ResponseEntity
    }

    

    
     
}
