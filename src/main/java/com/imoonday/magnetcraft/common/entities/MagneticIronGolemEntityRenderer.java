package com.imoonday.magnetcraft.common.entities;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class MagneticIronGolemEntityRenderer extends IronGolemEntityRenderer {
    public MagneticIronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(IronGolemEntity ironGolemEntity) {
        String id = ironGolemEntity instanceof MagneticIronGolemEntity entity && entity.isHasLodestone() ? "magnetic_iron_golem_with_lodestone" : "magnetic_iron_golem";
        return id("textures/entity/magnetic_iron_golem/" + id + ".png");
    }
}
