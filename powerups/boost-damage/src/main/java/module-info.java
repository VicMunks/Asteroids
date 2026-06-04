module sdu.asteroids.powerup.boost.damage {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.damage.BoostDamagePlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.damage.BoostDamagePlugin;
}
