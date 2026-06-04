module sdu.asteroids.powerup.help.heal {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.heal.HelpHealPlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.heal.HelpHealPlugin;
}
