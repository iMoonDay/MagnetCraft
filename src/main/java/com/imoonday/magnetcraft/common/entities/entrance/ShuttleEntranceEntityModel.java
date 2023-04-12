package com.imoonday.magnetcraft.common.entities.entrance;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ShuttleEntranceEntityModel extends EntityModel<ShuttleEntranceEntity> {

    private final ModelPart main;

    public ShuttleEntranceEntityModel(ModelPart root) {
        this.main = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        main.addChild("body", ModelPartBuilder.create().uv(6, -2).cuboid(-1.0F, -10.0F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, -5).cuboid(-1.0F, -9.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(3, -6).cuboid(-1.0F, -8.0F, -4.0F, 0.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(2, -7).cuboid(-1.0F, -7.0F, -5.0F, 0.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(1, -8).cuboid(-1.0F, -6.0F, -6.0F, 0.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(0, -8).cuboid(-1.0F, -4.0F, -7.0F, 0.0F, 8.0F, 14.0F, new Dilation(0.0F))
                .uv(1, 2).cuboid(-1.0F, 4.0F, -6.0F, 0.0F, 2.0F, 12.0F, new Dilation(0.0F))
                .uv(2, 6).cuboid(-1.0F, 6.0F, -5.0F, 0.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(3, 9).cuboid(-1.0F, 7.0F, -4.0F, 0.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(4, 12).cuboid(-1.0F, 8.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(6, 17).cuboid(-1.0F, 9.0F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, -1.0F, 0.0F, 1.5708F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ShuttleEntranceEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

}