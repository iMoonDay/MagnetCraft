package com.imoonday.magnetcraft.common.entities.golem;

import com.imoonday.magnetcraft.registries.common.EntityRendererRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
@Environment(value= EnvType.CLIENT)
public class MagneticIronGolemEntityRenderer extends MobEntityRenderer<MagneticIronGolemEntity, MagneticIronGolemEntityModel> {

    public MagneticIronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MagneticIronGolemEntityModel(context.getPart(EntityRendererRegistries.MODEL_MAGNETIC_IRON_GOLEM_LAYER)), 0.7f);
        this.addFeature(new MagneticIronGolemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(MagneticIronGolemEntity entity) {
        return id("textures/entity/magnetic_iron_golem/magnetic_iron_golem.png");
    }

    @Override
    protected void setupTransforms(MagneticIronGolemEntity entity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(entity, matrixStack, f, g, h);
        double d = 0.01;
        if ((double)entity.limbAnimator.getSpeed() < d) {
            return;
        }
        float i = 13.0f;
        float j = entity.limbAnimator.getPos(h) + 6.0f;
        float k = (Math.abs(j % i - 6.5f) - 3.25f) / 3.25f;
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k));
    }


}
