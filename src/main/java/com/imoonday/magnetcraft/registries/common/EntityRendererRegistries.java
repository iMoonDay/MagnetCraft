package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.entities.BackpackEntityModel;
import com.imoonday.magnetcraft.common.entities.bomb.ElectromagneticPulseBombEntityRenderer;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntityModel;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntityRenderer;
import com.imoonday.magnetcraft.common.entities.golem.MagneticIronGolemEntityModel;
import com.imoonday.magnetcraft.common.entities.golem.MagneticIronGolemEntityRenderer;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntityModel;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@Environment(EnvType.CLIENT)
public class EntityRendererRegistries {

    public static final String MAIN = "main";
    public static final EntityModelLayer MODEL_MAGNETIC_IRON_GOLEM_LAYER = registerMain("magnetic_iron_golem");
    public static final EntityModelLayer MODEL_MAGNETIC_WRENCH_LAYER = registerMain("magnetic_wrench");
    public static final EntityModelLayer MODEL_SHUTTLE_ENTRANCE_LAYER = registerMain("shuttle_entrance");
    public static final EntityModelLayer MODEL_BACKPACK_LAYER = registerMain("backpack");

    static EntityModelLayer registerMain(String id) {
        return new EntityModelLayer(id(id), MAIN);
    }

    public static void registerClient() {
        EntityRendererRegistry.register(EntityRegistries.MAGNETIC_IRON_GOLEM, MagneticIronGolemEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MAGNETIC_IRON_GOLEM_LAYER, MagneticIronGolemEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(EntityRegistries.MAGNETIC_WRENCH, MagneticWrenchEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MAGNETIC_WRENCH_LAYER, MagneticWrenchEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(EntityRegistries.ELECTROMAGNETIC_PULSE_BOMB, ElectromagneticPulseBombEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistries.SHUTTLE_ENTRANCE, ShuttleEntranceEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_SHUTTLE_ENTRANCE_LAYER, ShuttleEntranceEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BACKPACK_LAYER, BackpackEntityModel::getTexturedModelData);
    }

}
