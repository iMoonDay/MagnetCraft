package com.imoonday.magnetcraft.common.entities.entrance;

import com.imoonday.magnetcraft.registries.common.EntityRendererRegistries;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ShuttleEntranceEntityRenderer extends EntityRenderer<ShuttleEntranceEntity> {


    public static final Identifier TEXTURE = id("textures/entity/shuttle_entrance.png");
    private final ShuttleEntranceEntityModel model;

    public ShuttleEntranceEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ShuttleEntranceEntityModel(ctx.getPart(EntityRendererRegistries.MODEL_SHUTTLE_ENTRANCE_LAYER));
    }

    @Override
    public void render(ShuttleEntranceEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        matrixStack.scale(1.5f, 1.5f, 1.5f);
        matrixStack.translate(0, -0.2f, 0);
        float entityYaw = entity.getYaw();
        if (entityYaw * entity.prevYaw < 0 && Math.abs(entityYaw - entity.prevYaw) > 180) {
            entityYaw = entityYaw < 0 ? 180 : -180;
        }
        float lerp = MathHelper.lerp(tickDelta, entity.prevYaw, entityYaw);
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(lerp));
        RenderLayer layer = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);
        this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(ShuttleEntranceEntity entity) {
        return TEXTURE;
    }

}
