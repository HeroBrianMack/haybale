package com.t2pellet.haybalelib.client;

import com.t2pellet.haybalelib.client.registry.TlibEntityRenderers;
import com.t2pellet.haybalelib.client.registry.TlibParticleFactories;
import com.t2pellet.haybalelib.registry.api.RegistryClass;

public class HaybaleLibClient extends HaybaleLibModClient {

    public static final HaybaleLibClient INSTANCE = new HaybaleLibClient();

    @Override
    public Class<? extends RegistryClass> particleFactories() {
        return TlibParticleFactories.class;
    }

    @Override
    public Class<? extends RegistryClass> entityRenderers() {
        return TlibEntityRenderers.class;
    }
}
