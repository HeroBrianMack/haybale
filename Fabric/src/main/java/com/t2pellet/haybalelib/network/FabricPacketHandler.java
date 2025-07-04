package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.network.api.Packet;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FabricPacketHandler implements IPacketHandler {

    private final Map<Class<? extends Packet>, ResourceLocation> idMap;

    public FabricPacketHandler() {
        idMap = new HashMap<>();
    }

    public <T extends Packet> void registerServerPacket(String modid, String name, Class<? extends Packet> packetClass) {
        ResourceLocation loc = new ResourceLocation(modid, name);
        idMap.put(packetClass, loc);
        CustomPacketPayload.Type<T> type = null;
        try {
            type = (CustomPacketPayload.Type<T>) packetClass.getDeclaredMethod("staticType").invoke(null);
            ServerPlayNetworking.registerGlobalReceiver(type, (var1, var2) -> Services.SIDE.scheduleServer(var1.getExecutor()));
        } catch(NoSuchMethodException | InvocationTargetException | ClassCastException | IllegalAccessException s) {
            HaybaleLib.LOG.error("Error: Failed to instantiate packet - " + loc);
        }

//        ServerPlayNetworking.registerGlobalReceiver(loc, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
//            try {
//                T packet = packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(packetByteBuf);
//                Services.SIDE.scheduleServer(packet.getExecutor());
//            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
//                    InvocationTargetException ex) {
//                HaybaleLib.LOG.error("Error: Failed to instantiate packet - " + loc);
//            }
//        });

    }

    public <T extends Packet> void registerClientPacket(String modid, String name, Class<T> packetClass) {
        ResourceLocation loc = new ResourceLocation(modid, name);
        idMap.put(packetClass, loc);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            _registerClientPacket(loc, packetClass);
        }
    }

    @Environment(EnvType.CLIENT)
    private <T extends Packet> void _registerClientPacket(ResourceLocation id, Class<T> packetClass) {
        CustomPacketPayload.Type<T> type = null;
        try {
            type = (CustomPacketPayload.Type<T>) packetClass.getDeclaredMethod("staticType").invoke(null);

//            PayloadTypeRegistry.playS2C().register(type, (StreamCodec<? super RegistryFriendlyByteBuf, T>) packetClass.getDeclaredField("STREAM_CODEC").get(null));
            ClientPlayNetworking.registerGlobalReceiver(type, (var1, var2) -> Services.SIDE.scheduleClient(var1.getExecutor()));
        } catch(NoSuchMethodException | InvocationTargetException | ClassCastException | IllegalAccessException s) {
            HaybaleLib.LOG.error("Error: Failed to instantiate packet - " + id);
        }
//        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
//            try {
//                T packet = packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
//                Services.SIDE.scheduleClient(packet.getExecutor());
//            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
//                    InvocationTargetException ex) {
//                HaybaleLib.LOG.error("Error: Failed to instantiate packet - " + id);
//            }
//        });
    }

    @Override
    public <T extends Packet> void sendToServer(T packet) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(data);
        ClientPlayNetworking.send(packet);
    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer player) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(data);
        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public <T extends Packet> void sendTo(T packet, ServerPlayer... players) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        ResourceLocation id = idMap.get(packet.getClass());
        packet.encode(data);
        for (ServerPlayer player : players) ServerPlayNetworking.send(player, packet);
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
