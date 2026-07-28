package in.lucidpoint.app.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Entities are returned directly from controllers (no DTO layer for reads), so every
 * lazy @ManyToOne/@OneToOne relation is a Hibernate proxy at serialization time. Without
 * this module, Jackson tries to serialize the proxy's internal fields (hibernateLazyInitializer)
 * and fails with "No serializer found for class ByteBuddyInterceptor".
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        // Without this, uninitialized relations serialize as null instead of loading via
        // the open-in-view session — e.g. GET /classes would return sections: null.
        Hibernate6Module module = new Hibernate6Module();
        module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }
}
