package com.cs.doceho.stats.bot.v2.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
//Данные о боте
@Configuration
@Data
@PropertySource("application.yaml")
public class BotConfig {

    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;

}
