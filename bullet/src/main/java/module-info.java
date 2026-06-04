module sdu.asteroids.bullet {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.bullet.BulletPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.bullet.BulletPlugin;
    provides sdu.asteroids.common.services.BulletSPI
            with sdu.asteroids.bullet.BulletPlugin;
}
