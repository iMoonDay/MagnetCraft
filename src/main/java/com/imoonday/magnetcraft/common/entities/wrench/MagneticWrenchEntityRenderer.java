package com.imoonday.magnetcraft.common.entities.wrench;

import com.imoonday.magnetcraft.registries.common.EntityRendererRegistries;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class MagneticWrenchEntityRenderer extends EntityRenderer<MagneticWrenchEntity> {

    public static final Identifier TEXTURE = id("textures/entity/magnetic_wrench.png");
    private final MagneticWrenchEntityModel model;

    public MagneticWrenchEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new MagneticWrenchEntityModel(context.getPart(EntityRendererRegistries.MODEL_MAGNETIC_WRENCH_LAYER));
    }

    @Override
    public void render(MagneticWrenchEntity wrenchEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, wrenchEntity.prevYaw, wrenchEntity.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, wrenchEntity.prevPitch, wrenchEntity.getPitch()) + 90.0f));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(wrenchEntity)), false, wrenchEntity.isEnchanted());
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(wrenchEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(MagneticWrenchEntity tridentEntity) {
        return TEXTURE;
    }

}
