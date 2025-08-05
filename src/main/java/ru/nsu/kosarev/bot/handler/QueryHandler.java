package ru.nsu.kosarev.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface QueryHandler {

    void executeQuery(Update update, List<String> args);

    String getQuery();

    String getDescription();

}
