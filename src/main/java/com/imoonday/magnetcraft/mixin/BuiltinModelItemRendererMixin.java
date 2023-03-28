package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntityModel;
import com.imoonday.magnetcraft.registries.common.EntityRendererRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

    private MagneticWrenchEntityModel modelWrench;

    @Inject(method = "reload",at = @At("TAIL"))
    void reload(ResourceManager manager, CallbackInfo ci){
        BuiltinModelItemRenderer itemRenderer = (BuiltinModelItemRenderer) (Object) this;
        this.modelWrench = new MagneticWrenchEntityModel(itemRenderer.entityModelLoader.getModelPart(EntityRendererRegistries.MODEL_MAGNETIC_WRENCH_LAYER));
    }

    @Inject(method = "render",at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILHARD)
    void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci, Item item){
        if (stack.isOf(ItemRegistries.MAGNETIC_WRENCH_ITEM)) {
            matrices.push();
            matrices.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer vertexConsumer2 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelWrench.getLayer(MagneticWrenchEntityModel.TEXTURE), false, stack.hasGlint());
            this.modelWrench.render(matrices, vertexConsumer2, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }
    }

}
