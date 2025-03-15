package ru.nsu.kosarev.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kosarev.bot.resolver.QueryResolver;

@Slf4j
@Component
@RequiredArgsConstructor
public class VpnBridgeBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final QueryResolver queryResolver;

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            log.info("Received update: {}", update);

            if (update.hasMessage()) {
                String message = update.getMessage().getText();

                queryResolver.getQueryHandler(message).executeQuery(update);
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
        }
    }

    @Override
    public String getBotToken() {
        return "7999258956:AAEkdFcZCiCx2yIeI6Y12XmvtNhMwy79EoI";
    }

}
