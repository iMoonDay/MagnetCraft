package com.imoonday.magnetcraft.common.entities.entrance;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.6.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ShuttleEntranceEntityModel extends EntityModel<ShuttleEntranceEntity> {

    private final ModelPart bb_main;

    public ShuttleEntranceEntityModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(10, 6).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 10).cuboid(0.0F, -2.0F, 1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 9).cuboid(0.0F, -2.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(7, 15).cuboid(0.0F, -3.0F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(0.0F, -3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 11).cuboid(0.0F, -4.0F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 13).cuboid(0.0F, -6.0F, 5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 0).cuboid(0.0F, -14.0F, 6.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 5).cuboid(0.0F, -4.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 2).cuboid(0.0F, -6.0F, -6.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(0.0F, -14.0F, -7.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(0.0F, -16.0F, -6.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 13).cuboid(0.0F, -17.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 10).cuboid(0.0F, -16.0F, 5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 9).cuboid(0.0F, -17.0F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 13).cuboid(0.0F, -18.0F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 0).cuboid(0.0F, -18.0F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 3).cuboid(0.0F, -19.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 0).cuboid(0.0F, -19.0F, 1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(6, 7).cuboid(0.0F, -20.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ShuttleEntranceEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

}