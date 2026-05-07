package com.cs.doceho.stats.bot.v2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

  private boolean enabled = false;
  private Bot bot = new Bot();
  private Timeout timeout = new Timeout();

  @Data
  public static class Bot {
    private String name;
    private String token;
  }

  @Data
  public static class Timeout {
    private int connectMs = 2_000;
    private int readMs = 5_000;
    private int requestMs = 2_000;
    private int getUpdatesSeconds = 30;
  }
}
