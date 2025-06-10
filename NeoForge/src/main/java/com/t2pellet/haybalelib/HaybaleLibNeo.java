package com.t2pellet.haybalelib;

import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.client.HaybaleLibClient;
import com.t2pellet.haybalelib.services.NeoSidedExecutor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod(HaybaleLib.MODID)
@HaybaleLibMod.IMod(HaybaleLib.MODID)
public class HaybaleLibNeo extends HaybaleLibNeoMod {

    private static HaybaleLibNeo instance = null;
    public static HaybaleLibNeo getInstance() {
        return instance;
    }

    private Map<String, HaybaleLibNeoMod> modMap;

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
        NeoForge.EVENT_BUS.addListener(((NeoSidedExecutor) Services.SIDE)::onServerTick);
    }

    public void register(String id, HaybaleLibNeoMod mod) {
        modMap.put(id, mod);
    }

    public HaybaleLibNeoMod get(String modid) {
        return modMap.get(modid);
    }
}
