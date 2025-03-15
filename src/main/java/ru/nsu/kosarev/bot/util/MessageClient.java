package ru.nsu.kosarev.bot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class MessageClient {

    private final TelegramClient telegramClient;

    @Autowired
    public MessageClient() {
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    public String getBotToken() {
        return "7999258956:AAEkdFcZCiCx2yIeI6Y12XmvtNhMwy79EoI";
    }

    public void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("An error during send has occurred: ", e);
        }
    }

    public void sendFile(SendDocument document) {
        try {
            telegramClient.execute(document);
        } catch (TelegramApiException e) {
            log.error("An error during send has occurred: ", e);
        }
    }

}
