module sdu.asteroids.powerup.help.shield {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.shield.HelpShieldPlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.shield.HelpShieldPlugin;
}
