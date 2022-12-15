package com.t2pellet.tlib;

import com.t2pellet.tlib.client.registry.IModEntityModels;
import com.t2pellet.tlib.client.registry.IModParticleFactories;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ClientRegistrar {

    public static void register(String modid, IModEntityModels models) {
        for (Field field : models.getClass().getDeclaredFields()) {
            IModEntityModels.IIEntityModel modelInfo = field.getDeclaredAnnotation(IModEntityModels.IIEntityModel.class);
            if (modelInfo != null && field.getType().equals(IModEntityModels.TLibEntityModel.class)) {
                try {
                    IModEntityModels.TLibEntityModel<? extends Entity> model = (IModEntityModels.TLibEntityModel<? extends Entity>) field.get(null);
                    Field result = model.getClass().getDeclaredField("MODEL");
                    setField(result, model, Services.CLIENT_REGISTRY.registerEntityRenderer(modid, modelInfo.value(), fixType(model._type), model._renderProvider, model._modelProvider, model._modelData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> fixType(EntityType<? extends Entity> type) {
        return (EntityType<T>) type;
    }

    public static void register(String modid, IModParticleFactories particleFactories) {
        for (Field field : particleFactories.getClass().getDeclaredFields()) {
            IModParticleFactories.IParticleFactory particleFactoryInfo = field.getDeclaredAnnotation(IModParticleFactories.IParticleFactory.class);
            if (particleFactoryInfo != null && field.getType().equals(IModParticleFactories.TLibParticleFactory.class)) {
                try {
                    IModParticleFactories.TLibParticleFactory<? extends ParticleOptions> particleFactory = (IModParticleFactories.TLibParticleFactory<? extends ParticleOptions>) field.get(null);
                    registerParticleFactory(particleFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static <T extends ParticleOptions> void registerParticleFactory(IModParticleFactories.TLibParticleFactory<T> particleFactory) {
        Services.CLIENT_REGISTRY.registerParticleFactory(particleFactory._particle, particleFactory._provider);
    }

    private static void setField(Field field, Object object, Object value) {
        try {
            field.setAccessible(true);
            field.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
