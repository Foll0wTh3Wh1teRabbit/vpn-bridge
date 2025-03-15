package ru.nsu.kosarev.bot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.util.MessageClient;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueConfigQueryHandler implements QueryHandler {

    private static final String ISSUE_CONFIG_SCRIPT = "/root/wireguard-automated.sh";

    private static final String CONFIG_MESSAGE = "Ваш конфиг:";

    private static final UnaryOperator<String> PATH_TO_CONFIG_SUPPLIER = uuid -> "/root/wg0-client-" + uuid + ".conf";

    private final MessageClient messageClient;

    @Override
    public void executeQuery(Update update) {
        String configUuid = UUID.randomUUID().toString();
        String shellString = ISSUE_CONFIG_SCRIPT + ' ' + configUuid;

        log.info("IssueConfig <- uuid:[{}]", configUuid);

        runIssueScript(shellString)
            .thenRun(
                () -> {
                    File configFile = new File(PATH_TO_CONFIG_SUPPLIER.apply(configUuid));
                    InputFile inputFile = new InputFile(configFile);

                    SendDocument document = SendDocument.builder()
                        .chatId(update.getMessage().getChatId())
                        .caption(CONFIG_MESSAGE)
                        .document(inputFile)
                        .build();

                    messageClient.sendFile(document);

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

}
