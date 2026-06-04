package sdu.asteroids.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class ServiceLocator {
    private static ModuleLayer moduleLayer;
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();
    private static final Map<Class<?>, List<?>> listCache = new HashMap<>();

    public static <T> T getService(Class<T> serviceClass) {
        Collection<T> all = getServices(serviceClass);
        return all.isEmpty() ? null : all.iterator().next();
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getServices(Class<T> serviceClass) {
        List<T> cached = (List<T>) listCache.get(serviceClass);
        if (cached != null) {
            return cached;
        }
        List<T> result = ServiceLoader.load(moduleLayer, serviceClass)
                .stream()
                .map(provider -> {
                    Class<?> concreteType = provider.type();
                    T existing = (T) instanceCache.get(concreteType);
                    if (existing != null) {
                        return existing;
                    }
                    T instance = provider.get();
                    instanceCache.put(concreteType, instance);
                    return instance;
                })
                .toList();
        listCache.put(serviceClass, result);
        return result;
    }

    public static void setModuleLayer(ModuleLayer layer) {
        moduleLayer = layer;
        instanceCache.clear();
        listCache.clear();
    }

    public static <T> void registerForTesting(Class<T> type, T instance) {
        instanceCache.put(type, instance);
        listCache.put(type, List.of(instance));
    }
}
