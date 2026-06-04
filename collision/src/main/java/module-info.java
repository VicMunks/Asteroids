module sdu.asteroids.collision {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IPostEntityProcessorService
            with sdu.asteroids.collision.CollisionPlugin;
}
