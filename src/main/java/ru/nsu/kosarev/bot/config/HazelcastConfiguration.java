package ru.nsu.kosarev.bot.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public IMap<Long, List<File>> hazelcastConfigMap() {
        return hazelcastInstance().getMap("configs");
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

}
