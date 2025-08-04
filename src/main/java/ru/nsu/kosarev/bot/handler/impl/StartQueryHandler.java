package ru.nsu.kosarev.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AvailableQueryHandler;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartQueryHandler implements AvailableQueryHandler {

    private static final String START_MESSAGE =
        """
        Привет!
        
        Для получения списка доступных команд воспользуйтесь командой /help.""";

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update, List<String> args) {
        log.info("Start <- update: [{}]", update);

        Long chatId = update.getMessage().getChatId();

        SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text(START_MESSAGE)
            .build();

        messageClient.sendMessage(message);

        log.info("Start -> ");
    }

    @Override
    public String getQuery() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Стартовать работу с ботом";
    }

}
