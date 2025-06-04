package com.example.genaiapi.controller;

import com.example.genaiapi.model.GenerateContentResponse;
import com.example.genaiapi.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse.ResponseInfo;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "*")
/*
 * is a CORS (Cross-Origin Resource Sharing) configuration that allows web
 * browsers to make requests to your API from any domain
 */
public class GeminiController {
    @Autowired 
    private GeminiService geminiService;

    @PostMapping("/generate-text")
    public ResponseEntity<GenerateContentResponse> generateText(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        GenerateContentResponse response = geminiService.generateText(prompt);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/generate-with-system-instruction")
    public ResponseEntity<GenerateContentResponse> generateWithSystemInstruction(
            @RequestBody Map<String, String> request) {
         String prompt = request.get("prompt");
        String systemInstruction = request.get("systemInstruction");
        GenerateContentResponse response = geminiService.generateTextWithSystemInstruction(prompt, systemInstruction);
        return ResponseEntity.ok(response);
    }
        
    @PostMapping("/generate-with-config")
    public ResponseEntity<GenerateContentResponse> generateWithConfig(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        Double temperature = request.get("temperature") != null ? Double.valueOf(request.get("temperature").toString())
                : null;
        Integer maxTokens = request.get("maxTokens") != null ? Integer.valueOf(request.get("maxTokens").toString())
                : null;
        Double topP = request.get("topP") != null ? Double.valueOf(request.get("topP").toString()) : null;
        Integer topK = request.get("topK") != null ? Integer.valueOf(request.get("topK").toString()) : null;

        GenerateContentResponse response = geminiService.generateTextWithConfig(prompt, temperature, maxTokens, topP,
                topK);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-structured-json")
    public ResponseEntity<GenerateContentResponse> generateStructuredJson(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        GenerateContentResponse response = geminiService.generateStructuredJson(prompt);
        return ResponseEntity.ok(response);
    }
    

    @PostMapping("/function-calling")
    public ResponseEntity<GenerateContentResponse> functionCalling(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        GenerateContentResponse response = geminiService.functionCalling(prompt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/code-execution")
    public ResponseEntity<GenerateContentResponse> codeExecution(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        GenerateContentResponse response = geminiService.codeExecution(prompt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/multi-turn-conversation")
    public ResponseEntity<GenerateContentResponse> multiTurnConversation(
            @RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> conversationHistory = request.get("conversation");
        GenerateContentResponse response = geminiService.multiTurnConversation(conversationHistory);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<GenerateContentResponse> analyzeImage(
            @RequestParam("prompt") String prompt,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = imageFile.getContentType();

            GenerateContentResponse response = geminiService.analyzeImage(prompt, base64Image, mimeType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /*
     * MultipartFile
     * It encapsulates:
     * 
     * The actual binary content of the image.
     * Its original filename.
     * Its MIME type (e.g., image/jpeg).
     */

    /*
     * MultipartFile is a Spring Framework interface that represents an uploaded
     * file received in a multipart request. It's the standard way Spring Boot
     * handles file uploads from web forms or API clients.
     * 
     * Here's how MultipartFile is used in your analyzeImage method:
     * 
     * @RequestParam("image") MultipartFile imageFile:
     * 
     * @RequestParam("image"): This annotation tells Spring that the imageFile
     * parameter will come from a request parameter named "image" in the incoming
     * HTTP request. This typically corresponds to the name attribute of a file
     * input field in an HTML form (<input type="file" name="image">) or a part name
     * in a programmatic client request (e.g., Postman, curl).
     * MultipartFile imageFile: This is the type of the parameter. When a file is
     * uploaded via an HTTP POST request with Content-Type: multipart/form-data,
     * Spring automatically binds the uploaded file's data and metadata into a
     * MultipartFile object.
     * byte[] imageBytes = imageFile.getBytes();:
     * 
     * The MultipartFile object provides convenient methods to access the uploaded
     * file's content.
     * getBytes() is used here to read the entire content of the uploaded image file
     * into a byte array. This raw binary data is what's needed to encode the image.
     * String base64Image = Base64.getEncoder().encodeToString(imageBytes);:
     * 
     * As we discussed, the Gemini API expects image data as a Base64-encoded
     * string.
     * This line takes the imageBytes obtained from the MultipartFile and converts
     * them into the required Base64 string format.
     * String mimeType = imageFile.getContentType();:
     * 
     * MultipartFile also provides access to metadata about the uploaded file.
     * getContentType() retrieves the MIME type (e.g., "image/jpeg", "image/png") of
     * the uploaded file as declared by the client. This mimeType is then directly
     * passed to the geminiService.analyzeImage method, as the Gemini API needs it
     * to correctly interpret the image data.
     * In summary, MultipartFile is:
     * 
     * Spring Boot's Abstraction for File Uploads: It simplifies handling uploaded
     * files by encapsulating the file's content and metadata.
     * Essential for Multimodal Input: In your analyzeImage method, it's the bridge
     * that takes the image file from a web request and converts it into the byte[]
     * and mimeType required to construct the InlineData for the Gemini API call.
     */

     /*
      * Demo Data for analyzeImage
      * Providing a complete Base64 encoded string for a real image is impractical
      * here as it would be extremely long. However, here's a conceptual demo of what
      * the base64Image and mimeType would look like when passed to your analyzeImage
      * method.
      * 
      * Conceptual Demo Data:
      * 
      * prompt (String): "Describe this image in detail."
      * base64Image (String):
      * "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
      * (Note: This is a very short, actual Base64 string for a tiny 1x1 pixel
      * transparent PNG. A real image (like a photo) would result in a Base64 string
      * that is thousands or even millions of characters long.)
      * mimeType (String): "image/png"
      * To use this analyzeImage method with real data, you would typically:
      * 
      * Have an image file (e.g., my_picture.jpg).
      * Read that file into a byte[] in your Java application.
      * Convert that byte[] to a String using
      * java.util.Base64.getEncoder().encodeToString(imageBytes).
      * Determine the correct MIME type (e.g., "image/jpeg", "image/png") based on
      * the file type.
      * Pass these prompt, base64Image, and mimeType strings to your analyzeImage
      * method.
      * Why Base64 Encoding? Is it to Reduce Data Size or Length?
      * No, Base64 encoding is not used to reduce data size or length; in fact, it
      * increases it!
      * 
      * Here's why Base64 encoding is necessary and what it does:
      * 
      * Binary Data in Text-Based Formats:
      * 
      * JSON (and other text-based data interchange formats like XML) are designed to
      * handle text characters.
      * Image files, audio files, video files, etc., are binary data (sequences of
      * bytes that don't directly correspond to readable text characters).
      * Directly embedding raw binary data into a JSON string can corrupt the JSON
      * structure or lead to transmission errors.
      * Safe Transmission:
      * 
      * Base64 encoding is a method of converting binary data into an ASCII (text)
      * string representation.
      * It uses a set of 64 safe ASCII characters (A-Z, a-z, 0-9, +, /, and = for
      * padding) that can be reliably transmitted across systems and embedded within
      * text formats without causing issues.
      * Data Size Increase:
      * 
      * Because it maps every 3 bytes of binary data to 4 bytes of Base64-encoded
      * ASCII data, Base64 encoding actually makes the data approximately 33% larger
      * than the original binary size.
      * In summary, Base64 encoding is used to:
      * 
      * Represent binary data as text: Allowing it to be safely embedded within
      * text-based protocols and formats like JSON.
      * Ensure data integrity during transmission: Preventing corruption that could
      * occur if raw binary bytes were treated as text.
      * It's a necessary conversion for transmitting images (or any binary data) in a
      * JSON payload, despite the increase in size.
      */
}
