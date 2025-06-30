package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.network.api.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ForgePacketHandler implements IPacketHandler {

    private final int PROTOCOL_VERSION = 4;
    private final Map<ResourceLocation, Integer> idMap = new HashMap<>();
    private final SimpleChannel INSTANCE = ChannelBuilder.named(
            ResourceLocation.tryBuild(HaybaleLib.MODID, "main"))
            .clientAcceptedVersions((tmp, tmp2) -> true)
            .serverAcceptedVersions((tmp1, tmp2) -> true)
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();

    public void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(ResourceLocation.tryBuild(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    public void registerClientPacket(String modid, String name, Class<? extends Packet> packetClass) {
        idMap.put(ResourceLocation.tryBuild(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    private <T extends Packet> void registerPacket(String modid, String name, Class<T> packetClass) {
        ResourceLocation id = ResourceLocation.tryBuild(modid, name);
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
        // Changing this, as 1.20.2+ will change
        if (INSTANCE.isRemotePresent(Minecraft.getInstance().getConnection().getConnection())) {
            INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
        }
    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
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
