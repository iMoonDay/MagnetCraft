package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntityModel;
import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@Environment(EnvType.CLIENT)
public class EntityRendererRegistries {

    public static final EntityModelLayer MODEL_MAGNETIC_IRON_GOLEM_LAYER = new EntityModelLayer(id("magnetic_iron_golem"), "main");

    public static void registerClient(){
        EntityRendererRegistry.register(EntityRegistries.MAGNETIC_IRON_GOLEM, MagneticIronGolemEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MAGNETIC_IRON_GOLEM_LAYER, MagneticIronGolemEntityModel::getTexturedModelData);
    }

}