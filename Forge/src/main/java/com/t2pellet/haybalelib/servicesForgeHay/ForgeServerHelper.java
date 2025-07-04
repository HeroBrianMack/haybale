package com.t2pellet.haybalelib.servicesForgeHay;

import com.t2pellet.haybalelib.services.IServerHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ForgeServerHelper implements IServerHelper {
    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
