package com.t2pellet.haybalelib.registry;

import com.t2pellet.haybalelib.HaybaleLibNeo;
import com.t2pellet.haybalelib.HaybaleLibNeoMod;
import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.network.NeoChannel;
import com.t2pellet.haybalelib.network.NeoPacketHandler;
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
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoCommonRegistry implements ICommonRegistry {

    @Override
    public Supplier<SimpleParticleType> register(String modid, ParticleEntryType particleEntryType) {
        HaybaleLibNeoMod forgeMod = HaybaleLibNeo.getInstance().get(modid);
        return forgeMod.PARTICLES.register(particleEntryType.getName(), () -> new SimpleParticleType(true));
    }

    @Override
    public <T extends LivingEntity> Supplier<EntityType<T>> register(String modid, EntityEntryType<T> entityEntryType) {
        HaybaleLibNeoMod forgeMod = HaybaleLibNeo.getInstance().get(modid);
        Supplier<EntityType<T>> result = forgeMod.ENTITIES.register(entityEntryType.getName(), () -> EntityType.Builder.of(entityEntryType.getFactory(), entityEntryType.getMobCategory())
                .clientTrackingRange(48).updateInterval(3).sized(entityEntryType.getWidth(), entityEntryType.getHeight())
                .build(entityEntryType.getName()));
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<EntityAttributeCreationEvent>) event -> event.put(result.get(), entityEntryType.buildAttributes()));
        return result;
    }

    @Override
    public Supplier<SoundEvent> register(String modid, SoundEntryType soundEntryType) {
        ResourceLocation location = new ResourceLocation(modid, soundEntryType.getName());
        HaybaleLibNeoMod forgeMod = HaybaleLibNeo.getInstance().get(modid);
        return forgeMod.SOUNDS.register(soundEntryType.getName(), () -> SoundEvent.createVariableRangeEvent(location));
    }

    @Override
    public Supplier<Item> register(String modid, ItemEntryType itemEntryType) {
        HaybaleLibNeoMod forgeMod = HaybaleLibNeo.getInstance().get(modid);
        return forgeMod.ITEMS.register(itemEntryType.getName(), () -> new Item(itemEntryType.getProperties()));
    }

    @Override
    public void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        NeoPacketHandler packetHandler = (NeoPacketHandler) Services.PACKET_HANDLER;
        packetHandler.registerServerPacket(modid, name, packetClass);
    }

    @Override
    public void registerClientPacket(String modid, String name, Class<? extends Packet> packetClass) {
        NeoPacketHandler packetHandler = (NeoPacketHandler) Services.PACKET_HANDLER;
        packetHandler.registerClientPacket(modid, name, packetClass);
    }
}
