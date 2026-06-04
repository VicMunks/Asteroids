module sdu.asteroids.wave {
    requires sdu.asteroids.common;
    requires java.net.http;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.wave.WavePlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.wave.WavePlugin;
}
