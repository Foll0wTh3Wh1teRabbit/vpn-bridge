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
public class StartQueryHandler implements QueryHandler {

    private static final String START_MESSAGE =
        """
        Привет!
        
        Для получения списка доступных команд воспользуйтесь командой /help.""";

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update) {
        log.info("Start <- update: [{}]", update);

        SendMessage message = SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(START_MESSAGE)
            .build();

        messageClient.sendMessage(message);

        log.info("Start -> ");
    }

    @Override
    public String getQuery() {
        return "/start";
    }

}
