module sdu.asteroids.screens {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.screens.ScreensPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.screens.ScreensPlugin;
}
