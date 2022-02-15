package me.refluxo.moduleloader.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {

    private static final Map<Class<? extends Service>, Service> SERVICES = new ConcurrentHashMap<>();

    public static <T extends Service> void registerService(final Class<T> clazz, final T service){
        SERVICES.put(clazz, service);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Service> T access(final Class<T> clazz){
        return (T) SERVICES.get(clazz);
    }
}
