package sdu.asteroids.core;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.util.ServiceLocator;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PluginLoader {

    public void load(GameData gameData, World world) {
        Path pluginsDir = Path.of("plugins");

        Path[] jars = new Path[0];
        if (Files.exists(pluginsDir)) {
            try {
                jars = Files.list(pluginsDir)
                        .filter(p -> p.toString().endsWith(".jar"))
                        .toArray(Path[]::new);
            } catch (IOException ignored) {
            }
        }

        if (jars.length == 0) {
            ServiceLocator.setModuleLayer(ModuleLayer.boot());
            return;
        }

        ModuleFinder finder = ModuleFinder.of(jars);
        List<String> moduleNames = finder.findAll().stream()
                .map(ref -> ref.descriptor().name())
                .toList();

        Configuration config = ModuleLayer.boot().configuration()
                .resolve(finder, ModuleFinder.of(), moduleNames);

        ModuleLayer layer = ModuleLayer.boot()
                .defineModulesWithOneLoader(config, ClassLoader.getSystemClassLoader());

        ServiceLocator.setModuleLayer(layer);

        for (IGamePluginService plugin : ServiceLocator.getServices(IGamePluginService.class)) {
            plugin.start(gameData, world);
        }
    }
}
