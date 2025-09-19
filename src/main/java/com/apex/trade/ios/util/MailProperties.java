package com.apex.trade.ios.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties = new Properties();


}
