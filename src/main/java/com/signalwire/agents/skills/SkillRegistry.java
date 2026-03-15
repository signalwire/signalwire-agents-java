package com.signalwire.agents.skills;

import com.signalwire.agents.logging.Logger;
import com.signalwire.agents.skills.builtin.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Static registry of all available skills.
 * Skills are registered by name and can be instantiated on demand.
 */
public final class SkillRegistry {

    private static final Logger log = Logger.getLogger(SkillRegistry.class);
    private static final Map<String, Supplier<SkillBase>> registry = new ConcurrentHashMap<>();

    static {
        // Register built-in skills that are implemented
        register("datetime", DatetimeSkill::new);
        register("math", MathSkill::new);
        register("joke", JokeSkill::new);
        register("weather_api", WeatherApiSkill::new);
    }

    private SkillRegistry() {}

    /**
     * Register a skill factory.
     */
    public static void register(String name, Supplier<SkillBase> factory) {
        registry.put(name, factory);
        log.debug("Registered skill: %s", name);
    }

    /**
     * Get a new instance of a skill by name.
     */
    public static SkillBase get(String name) {
        Supplier<SkillBase> factory = registry.get(name);
        if (factory == null) {
            return null;
        }
        return factory.get();
    }

    /**
     * Check if a skill is registered.
     */
    public static boolean has(String name) {
        return registry.containsKey(name);
    }

    /**
     * List all registered skill names.
     */
    public static Set<String> list() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    /**
     * Unregister a skill (for testing).
     */
    public static void unregister(String name) {
        registry.remove(name);
    }
}
