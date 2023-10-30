package searchengine.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ConfigurationProperties(prefix = "configsites")
@Data
public class SiteConfig {
    private ArrayList<Site> sites;
}
