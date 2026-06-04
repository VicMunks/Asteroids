package sdu.asteroids.common.data;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public List<Entity> getEntities() {
        return List.copyOf(entities);
    }

    public List<Entity> getEntities(EntityType t) {
        return entities.stream()
                .filter(e -> e.getType() == t)
                .toList();
    }

    public void cleanUp() {
        entities.removeIf(e -> !e.isActive());
    }
}
