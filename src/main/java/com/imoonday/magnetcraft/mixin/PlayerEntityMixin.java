package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.FluidRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkLootingAt(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player != null) {
            World world = ((PlayerEntity) (Object) this).world;
            if (world == null) return;
            BlockState state = !player.isSwimming() ? world.getBlockState(player.getBlockPos().up()) : player.getBlockStateAtPos();
            if (state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                int slownessDuration = player.getStatusEffect(StatusEffects.SLOWNESS) != null ? Objects.requireNonNull(player.getStatusEffect(StatusEffects.SLOWNESS)).getDuration() + 2 : 2;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slownessDuration, 0, false, false, false));
                int attractDuration = player.getStatusEffect(EffectRegistries.ATTRACT_EFFECT) != null ? Objects.requireNonNull(player.getStatusEffect(EffectRegistries.ATTRACT_EFFECT)).getDuration() + 2 : 2;
                player.addStatusEffect(new StatusEffectInstance(EffectRegistries.ATTRACT_EFFECT, attractDuration, 0, false, false, false));
            }
        }
    }
}
