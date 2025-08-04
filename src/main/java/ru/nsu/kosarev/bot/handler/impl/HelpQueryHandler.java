package ru.nsu.kosarev.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AvailableQueryHandler;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpQueryHandler implements AvailableQueryHandler {

    private final MessageClient messageClient;

    private final List<AvailableQueryHandler> queryHandlers;

    @Override
    public void executeQuery(Update update) {
        log.info("Help <- update: [{}]", update);

        Long chatId = update.getMessage().getChatId();

        String queriesHelpString = queryHandlers.stream()
            .map(query -> String.format("%s - %s", query.getQuery(), query.getDescription()))
            .collect(Collectors.joining("\n"));

        SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text(queriesHelpString)
            .build();

        messageClient.sendMessage(message);
    }

    @Override
    public String getQuery() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Получить список доступных запросов";
    }

}
