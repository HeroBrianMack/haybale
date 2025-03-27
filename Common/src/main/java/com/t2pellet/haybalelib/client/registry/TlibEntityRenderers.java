package com.t2pellet.haybalelib.client.registry;

import com.t2pellet.haybalelib.client.registry.api.EntityRendererEntryType;
import com.t2pellet.haybalelib.registry.HaybaleLibEntities;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Cow;

@RegistryClass.IRegistryClass(EntityRendererProvider.class)
public class TlibEntityRenderers implements RegistryClass {

    @RegistryClass.IRegistryEntry
    public static final EntityRendererEntryType<Cow> COW_RENDERER = new EntityRendererEntryType<>(HaybaleLibEntities.TEST_ENTITY::get, CowRenderer::new);
}
