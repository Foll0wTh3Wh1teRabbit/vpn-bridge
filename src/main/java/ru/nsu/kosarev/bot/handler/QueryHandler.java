package ru.nsu.kosarev.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface QueryHandler {

    void executeQuery(Update update);

    String getQuery();

}
