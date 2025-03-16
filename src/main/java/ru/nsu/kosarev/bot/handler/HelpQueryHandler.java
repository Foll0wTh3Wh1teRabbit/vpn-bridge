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
public class HelpQueryHandler implements QueryHandler {

    private static final String AVAILABLE_QUERIES = """
        /issueConfig – выпустить новый конфиг.
        """;

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update) {
        log.info("Help <- update: [{}]", update);

        Long chatId = update.getMessage().getChatId();

        SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text(AVAILABLE_QUERIES)
            .build();

        messageClient.sendMessage(message);
    }

    @Override
    public String getQuery() {
        return "/help";
    }

}
