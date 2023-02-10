package com.imoonday.magnetcraft.effects;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.events.AttractEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class AttractEffect extends StatusEffect {
    public AttractEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // 药水效果是有益的还是有害的
                0x76428A); // 显示的颜色
    }

    // 这个方法在每个 tick 都会调用，以检查是否应应用药水效果
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 在我们的例子中，为了确保每一 tick 药水效果都会被应用，我们只要这个方法返回 true 就行了。
        return true;
    }

    // 这个方法在应用药水效果时会被调用，所以我们可以在这里实现自定义功能。
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        if (entity instanceof PlayerEntity && entity.isSpectator()) return;

        int dis = 20 + amplifier * 2;
        if (!entity.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis,
                        entity.getPos().getY() + dis,
                        entity.getPos().getZ() + dis,
                        entity.getPos().getX() - dis,
                        entity.getPos().getY() - dis,
                        entity.getPos().getZ() - dis),
                e -> e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(MagnetCraft.ATTRACT_EFFECT)
                        && e.distanceTo(entity) <= dis && !e.isSpectator()).isEmpty())
            return;
        AttractEvent.attractItems(null,null, entity, false, dis,null);

    }
}
