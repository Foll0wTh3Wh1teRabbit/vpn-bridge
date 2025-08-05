package ru.nsu.kosarev.bot.handler.admin;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AdminQueryHandler;
import ru.nsu.kosarev.bot.handler.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;
import ru.nsu.kosarev.bot.util.ProcessClient;

import java.io.File;
import java.util.List;

import static ru.nsu.kosarev.bot.util.MessageScriptCommands.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveQueryHandler implements AdminQueryHandler {

    private final MessageClient messageClient;

    private final ProcessClient processClient;

    private final AdminCheckerService adminCheckerService;

    private final IMap<Long, List<File>> hazelcastConfigMap;

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

        Long userToRemove = Long.parseLong(args.getFirst());
        String shellString = String.join(
            " ",
            CONFIG_SCRIPT,
            Integer.toString(REMOVE_CONFIGS),
            userToRemove + "-"
        );

        processClient.runScript(shellString)
            .thenRun(() -> hazelcastConfigMap.remove(userId))
            .exceptionally(
                th -> {
                    SendMessage error = SendMessage.builder()
                        .chatId(chatId)
                        .text("Ошибка обработки запроса")
                        .build();

                    messageClient.sendMessage(error);

                    return null;
                }
            );
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
