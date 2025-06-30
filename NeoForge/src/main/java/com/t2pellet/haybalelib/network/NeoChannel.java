package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.HaybaleLib;
import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.network.api.Packet;
import com.t2pellet.haybalelib.network.capability.CapabilityPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class NeoChannel {

    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(HaybaleLib.MODID)
                .versioned("1.0")
                .optional();
        for (Class<? extends Packet> packet : NeoPacketHandler.packets.keySet()) {
            String[] info = NeoPacketHandler.packets.get(packet);
            registrar.play(new ResourceLocation(info[0], info[1]), friendlyByteBuf -> {try {
                return packet.getConstructor(FriendlyByteBuf.class).newInstance(friendlyByteBuf);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to instantiate packet: " + packet, e);
            }}, handler -> handler.client((t, contextSupplier) -> {
                if (contextSupplier.flow().getReceptionSide().isClient()) {
                    Services.SIDE.scheduleClient(t.getExecutor());
                } else {
                    Services.SIDE.scheduleServer(t.getExecutor());
                }
            }));
        }
    }
}
