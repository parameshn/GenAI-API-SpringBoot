package com.example.genaiapi.config;

import org.springframework.beans.factory.annotations.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import lombok.Getter;

@Configuration
@Getter
/*
 * You could even use @Data (includes getters/setters/toString/equals/hashCode),
 * but for a config class, @Getter is more appropriate and safer.
 */
public class GeminiConfig {

    @Value("${gemini.api.key:}")
    private String apikey;
    /*
     * Inject the value of the property named gemini.api.key from
     * application.properties (or from an environment variable if one is set).
     * 
     * This part: :
     * "If gemini.api.key is not found, just use an empty string ("") as the default value."
     */

    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*
     * What it does: The @Configuration class is a designated place for setting up
     * and configuring application components. By putting the RestTemplate bean
     * there, you centralize its creation and any common configurations it might
     * need (e.g., message converters for JSON/XML, error handlers, interceptors for
     * logging or authentication).
     * Why it's good: If you need to add a new Interceptor for all outgoing
     * RestTemplate calls, or change its timeout settings, you do it in one place
     * (the bean definition), and all parts of your application that use that
     * RestTemplate will automatically pick up the change. If you create new
     * RestTemplate() everywhere, you'd have to update every single location.
     */


     /*
      * Simplifies HTTP calls: No need to write boilerplate code for HTTP
      * connections.
      * 
      * Automatic JSON/XML conversion: Integrates with Spring converters.
      * 
      * Handles errors: Throws exceptions for non-2xx HTTP responses so you can
      * handle them gracefully.
      * 
      * Supports all HTTP methods: GET, POST, PUT, DELETE, PATCH, etc.
      * 
      * Flexible: Allows adding custom headers, cookies, authentication, timeouts,
      * etc.
      * 
      */

      /*
       * RestTemplate is synchronous — meaning it blocks the calling thread until the
       * HTTP response arrives. For modern apps requiring async or reactive calls,
       * Spring recommends using WebClient (from Spring WebFlux). But RestTemplate
       * remains very popular and easy for many common use cases.
       */
}
