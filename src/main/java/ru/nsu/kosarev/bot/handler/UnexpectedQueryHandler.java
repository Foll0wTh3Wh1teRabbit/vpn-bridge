package ru.nsu.kosarev.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.util.MessageClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnexpectedQueryHandler implements QueryHandler {

    private static final String UNEXPECTED_QUERY_MESSAGE =
        "Неизвестный тип запроса.\n\nИнформация о доступных запросах: /help";

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update) {
        log.info("UnexpectedQueryHandler <- update: [{}]", update);

        Long chatId = update.getMessage().getChatId();

        SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text(UNEXPECTED_QUERY_MESSAGE)
            .build();

        messageClient.sendMessage(message);
    }

    @Override
    public String getQuery() {
        return "/unexpected";
    }

}
