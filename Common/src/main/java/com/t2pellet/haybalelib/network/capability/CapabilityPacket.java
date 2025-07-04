package com.t2pellet.haybalelib.network.capability;

import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.entity.capability.api.Capability;
import com.t2pellet.haybalelib.entity.capability.api.ICapabilityHaver;
import com.t2pellet.haybalelib.network.api.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static com.t2pellet.haybalelib.HaybaleLib.LOG;

public class CapabilityPacket<E extends Entity & ICapabilityHaver> extends Packet {

    public E getCapabilityHaver() {
        return capabilityHaver;
    }

    private E capabilityHaver;

    public Class<? extends Capability> getClazz() {
        return clazz;
    }

    private Class<? extends Capability> clazz;
    public static final CustomPacketPayload.Type<CapabilityPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(HaybaleLib.MODID, "capability"));
    public static final StreamCodec<FriendlyByteBuf, CapabilityPacket> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, CapabilityPacket>() {
        @Override
        public CapabilityPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new CapabilityPacket(friendlyByteBuf);
        }

        @Override
        public void encode(FriendlyByteBuf o, CapabilityPacket capabilityPacket) {
            capabilityPacket.encode(o);
        }
    };

    public CapabilityPacket(E capabilityHaver, Class<? extends Capability> clazz) {
        super();
        this.capabilityHaver = capabilityHaver;
        this.clazz = (Class<? extends Capability>) clazz.getInterfaces()[0];
    }

    public CapabilityPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
        String classStr = tag.getString("class");
        try {
            this.clazz = (Class<? extends Capability>) Class.forName(classStr);
        } catch (ClassNotFoundException e) {
            LOG.error("Capability Packet " + classStr + " Class not found!");
        }
    }

    @Override
    public Runnable getExecutor() {
        return () -> {
            Tag data = tag.get("data");
            int id = tag.getInt("entity");
            this.capabilityHaver = (E) Minecraft.getInstance().level.getEntity(id);
            capabilityHaver.getCapabilityManager().getCapability(clazz).readTag(data);
        };
    }

    @Override
    public void encode(FriendlyByteBuf byteBuf) {
        Capability capability = capabilityHaver.getCapabilityManager().getCapability(clazz);
        tag.putInt("entity", capabilityHaver.getId());
        tag.putString("class", clazz.getName());
        tag.put("data", capability.writeTag());
        super.encode(byteBuf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type(new ResourceLocation(HaybaleLib.MODID, "capability"));
    }

    public static Type<? extends CustomPacketPayload> staticType() {
        return new Type(new ResourceLocation(HaybaleLib.MODID, "capability"));
    }

}
