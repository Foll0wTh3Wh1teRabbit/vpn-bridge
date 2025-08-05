package ru.nsu.kosarev.bot.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
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
        Config config = new Config();
        JoinConfig join = config.getNetworkConfig().getJoin();

        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        join.getKubernetesConfig().setEnabled(false);
        join.getAzureConfig().setEnabled(false);
        join.getGcpConfig().setEnabled(false);
        join.getDiscoveryConfig().getDiscoveryStrategyConfigs().clear();

        return Hazelcast.newHazelcastInstance(config);
    }

}
