package com.portfolio.awesomepizzabe.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "appinfo")
public class WorkhourProperties {

    String start;
    String end;

    @Value("reason-message")
    String reasonMessage;
}
