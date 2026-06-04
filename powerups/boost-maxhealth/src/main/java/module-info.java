module sdu.asteroids.powerup.boost.maxhealth {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.maxhealth.BoostMaxHealthPlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.maxhealth.BoostMaxHealthPlugin;
}
