package com.t2pellet.haybalelib.client;

import com.t2pellet.haybalelib.registry.api.RegistryClass;

public abstract class HaybaleLibModClient {

    public Class<? extends RegistryClass> entityModels() { return null; }

    public Class<? extends RegistryClass> entityRenderers() { return null; }

    public Class<? extends RegistryClass> particleFactories() { return null; }

}
