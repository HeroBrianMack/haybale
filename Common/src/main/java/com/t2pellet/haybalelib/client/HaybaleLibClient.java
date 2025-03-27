package com.t2pellet.haybalelib.client;

import com.t2pellet.haybalelib.client.registry.HaybaleLibEntityRenderers;
import com.t2pellet.haybalelib.client.registry.HaybaleLibParticleFactories;
import com.t2pellet.haybalelib.registry.api.RegistryClass;

public class HaybaleLibClient extends HaybaleLibModClient {

    public static final HaybaleLibClient INSTANCE = new HaybaleLibClient();

    @Override
    public Class<? extends RegistryClass> particleFactories() {
        return HaybaleLibParticleFactories.class;
    }

    @Override
    public Class<? extends RegistryClass> entityRenderers() {
        return HaybaleLibEntityRenderers.class;
    }
}
