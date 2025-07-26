package ru.nsu.kosarev.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.util.MessageClient;

import static ru.nsu.kosarev.bot.util.MessageScriptCommands.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListConfigQueryHandler implements QueryHandler {

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update) {
        String userId = Long.toString(update.getMessage().getFrom().getId());
        String shellString = String.join(" ", ISSUE_CONFIG_SCRIPT, Integer.toString(LIST_USER_CONFIGS));

        log.info("ListConfig <- update: [{}], userId:[{}]", update, userId);
    }

    @Override
    public String getQuery() {
        return "/listConfig";
    }

}
