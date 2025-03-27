package com.t2pellet.haybalelib.client.registry;

import com.t2pellet.haybalelib.client.particle.TestParticle;
import com.t2pellet.haybalelib.client.registry.api.ParticleFactoryEntryType;
import com.t2pellet.haybalelib.registry.HaybaleLibParticles;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import net.minecraft.core.particles.ParticleType;

@RegistryClass.IRegistryClass(ParticleType.class)
public class TlibParticleFactories implements RegistryClass {

    @RegistryClass.IRegistryEntry
    public static final ParticleFactoryEntryType TEST_PARTICLE = new ParticleFactoryEntryType(HaybaleLibParticles.TEST_PARTICLE, TestParticle.Factory::new);
}
