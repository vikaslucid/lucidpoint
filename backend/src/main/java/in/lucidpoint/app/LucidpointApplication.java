package in.lucidpoint.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. Running this class boots an embedded Tomcat server on port 8080
 * and starts the whole Spring context (all @Component/@Service/@Repository/@Controller beans).
 */
@SpringBootApplication
public class LucidpointApplication {
    public static void main(String[] args) {
        SpringApplication.run(LucidpointApplication.class, args);
    }
}
