module sdu.asteroids.powerup.weapon.piercing {
    requires sdu.asteroids.common;
    requires sdu.asteroids.powerup.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.powerup.piercing.WeaponPiercingPlugin;
    provides sdu.asteroids.common.services.PowerupSPI
        with sdu.asteroids.powerup.piercing.WeaponPiercingPlugin;
    provides sdu.asteroids.common.services.WeaponModifierSPI
        with sdu.asteroids.powerup.piercing.WeaponPiercingPlugin;
}
