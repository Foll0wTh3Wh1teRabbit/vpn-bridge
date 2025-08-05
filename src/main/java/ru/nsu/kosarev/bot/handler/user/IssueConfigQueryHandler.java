package ru.nsu.kosarev.bot.handler.user;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.UserQueryHandler;
import ru.nsu.kosarev.bot.handler.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;
import ru.nsu.kosarev.bot.util.ProcessClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import static ru.nsu.kosarev.bot.util.MessageScriptCommands.CONFIG_SCRIPT;
import static ru.nsu.kosarev.bot.util.MessageScriptCommands.ISSUE_CONFIG;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueConfigQueryHandler implements UserQueryHandler {

    private static final BiFunction<Long, String, String> CONFIG_NAME_BUILDER =
        (userId, name) -> userId + "-" + name + ".conf";

    private static final BiFunction<Long, String, String> CONFIG_PATH_BUILDER =
        (userId, name) -> "/root/" + CONFIG_NAME_BUILDER.apply(userId, name);

    private final MessageClient messageClient;

    private final ProcessClient processClient;

    private final AdminCheckerService adminCheckerService;

    private final IMap<Long, List<File>> hazelcastConfigMap;

    @Override
    public void executeQuery(Update update, List<String> args) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (!adminCheckerService.isAdmin(userId) && hazelcastConfigMap.containsKey(userId)) {
            SendMessage alreadyHasConfig = SendMessage.builder()
                .chatId(chatId)
                .text("У вас уже имеется выпущенный конфиг")
                .build();

            messageClient.sendMessage(alreadyHasConfig);

            return;
        }

        String configName = args.isEmpty() ? UUID.randomUUID().toString() : args.getFirst();
        boolean configExists = Optional.ofNullable(hazelcastConfigMap.get(userId))
            .map(
                fileList -> fileList.stream()
                    .map(File::getName)
                    .anyMatch(CONFIG_NAME_BUILDER.apply(userId, configName)::equals)
            )
            .orElse(false);

        if (configExists) {
            SendMessage alreadyHasConfig = SendMessage.builder()
                .chatId(chatId)
                .text("У вас уже имеется выпущенный конфиг c таким именем")
                .build();

            messageClient.sendMessage(alreadyHasConfig);

            return;
        }

        String shellString = String.join(
            " ",
            CONFIG_SCRIPT,
            Integer.toString(ISSUE_CONFIG),
            CONFIG_NAME_BUILDER.apply(userId, configName)
        );

        log.info("IssueConfig <- update: [{}], configName:[{}]", update, configName);

        processClient.runScript(shellString)
            .thenRun(
                () -> {
                    File configFile = new File(CONFIG_PATH_BUILDER.apply(userId, configName));

                    try {
                        Files.copy(configFile.toPath(), configFile.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    InputFile inputFile = new InputFile(configFile);

                    SendDocument document = SendDocument.builder()
                        .chatId(chatId)
                        .document(inputFile)
                        .build();

                    messageClient.sendFile(document);

                    hazelcastConfigMap.compute(
                        userId,
                        (id, configs) -> {
                            if (configs == null) {
                                return new ArrayList<>(List.of(configFile));
                            }

                            List<File> modified = new ArrayList<>(configs);
                            modified.add(configFile);

                            return modified;
                        }
                    );

                    log.info("IssueConfig -> document: [{}]", document);
                }
            )
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
        return "/issueConfig";
    }

    @Override
    public String getDescription() {
        return "Выпустить конфиг";
    }

}
