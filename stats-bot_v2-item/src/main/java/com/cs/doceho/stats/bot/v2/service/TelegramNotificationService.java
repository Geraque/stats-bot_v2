package com.cs.doceho.stats.bot.v2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramNotificationService implements NotificationService {

    private final TelegramBot telegramBot;

    @Override
    public void sendMessage(String chatId, String message) {
        telegramBot.sendNotification(chatId, message);
    }
}
