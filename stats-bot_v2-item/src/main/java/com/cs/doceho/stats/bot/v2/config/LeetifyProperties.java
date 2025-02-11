package com.cs.doceho.stats.bot.v2.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.yaml")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "leetify")
public class LeetifyProperties {
  List<Account> accounts;

  @FieldDefaults(level = AccessLevel.PRIVATE)
  @Data
  public static class Account {
    String login;
    String password;
  }
}

