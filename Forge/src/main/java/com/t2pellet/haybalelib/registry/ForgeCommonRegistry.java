package com.t2pellet.haybalelib.registry;

import com.t2pellet.haybalelib.HaybaleLibForge;
import com.t2pellet.haybalelib.HaybaleLibForgeMod;
import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.network.ForgePacketHandler;
import com.t2pellet.haybalelib.network.api.Packet;
import com.t2pellet.haybalelib.registry.api.EntityEntryType;
import com.t2pellet.haybalelib.registry.api.ItemEntryType;
import com.t2pellet.haybalelib.registry.api.ParticleEntryType;
import com.t2pellet.haybalelib.registry.api.SoundEntryType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeCommonRegistry implements ICommonRegistry {

    @Override
    public Supplier<SimpleParticleType> register(String modid, ParticleEntryType particleEntryType) {
        HaybaleLibForgeMod forgeMod = HaybaleLibForge.getInstance().get(modid);
        return forgeMod.PARTICLES.register(particleEntryType.getName(), () -> new SimpleParticleType(true));
    }

    @Override
    public <T extends LivingEntity> Supplier<EntityType<T>> register(String modid, EntityEntryType<T> entityEntryType) {
        HaybaleLibForgeMod forgeMod = HaybaleLibForge.getInstance().get(modid);
        RegistryObject<EntityType<T>> result = forgeMod.ENTITIES.register(entityEntryType.getName(), () -> EntityType.Builder.of(entityEntryType.getFactory(), entityEntryType.getMobCategory())
                .clientTrackingRange(48).updateInterval(3).sized(entityEntryType.getWidth(), entityEntryType.getHeight())
                .build(entityEntryType.getName()));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<EntityAttributeCreationEvent>) event -> event.put(result.get(), entityEntryType.buildAttributes()));
        return result;
    }

    @Override
    public Supplier<SoundEvent> register(String modid, SoundEntryType soundEntryType) {
        ResourceLocation location = ResourceLocation.tryBuild(modid, soundEntryType.getName());
        HaybaleLibForgeMod forgeMod = HaybaleLibForge.getInstance().get(modid);
        return forgeMod.SOUNDS.register(soundEntryType.getName(), () -> SoundEvent.createVariableRangeEvent(location));
    }

    @Override
    public Supplier<Item> register(String modid, ItemEntryType itemEntryType) {
        HaybaleLibForgeMod forgeMod = HaybaleLibForge.getInstance().get(modid);
        return forgeMod.ITEMS.register(itemEntryType.getName(), () -> new Item(itemEntryType.getProperties()));
    }

    @Override
    public void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        ForgePacketHandler packetHandler = (ForgePacketHandler) Services.PACKET_HANDLER;
        packetHandler.registerServerPacket(modid, name, packetClass);
    }

    @Override
    public void registerClientPacket(String modid, String name, Class<? extends Packet> packetClass) {
        ForgePacketHandler packetHandler = (ForgePacketHandler) Services.PACKET_HANDLER;
        packetHandler.registerClientPacket(modid, name, packetClass);
    }
}
