package sdu.asteroids.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.IPostEntityProcessorService;
import sdu.asteroids.common.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class GameConfig {

    @Bean
    public List<IGamePluginService> gamePlugins() {
        return new ArrayList<>(ServiceLocator.getServices(IGamePluginService.class));
    }

    @Bean
    public List<IEntityProcessorService> entityProcessors() {
        return new ArrayList<>(ServiceLocator.getServices(IEntityProcessorService.class));
    }

    @Bean
    public List<IPostEntityProcessorService> postProcessors() {
        return new ArrayList<>(ServiceLocator.getServices(IPostEntityProcessorService.class));
    }

    @Bean
    public Game game(List<IGamePluginService> gamePlugins,
                     List<IEntityProcessorService> entityProcessors,
                     List<IPostEntityProcessorService> postProcessors) {
        return new Game(gamePlugins, entityProcessors, postProcessors);
    }
}
