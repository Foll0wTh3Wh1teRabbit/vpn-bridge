package ru.nsu.kosarev.bot.handler.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCheckerService {

    private static final List<Long> ADMINS = List.of(446488200L);

    public boolean isAdmin(Long userId) {
        return ADMINS.contains(userId);
    }

}
