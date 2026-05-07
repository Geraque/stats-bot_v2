package com.cs.doceho.stats.bot.v2.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class NoOpNotificationServiceTest {

  @Test
  void shouldLogSkippedNotificationWhenTelegramIsDisabled(CapturedOutput output) {
    NoOpNotificationService service = new NoOpNotificationService();

    service.sendMessage("123", "test");

    assertThat(output.getOut()).contains("Skipping Telegram notification because integration is disabled");
  }
}
