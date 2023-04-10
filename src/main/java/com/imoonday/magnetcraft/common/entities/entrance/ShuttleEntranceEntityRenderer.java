package com.imoonday.magnetcraft.common.entities.entrance;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ShuttleEntranceEntityRenderer extends EntityRenderer<ShuttleEntranceEntity> {


    public static final Identifier TEXTURE = id("textures/entity/shuttle_entrance.png");

    public ShuttleEntranceEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ShuttleEntranceEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(ShuttleEntranceEntity entity) {
        return TEXTURE;
    }

}
