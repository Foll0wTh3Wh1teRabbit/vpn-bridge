package ru.nsu.kosarev.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface QueryHandler {

    void executeQuery(Update update) throws IOException;

    String getQuery();

}
