package ru.nsu.kosarev.bot.util;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class ProcessClient {

    public CompletableFuture<Void> runScript(String shellString) {
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
