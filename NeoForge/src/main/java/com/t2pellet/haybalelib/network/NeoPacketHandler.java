package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.network.api.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class NeoPacketHandler implements IPacketHandler {

    private final String PROTOCOL_VERSION = "4";
    private final Map<ResourceLocation, Integer> idMap = new HashMap<>();
    private final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(HaybaleLib.MODID, "main"))
            .clientAcceptedVersions((tmp) -> true)
            .serverAcceptedVersions((tmp1) -> true)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    public void registerClientPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    private <T extends Packet> void registerPacket(String modid, String name, Class<T> packetClass) {
        ResourceLocation id = new ResourceLocation(modid, name);
        // changed in 1.20.2+!
        INSTANCE.messageBuilder(packetClass, idMap.get(id)).encoder(Packet::encode).decoder(friendlyByteBuf -> {
            try {
                return packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(friendlyByteBuf);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                HaybaleLib.LOG.error("Error: Failed to instantiate packet - " + id);
            }
            return null;
        }).consumerNetworkThread((t, contextSupplier) -> {
            if (contextSupplier.getDirection().getReceptionSide().isClient()) {
                Services.SIDE.scheduleClient(t.getExecutor());
            } else {
                Services.SIDE.scheduleServer(t.getExecutor());
            }
            contextSupplier.setPacketHandled(true);
        }).add();
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer... players) {
        for (ServerPlayer player : players) {
            sendTo(packet, player);
        }
    }

    @Override
    public <T extends Packet> void sendInRange(T packet, Entity e, float range) {
        AABB box = new AABB(e.blockPosition()).inflate(range);
        sendInArea(packet, e.level(), box);
    }

    @Override
    public <T extends Packet> void sendInArea(T packet, Level world, AABB area) {
        ServerPlayer[] players = ((ServerLevel) world).players().stream().filter((p) -> area.contains(p.position())).toArray(ServerPlayer[]::new);
        sendTo(packet, players);
    }
}