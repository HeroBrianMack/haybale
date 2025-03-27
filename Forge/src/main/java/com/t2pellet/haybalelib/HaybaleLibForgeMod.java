package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.client.compat.ConfigMenu;
import com.t2pellet.haybalelib.config.ConfigRegistrar;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class HaybaleLibForgeMod {

    // Mod references
    private final String modid;
    private final HaybaleLibMod commonMod;
    private final HaybaleLibModClient clientMod;

    // Deferred registries
    public final DeferredRegister<EntityType<?>> ENTITIES;
    public final DeferredRegister<ParticleType<?>> PARTICLES;
    public final DeferredRegister<SoundEvent> SOUNDS;
    public final DeferredRegister<Item> ITEMS;

    public HaybaleLibForgeMod() {
        initialSetup();
        HaybaleLibMod.IMod modAnnotation = getClass().getAnnotation(HaybaleLibMod.IMod.class);
        commonMod = getCommonMod();
        clientMod = getClientMod();
        modid = modAnnotation.value();
        HaybaleLibForge.getInstance().register(modid, this);
        // Create deferred registers
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modid);
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
        PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, modid);
        SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modid);
        // Common init
        onCommonSetup();
        // Client init
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onClientSetup);
        // Register into deferred registers
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        SOUNDS.register(bus);
        // Events
        registerEvents();
    }

    // If you want to call any custom logic in your mod BEFORE any TLib stuff
    protected void initialSetup() {}

    protected abstract HaybaleLibMod getCommonMod();
    protected abstract HaybaleLibModClient getClientMod();

    protected void registerEvents() {
    }

    private void onCommonSetup() {
        if (commonMod != null) {
            CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.particles());
            CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.entities());
            CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.items());
            CommonRegistrar.INSTANCE.registerFromClass(modid, commonMod.sounds());
            CommonRegistrar.INSTANCE.registerPackets(modid, commonMod.packets());
            CommonRegistrar.INSTANCE.registerCapabilities(modid, commonMod.capabilities());
            ConfigRegistrar.INSTANCE.register(modid, commonMod::config);
        }
    }

    private void onClientSetup() {
        if (clientMod != null) {
            ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.entityModels());
            ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.entityRenderers());
            ClientRegistrar.INSTANCE.registerFromClass(modid, clientMod.particleFactories());
        }
        // I have no idea why their modid is different in forge
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            System.out.println("CLOTH LOADED");
            ConfigMenu configMenu = new ConfigMenu(modid);
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> {
                        System.out.println("REGISTERED CONFIG FACTORY");
                        return new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> configMenu.buildConfigScreen());
                    });
        }
    }



}
