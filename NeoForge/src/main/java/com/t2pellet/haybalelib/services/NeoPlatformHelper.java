package com.t2pellet.haybalelib.services;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public class NeoPlatformHelper implements IPlatformHelper {

    @Override
    public String getGameDir() {
        return FMLPaths.GAMEDIR.get().toString();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
