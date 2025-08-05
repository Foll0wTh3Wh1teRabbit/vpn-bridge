package ru.nsu.kosarev.bot.handler.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AdminQueryHandler;
import ru.nsu.kosarev.bot.handler.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;
import ru.nsu.kosarev.bot.util.ProcessClient;

import java.util.List;

import static ru.nsu.kosarev.bot.util.MessageScriptCommands.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnlockQueryHandler implements AdminQueryHandler {

    private final MessageClient messageClient;

    private final ProcessClient processClient;

    private final AdminCheckerService adminCheckerService;

    @Override
    public void executeQuery(Update update, List<String> args) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (!adminCheckerService.isAdmin(userId)) {
            SendMessage alreadyHasConfig = SendMessage.builder()
                .chatId(chatId)
                .text("Доступ запрещен")
                .build();

            messageClient.sendMessage(alreadyHasConfig);

            return;
        }

        String shellString = String.join(
            " ",
            CONFIG_SCRIPT,
            Integer.toString(UNLOCK_CONFIGS),
            userId + "-"
        );

        processClient.runScript(shellString)
            .exceptionally(
                th -> {
                    log.error("An error during issue config has occurred: ", th);

                    return null;
                }
            );
    }

    @Override
    public String getQuery() {
        return "/unlock";
    }

    @Override
    public String getDescription() {
        return "Разблокировать конфиги пользователя";
    }

}
