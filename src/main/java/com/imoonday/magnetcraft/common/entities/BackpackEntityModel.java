package com.imoonday.magnetcraft.common.entities;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class BackpackEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    private final ModelPart body;
    private final ModelPart top;

    public BackpackEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.top = root.getChild("top");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -9.0F, -18.0F, 12.0F, 9.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 13).cuboid(-6.0F, -8.0F, -14.0F, 12.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 22).cuboid(-5.0F, -8.0F, -13.0F, 10.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 13).cuboid(-4.0F, -7.0F, -12.0F, 8.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 20).cuboid(-5.0F, -9.0F, -14.0F, 10.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 22).cuboid(-5.0F, -10.0F, -18.0F, 10.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 16.0F));
        modelPartData.addChild("top", ModelPartBuilder.create().uv(0, 2).cuboid(-3.0F, -11.0F, -17.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(2.0F, -11.0F, -17.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 26).cuboid(-2.0F, -12.0F, -17.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 16.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.top);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float pitch = 0.0f;
        float roll = 0.0f;
        float pivotX = 0.0f;
        float pivotY = 0.0f;
        float pivotZ = 16f;
        float yaw = 0.0f;
        if (entity.isInSneakingPose()) {
            pitch += 0.5f;
            pivotY -= 6.5f;
            pivotZ += 2.25f;
        }
        this.body.pivotX = pivotX;
        this.body.pivotY = pivotY;
        this.body.pivotZ = pivotZ;
        this.body.pitch = pitch;
        this.body.roll = roll;
        this.body.yaw = yaw;
        this.top.pivotX = this.body.pivotX;
        this.top.pivotY = this.body.pivotY;
        this.top.pivotZ = this.body.pivotZ;
        this.top.pitch = this.body.pitch;
        this.top.roll = this.body.roll;
        this.top.yaw = this.body.yaw;
    }
}
