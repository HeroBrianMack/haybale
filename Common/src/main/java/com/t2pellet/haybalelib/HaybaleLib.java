package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.config.ExampleConfig;
import com.t2pellet.haybalelib.config.api.Config;
import com.t2pellet.haybalelib.network.HaybaleLibPackets;
import com.t2pellet.haybalelib.network.api.registry.IModPackets;
import com.t2pellet.haybalelib.registry.HaybaleLibEntities;
import com.t2pellet.haybalelib.registry.HaybaleLibParticles;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@HaybaleLibMod.IMod(HaybaleLib.MODID)
public class HaybaleLib extends HaybaleLibMod {

    public static final String MODID = "haybalelib";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final HaybaleLib INSTANCE = new HaybaleLib();

    private HaybaleLib() {
    }

    @Override
    public Config config() throws IOException, IllegalAccessException {
        return new ExampleConfig();
    }

    @Override
    public IModPackets packets() {
        return new HaybaleLibPackets();
    }

    @Override
    public Class<? extends RegistryClass> particles() {
        return HaybaleLibParticles.class;
    }

    @Override
    public Class<? extends RegistryClass> entities() {
        return HaybaleLibEntities.class;
    }
}
