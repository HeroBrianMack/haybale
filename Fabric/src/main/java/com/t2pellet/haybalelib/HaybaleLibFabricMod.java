package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.config.ConfigRegistrar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public abstract class HaybaleLibFabricMod implements ModInitializer, ClientModInitializer {

    private final String modid;
    private final HaybaleLibMod commonMod;
    private final HaybaleLibModClient clientMod;

    public HaybaleLibFabricMod() {
        HaybaleLibMod.IMod modAnnotation = getClass().getAnnotation(HaybaleLibMod.IMod.class);
        commonMod = getCommonMod();
        clientMod = getClientMod();
        modid = modAnnotation.value();
    }

    protected abstract HaybaleLibMod getCommonMod();
    protected abstract HaybaleLibModClient getClientMod();

    @Override
    public void onInitializeClient() {
        ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.particleFactories());
        ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.entityModels());
        ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.entityRenderers());
    }

    @Override
    public void onInitialize() {
        ConfigRegistrar.INSTANCE.register(modid, commonMod::config);
        CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.entities());
        CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.items());
        CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.particles());
        CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.sounds());
        CommonRegistrar.INSTANCE.registerPackets(modid, commonMod.packets());
        CommonRegistrar.INSTANCE.registerCapabilities(modid, commonMod.capabilities());
        registerEvents();
    }

    protected void registerEvents() {}
}
