package com.cs.doceho.stats.bot.v2.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.yaml")
public class LeetifyProperties {

  @Value("${leetify.login}")
  String login;
  @Value("${leetify.password}")
  String password;

}
