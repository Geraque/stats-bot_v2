package com.cs.doceho.stats.bot.v2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    prefix = "telegram",
    name = "enabled",
    havingValue = "false",
    matchIfMissing = true
)
public class NoOpNotificationService implements NotificationService {

  @Override
  public void sendMessage(String chatId, String message) {
    log.info("Skipping Telegram notification because integration is disabled. chatId={}", chatId);
  }
}
