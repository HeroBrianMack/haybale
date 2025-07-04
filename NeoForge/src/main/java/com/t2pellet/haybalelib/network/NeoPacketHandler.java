package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.network.api.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class NeoPacketHandler implements IPacketHandler {

    private final Map<ResourceLocation, Integer> idMap = new HashMap<>();
    static final Map<Class<? extends Packet>, String[]> packets = new HashMap<>();
    public void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, "Server", packetClass);
    }

    public void registerClientPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, "Client", packetClass);
    }

    private <T extends Packet> void registerPacket(String modid, String name, String side, Class<? extends Packet> packetClass) {
//        ResourceLocation id = new ResourceLocation(modid, name);
        // Side is unused, but potentially useful in the future...
        // 1.20.5+ This serves zero purpose...
        String[] str = packets.put(packetClass, new String[] {modid, name, side});
        if (str != null) {
            HaybaleLib.LOG.error("Error: Overwritten instantiation of packet - " + str[1]);
        }
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);

    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
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