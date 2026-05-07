package com.cs.doceho.stats.bot.v2.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.cs.doceho.stats.bot.v2.api.CategoryApi;
import com.cs.doceho.stats.bot.v2.api.MatchApi;
import com.cs.doceho.stats.bot.v2.api.TopApi;
import com.cs.doceho.stats.bot.v2.service.NoOpNotificationService;
import com.cs.doceho.stats.bot.v2.service.NotificationService;
import com.cs.doceho.stats.bot.v2.service.TelegramBot;
import com.cs.doceho.stats.bot.v2.service.TelegramNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

class TelegramConditionalBeansTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
      .withUserConfiguration(TestTelegramConfiguration.class)
      .withBean(MatchApi.class, () -> mock(MatchApi.class))
      .withBean(TopApi.class, () -> mock(TopApi.class))
      .withBean(CategoryApi.class, () -> mock(CategoryApi.class));

  @Test
  void shouldLoadContextWithoutTelegramBeansWhenDisabled() {
    contextRunner
        .withPropertyValues("telegram.enabled=false")
        .run(context -> {
          assertThat(context).hasSingleBean(NotificationService.class);
          assertThat(context).hasSingleBean(NoOpNotificationService.class);
          assertThat(context).doesNotHaveBean(TelegramBot.class);
          assertThat(context).doesNotHaveBean(TelegramNotificationService.class);
        });
  }

  @Test
  void shouldCreateTelegramBeansWhenEnabled() {
    contextRunner
        .withPropertyValues(
            "telegram.enabled=true",
            "telegram.bot.name=Doceho",
            "telegram.bot.token=test-token"
        )
        .run(context -> {
          assertThat(context).hasSingleBean(NotificationService.class);
          assertThat(context).hasSingleBean(TelegramBot.class);
          assertThat(context).hasSingleBean(TelegramNotificationService.class);
          assertThat(context).doesNotHaveBean(NoOpNotificationService.class);
        });
  }

  @Configuration
  @Import({
      TelegramConfiguration.class,
      TelegramBot.class,
      TelegramNotificationService.class,
      NoOpNotificationService.class
  })
  static class TestTelegramConfiguration {
  }
}
