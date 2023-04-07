package searchengine.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
public class ParserConfig {
    private String useRagent;
    private String referrer;
    private int timeout;
}
