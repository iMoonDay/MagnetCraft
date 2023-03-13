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
        return id("textures/entity/magnetic_iron_golem/magnetic_iron_golem.png");
    }
}
