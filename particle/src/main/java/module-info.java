module sdu.asteroids.particle {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.particle.ParticlePlugin;
    provides sdu.asteroids.common.services.IPostEntityProcessorService
            with sdu.asteroids.particle.ParticlePlugin;
}
