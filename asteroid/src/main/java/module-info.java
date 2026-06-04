module sdu.asteroids.asteroid {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.asteroid.AsteroidPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.asteroid.AsteroidPlugin;
    provides sdu.asteroids.common.services.AsteroidSplittingSPI
            with sdu.asteroids.asteroid.AsteroidPlugin;
    provides sdu.asteroids.common.services.AsteroidSpawnSPI
            with sdu.asteroids.asteroid.AsteroidPlugin;
}
