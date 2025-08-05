package ru.nsu.kosarev.bot.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AdminQueryHandler;
import ru.nsu.kosarev.bot.handler.QueryHandler;
import ru.nsu.kosarev.bot.handler.UserQueryHandler;
import ru.nsu.kosarev.bot.handler.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpQueryHandler implements UserQueryHandler {

    private final MessageClient messageClient;

    private final AdminCheckerService adminCheckerService;

    private final List<UserQueryHandler> userQueryHandlers;

    private final List<AdminQueryHandler> adminQueryHandlers;

    @Override
    public void executeQuery(Update update, List<String> args) {
        log.info("Help <- update: [{}]", update);

        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        List<? extends QueryHandler> handlersToDisplay = adminCheckerService.isAdmin(userId) ?
            Stream.concat(userQueryHandlers.stream(), adminQueryHandlers.stream()).toList() :
            userQueryHandlers;

        String queriesHelpString = handlersToDisplay.stream()
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
