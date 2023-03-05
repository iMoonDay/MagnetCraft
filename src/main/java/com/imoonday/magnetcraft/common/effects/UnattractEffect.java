package com.imoonday.magnetcraft.common.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class UnattractEffect extends StatusEffect {
    public UnattractEffect() {
        super(
                StatusEffectCategory.HARMFUL, // 药水效果是有益的还是有害的
                0xFFFFFF); // 显示的颜色
    }

}
