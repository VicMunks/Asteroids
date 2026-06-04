module sdu.asteroids.powerup.boost.speed {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.speed.BoostSpeedPlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.speed.BoostSpeedPlugin;
}
