module sdu.asteroids.audio {
    requires sdu.asteroids.common;
    requires javafx.media;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.audio.AudioPlugin;
    provides sdu.asteroids.common.services.IPostEntityProcessorService
            with sdu.asteroids.audio.AudioPlugin;
}
