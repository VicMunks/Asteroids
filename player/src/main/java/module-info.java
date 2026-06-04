module sdu.asteroids.player {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.player.PlayerPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.player.PlayerPlugin;
}
