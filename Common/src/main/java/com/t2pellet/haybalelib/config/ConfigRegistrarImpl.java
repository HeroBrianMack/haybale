package com.t2pellet.haybalelib.config;

import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.config.api.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ConfigRegistrarImpl implements ConfigRegistrar {

    private final Map<String, Config> configMap;

    ConfigRegistrarImpl() {
        configMap = new HashMap<>();
    }


    @Override
    public <T extends Config> void register(String modid, ConfigSupplier<T> configSupplier) {
        try {
            T config = configSupplier.get();
            config.load();
            config.save();
            configMap.put(modid, config);
        } catch (Exception e) {
            HaybaleLib.LOG.error("Failed to register config for mod: " + modid);
            HaybaleLib.LOG.error(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Config> T get(String modid) {
        return (T) configMap.get(modid);
    }

    @Override
    public Set<String> getAllRegistered() {
        return configMap.keySet();
    }
}