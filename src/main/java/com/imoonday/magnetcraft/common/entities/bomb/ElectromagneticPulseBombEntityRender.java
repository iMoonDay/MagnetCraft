package com.imoonday.magnetcraft.common.entities.bomb;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ElectromagneticPulseBombEntityRender extends EntityRenderer<ElectromagneticPulseBombEntity> {

    private static final Identifier TEXTURE = id("textures/entity/electromagnetic_pulse_bomb_0.png");

    public ElectromagneticPulseBombEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(ElectromagneticPulseBombEntity entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(ElectromagneticPulseBombEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        String id = "textures/entity/electromagnetic_pulse_bomb_" + getTextRenderer().random.nextBetween(0, 7) + ".png";
        RenderLayer layer = RenderLayer.getEntityCutoutNoCull(id(id));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1, entity.isFlash());
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1, entity.isFlash());
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0, entity.isFlash());
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0, entity.isFlash());
        matrixStack.pop();
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV, boolean drawFlash) {
        vertexConsumer.vertex(positionMatrix, x - 0.5f, (float) y - 0.25f, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(drawFlash ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(ElectromagneticPulseBombEntity entity) {
        return TEXTURE;
    }

}
