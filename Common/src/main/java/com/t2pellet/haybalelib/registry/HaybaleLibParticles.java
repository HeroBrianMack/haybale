package com.t2pellet.haybalelib.registry;

import com.t2pellet.haybalelib.registry.api.ParticleEntryType;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import net.minecraft.core.particles.SimpleParticleType;

@RegistryClass.IRegistryClass(SimpleParticleType.class)
public class HaybaleLibParticles implements RegistryClass {

    @RegistryClass.IRegistryEntry
    public static final ParticleEntryType TEST_PARTICLE = new ParticleEntryType("test");
}
