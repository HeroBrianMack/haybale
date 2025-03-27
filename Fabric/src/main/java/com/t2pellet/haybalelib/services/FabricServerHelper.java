package com.t2pellet.haybalelib.services;

import com.t2pellet.haybalelib.HaybaleLibFabric;
import net.minecraft.server.MinecraftServer;

public class FabricServerHelper implements IServerHelper {

    @Override
    public MinecraftServer getServer() {
        return HaybaleLibFabric.getServer();
    }
}
