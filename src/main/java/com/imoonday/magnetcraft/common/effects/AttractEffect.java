package com.imoonday.magnetcraft.common.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class AttractEffect extends StatusEffect {
    public AttractEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // 药水效果是有益的还是有害的
                0x76428A); // 显示的颜色
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }
}
