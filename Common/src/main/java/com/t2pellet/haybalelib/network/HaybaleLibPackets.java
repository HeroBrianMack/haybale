package com.t2pellet.haybalelib.network;

import com.t2pellet.haybalelib.network.api.registry.IModPackets;
import com.t2pellet.haybalelib.network.capability.CapabilityPacket;

public class HaybaleLibPackets implements IModPackets {

    @IPacket(name = "capability", client = true)
    public static final TLibPacket capabilityPacket = new TLibPacket(CapabilityPacket.class);

}
