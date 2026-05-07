package com.cs.doceho.stats.bot.v2.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfiguration {
}

@Slf4j
@Component
@RequiredArgsConstructor
class TelegramIntegrationStatusLogger {

  private final TelegramProperties telegramProperties;

  @EventListener(ApplicationStartedEvent.class)
  public void logStatus() {
    if (telegramProperties.isEnabled()) {
      log.info("Telegram integration is enabled.");
    } else {
      log.info("Telegram integration is disabled.");
    }
  }
}
