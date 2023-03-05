package com.imoonday.magnetcraft.common.effects;

import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DegaussingEffect extends StatusEffect {
    public DegaussingEffect() {
        super(
                StatusEffectCategory.HARMFUL, // 药水效果是有益的还是有害的
                0xFFFFFF); // 显示的颜色
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.removeStatusEffect(EffectRegistries.ATTRACT_EFFECT);
    }
}
