package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.client.HaybaleLibClient;
import com.t2pellet.haybalelib.servicesForgeHay.ForgeSidedExecutor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod(HaybaleLib.MODID)
@HaybaleLibMod.IMod(HaybaleLib.MODID)
public class HaybaleLibForge extends HaybaleLibForgeMod {

    private static HaybaleLibForge instance = null;
    public static HaybaleLibForge getInstance() {
        return instance;
    }

    private Map<String, HaybaleLibForgeMod> modMap;

    @Override
    protected void initialSetup() {
        instance = this;
        modMap = new HashMap<>();
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
        MinecraftForge.EVENT_BUS.addListener(((ForgeSidedExecutor) Services.SIDE)::onServerTick);
    }

    public void register(String id, HaybaleLibForgeMod mod) {
        modMap.put(id, mod);
    }

    public HaybaleLibForgeMod get(String modid) {
        return modMap.get(modid);
    }
}
