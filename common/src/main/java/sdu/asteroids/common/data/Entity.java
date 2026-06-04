package sdu.asteroids.common.data;

import java.util.UUID;

public class Entity {
    private String id;
    private EntityType type;
    private EntityType owner;
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double rotation;
    private double rotationSpeed;
    private double radius;
    private double speed;
    private int health;
    private double damage;
    private IShape shape;
    private double lifetime;
    private boolean active;
    private int pierceRemaining = 0;
    private double explosionRadius = 0.0;

    public Entity() {
        this.id = UUID.randomUUID().toString();
        this.lifetime = -1.0;
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public EntityType getType() { return type; }
    public void setType(EntityType type) { this.type = type; }

    public EntityType getOwner() { return owner; }
    public void setOwner(EntityType owner) { this.owner = owner; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }

    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }

    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }

    public double getRotationSpeed() { return rotationSpeed; }
    public void setRotationSpeed(double rotationSpeed) { this.rotationSpeed = rotationSpeed; }

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    public IShape getShape() { return shape; }
    public void setShape(IShape shape) { this.shape = shape; }

    public double getLifetime() { return lifetime; }
    public void setLifetime(double lifetime) { this.lifetime = lifetime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getPierceRemaining() { return pierceRemaining; }
    public void setPierceRemaining(int pierceRemaining) { this.pierceRemaining = pierceRemaining; }

    public double getExplosionRadius() { return explosionRadius; }
    public void setExplosionRadius(double explosionRadius) { this.explosionRadius = explosionRadius; }
}
