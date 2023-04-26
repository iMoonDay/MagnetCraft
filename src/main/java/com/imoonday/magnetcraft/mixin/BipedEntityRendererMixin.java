package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.BackpackFeatureRenderer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityRenderer.class)
public class BipedEntityRendererMixin<T extends MobEntity, M extends BipedEntityModel<T>> {

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Lnet/minecraft/client/render/entity/model/BipedEntityModel;FFFF)V", at = @At("TAIL"))
    public void BipedEntityRenderer(EntityRendererFactory.Context ctx, BipedEntityModel<T> model, float shadowRadius, float scaleX, float scaleY, float scaleZ, CallbackInfo ci) {
        BipedEntityRenderer<T, M> renderer = (BipedEntityRenderer<T, M>) (Object) this;
        renderer.addFeature(new BackpackFeatureRenderer<>(renderer, ctx.getModelLoader()));
    }

}
