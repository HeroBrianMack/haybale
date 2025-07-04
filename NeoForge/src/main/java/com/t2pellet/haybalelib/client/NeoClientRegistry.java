package com.t2pellet.haybalelib.client;

import com.t2pellet.haybalelib.client.registry.IClientRegistry;
import com.t2pellet.haybalelib.client.registry.api.EntityModelEntryType;
import com.t2pellet.haybalelib.client.registry.api.EntityRendererEntryType;
import com.t2pellet.haybalelib.client.registry.api.ParticleFactoryEntryType;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoClientRegistry implements IClientRegistry {

    @Override
    public Supplier<ParticleType<SimpleParticleType>> register(String modid, ParticleFactoryEntryType particleFactoryEntry) {
        System.out.println("Called to register particle!");
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<RegisterParticleProvidersEvent>) particleFactoryRegisterEvent -> {
            System.out.println("REGISTER SPRITE SET");
            particleFactoryRegisterEvent.registerSpriteSet(particleFactoryEntry.get(), spriteSet -> particleFactoryEntry.getProviderFunction().apply(spriteSet));
        });
        return particleFactoryEntry::get;
    }

    @Override
    public Supplier<ModelLayerLocation> register(String modid, EntityModelEntryType modelEntry) {
        Lazy<ModelLayerLocation> locSupplier = Lazy.of(() -> (new ModelLayerLocation(new ResourceLocation(modid, modelEntry.getName()), "main")));
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> {
            event.registerLayerDefinition(locSupplier.get(), modelEntry::getLayerDefinition);
        });
        return locSupplier;
    }

    @Override
    public <T extends Entity> Supplier<EntityRendererProvider<T>> register(String modid, EntityRendererEntryType<T> rendererEntry) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> {
            event.registerEntityRenderer(rendererEntry.getEntityType(), rendererEntry.getRendererProvider());
        });
        return rendererEntry::getRendererProvider;
    }
}
