package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.client.HaybaleLibClient;
import com.t2pellet.haybalelib.services.FabricSidedExecutor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

@HaybaleLibMod.IMod(HaybaleLib.MODID)
public class HaybaleLibFabric extends HaybaleLibFabricMod {

    private static MinecraftServer server;

    public static MinecraftServer getServer() {
        return server;
    }

    @Override
    protected HaybaleLibMod getCommonMod() {
        return HaybaleLib.INSTANCE;
    }

    @Override
    protected HaybaleLibModClient getClientMod() {
        return HaybaleLibClient.INSTANCE;
    }

    @Override
    protected void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> HaybaleLibFabric.server = server);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ((FabricSidedExecutor) Services.SIDE).onServerTick(server);
        });
    }
}
