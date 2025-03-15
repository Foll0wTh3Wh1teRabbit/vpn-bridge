package ru.nsu.kosarev.bot.resolver;

public class QueryHandlerNotFoundException extends RuntimeException {
    
    private static final String MESSAGE = "Matching query handler not found";

    public QueryHandlerNotFoundException() {
        super(MESSAGE);
    }
    
}
