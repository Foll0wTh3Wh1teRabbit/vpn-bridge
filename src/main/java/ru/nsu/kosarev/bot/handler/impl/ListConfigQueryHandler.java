package ru.nsu.kosarev.bot.handler.impl;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AvailableQueryHandler;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListConfigQueryHandler implements AvailableQueryHandler {

    private final MessageClient messageClient;

    private final IMap<String, List<File>> hazelcastConfigMap;

    @Override
    public void executeQuery(Update update) {
        String userId = Long.toString(update.getMessage().getFrom().getId());

        log.info("ListConfig <- update: [{}], userId:[{}]", update, userId);

        if (!hazelcastConfigMap.containsKey(userId)) {
            SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("У вас нет выпущенных конфигов")
                .build();

            messageClient.sendMessage(message);
        } else {
            hazelcastConfigMap.get(userId).stream()
                .map(InputFile::new)
                .map(file -> SendDocument.builder().chatId(update.getMessage().getChatId()).document(file).build())
                .forEach(messageClient::sendFile);
        }
    }

    @Override
    public String getQuery() {
        return "/listConfig";
    }

    @Override
    public String getDescription() {
        return "Получить список выпущенных конфигов";
    }

}
