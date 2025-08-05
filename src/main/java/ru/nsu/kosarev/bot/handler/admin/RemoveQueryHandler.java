package ru.nsu.kosarev.bot.handler.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AdminQueryHandler;
import ru.nsu.kosarev.bot.handler.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveQueryHandler implements AdminQueryHandler {

    private final MessageClient messageClient;

    private final AdminCheckerService adminCheckerService;

    @Override
    public void executeQuery(Update update, List<String> args) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (!adminCheckerService.isAdmin(userId)) {
            //You're not allowed
        }


    }

    @Override
    public String getQuery() {
        return "/remove";
    }

    @Override
    public String getDescription() {
        return "Удалить конфиги пользователя";
    }

}
