package ru.nsu.kosarev.bot.resolver;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nsu.kosarev.bot.handler.QueryHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryResolver {

    private static final Map<String, QueryHandler> queryHandlers = new HashMap<>();

    private final List<QueryHandler> handlers;

    @PostConstruct
    public void initHandlers() {
        handlers.forEach(queryHandler -> queryHandlers.put(queryHandler.getQuery(), queryHandler));
    }

    public QueryHandler getQueryHandler(String query) {
        return Optional.ofNullable(queryHandlers.get(query)).orElseThrow(QueryHandlerNotFoundException::new);
    }

}
