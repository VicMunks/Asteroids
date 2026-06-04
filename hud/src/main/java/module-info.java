module sdu.asteroids.hud {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.hud.HudPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.hud.HudPlugin;
}
