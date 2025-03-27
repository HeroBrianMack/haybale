package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.entity.capability.api.Capability;
import com.t2pellet.haybalelib.entity.capability.registry.CapabilityRegistrar;
import com.t2pellet.haybalelib.entity.capability.api.registry.IModCapabilities;
import com.t2pellet.haybalelib.network.api.registry.IModPackets;
import com.t2pellet.haybalelib.registry.api.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.util.function.Supplier;

class CommonRegistrar extends BaseRegistrar {

    public static final CommonRegistrar INSTANCE = new CommonRegistrar();

    private CommonRegistrar() {}

    @Override
    protected boolean checkIsValid(Class<?> type) {
        return type.isAssignableFrom(SimpleParticleType.class)
                || type.isAssignableFrom(EntityType.class)
                || type.isAssignableFrom(SoundEvent.class)
                || type.isAssignableFrom(Item.class);
    }

    @Override
    protected void doGenericRegistration(String modid, Class<?> registryType, Field declaredField) throws IllegalAccessException {
        // Get EntryType and register accordingly based on its subclass
        // This is one of two places where we have to do this if / elif block. The other is in EntryType::isValid
        EntryType<?> entryType = (EntryType<?>) declaredField.get(null);
        if (registryType.isAssignableFrom(entryType.type)) {
            Supplier<?> result;
            if (entryType instanceof ParticleEntryType) result = Services.COMMON_REGISTRY.register(modid, (ParticleEntryType) entryType);
            else if (entryType instanceof EntityEntryType<?>) result = Services.COMMON_REGISTRY.register(modid, (EntityEntryType<? extends LivingEntity>) entryType);
            else if (entryType instanceof SoundEntryType) result = Services.COMMON_REGISTRY.register(modid, (SoundEntryType) entryType);
            else if (entryType instanceof ItemEntryType) result = Services.COMMON_REGISTRY.register(modid, (ItemEntryType) entryType);
            else result = null;
            setField("supplier", entryType, result);
        }
    }


    void registerPackets(String modid, IModPackets packets) {
        if (packets == null) return;

        HaybaleLib.LOG.debug("Registering packets for modid: " + modid);
        for (Field field : packets.getClass().getDeclaredFields()) {
            IModPackets.IPacket packetInfo = field.getAnnotation(IModPackets.IPacket.class);
            if (packetInfo != null && field.getType().equals(IModPackets.HaybaleLibPacket.class)) {
                try {
                    IModPackets.HaybaleLibPacket packet = (IModPackets.HaybaleLibPacket) field.get(null);
                    if (packetInfo.client()) {
                        Services.COMMON_REGISTRY.registerClientPacket(modid, packetInfo.name(), packet.getPacketClass());
                    } else {
                        Services.COMMON_REGISTRY.registerServerPacket(modid, packetInfo.name(), packet.getPacketClass());
                    }
                } catch (Exception e) {
                    HaybaleLib.LOG.error("Failed to register packets for " + modid);
                    HaybaleLib.LOG.error(e);
                }
            }
        }
    }

    void registerCapabilities(String modid, IModCapabilities capabilities) {
        if (capabilities == null) return;

        HaybaleLib.LOG.debug("Registering capabilities for modid: " + modid);
        for (Field field : capabilities.getClass().getDeclaredFields()) {
            IModCapabilities.ICapability capabilityInfo = field.getAnnotation(IModCapabilities.ICapability.class);
            if (capabilityInfo != null && field.getType().equals(IModCapabilities.HaybaleLibCapability.class)) {
                try {
                    IModCapabilities.HaybaleLibCapability<?> capability = (IModCapabilities.HaybaleLibCapability<?>) field.get(null);
                    registerCapability(capabilityInfo.value(), capability::get);
                } catch (Exception e) {
                    HaybaleLib.LOG.error("Failed to register capabilities for " + modid);
                    HaybaleLib.LOG.error(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Capability> void registerCapability(Class<? extends Capability> clazz, CapabilityRegistrar.CapabilityFactory<T> supplier) {
        CapabilityRegistrar.INSTANCE.register((Class<T>) clazz, supplier);
    }
}
