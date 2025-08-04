package ru.nsu.kosarev.bot.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCheckerService {

    private static final List<Long> ADMIN_IDS = List.of(446488200L);

    public boolean isAdmin(String id) {
        return ADMIN_IDS.contains(Long.parseLong(id));
    }

}
