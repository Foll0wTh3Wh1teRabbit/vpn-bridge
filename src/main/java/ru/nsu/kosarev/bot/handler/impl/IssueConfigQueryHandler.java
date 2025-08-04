package ru.nsu.kosarev.bot.handler.impl;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.handler.AvailableQueryHandler;
import ru.nsu.kosarev.bot.util.AdminCheckerService;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;

import static ru.nsu.kosarev.bot.util.MessageScriptCommands.ISSUE_CONFIG;
import static ru.nsu.kosarev.bot.util.MessageScriptCommands.ISSUE_CONFIG_SCRIPT;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueConfigQueryHandler implements AvailableQueryHandler {

    private static final BinaryOperator<String> CONFIG_NAME_BUILDER =
        (userId, uuid) -> userId + "-" + uuid;

    private static final BinaryOperator<String> CONFIG_PATH_BUILDER =
        (userId, uuid) -> "/root/" + CONFIG_NAME_BUILDER.apply(userId, uuid) + ".conf";

    private final MessageClient messageClient;

    private final AdminCheckerService adminCheckerService;

    private final IMap<String, List<File>> hazelcastConfigMap;

    @Override
    public void executeQuery(Update update) {
        String userId = Long.toString(update.getMessage().getFrom().getId());
        String configUuid = UUID.randomUUID().toString();
        String shellString = String.join(
            " ",
            ISSUE_CONFIG_SCRIPT,
            Integer.toString(ISSUE_CONFIG),
            CONFIG_NAME_BUILDER.apply(userId, configUuid)
        );

        if (!adminCheckerService.isAdmin(userId) && hazelcastConfigMap.containsKey(userId)) {
            log.warn("User {} already has config", userId);

            return;
        }

        log.info("IssueConfig <- update: [{}], configName:[{}]", update, configUuid);

        runIssueScript(shellString)
            .thenRun(
                () -> {
                    File configFile = new File(CONFIG_PATH_BUILDER.apply(userId, configUuid));
                    InputFile inputFile = new InputFile(configFile);

                    SendDocument document = SendDocument.builder()
                        .chatId(update.getMessage().getChatId())
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
                    log.error("An error during issue config has occurred: ", th);

                    return null;
                }
            );
    }

    @Override
    public String getQuery() {
        return "/issueConfig";
    }

    private CompletableFuture<Void> runIssueScript(String shellString) {
        return CompletableFuture.runAsync(
            () -> {
                ProcessBuilder processBuilder = new ProcessBuilder(shellString.split("\\s"));
                Process process = null;

                try {
                    process = processBuilder.start();

                    int processCode = process.waitFor();
                    if (processCode != 0) {
                        throw new RuntimeException("Shell command exited with code " + processCode);
                    }
                } catch (IOException | InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("Error running shell command", e);
                } finally {
                    if (process != null) {
                        process.destroy();
                    }
                }
            }
        );
    }

    @Override
    public String getDescription() {
        return "Выпустить конфиг";
    }

}
