package com.t2pellet.haybalelib.network.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class Packet implements CustomPacketPayload {

    protected CompoundTag tag;

    public Packet(FriendlyByteBuf byteBuf) {
        this.tag = byteBuf.readNbt();
    }

    public Packet() {
        tag = new CompoundTag();
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(tag);
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(tag);
    }

    public abstract Runnable getExecutor();

}
