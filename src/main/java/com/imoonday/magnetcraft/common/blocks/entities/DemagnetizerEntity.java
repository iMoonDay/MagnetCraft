package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class DemagnetizerEntity extends BlockEntity {

    public DemagnetizerEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.DEMAGNETIZER_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {
        if (world.isReceivingRedstonePower(pos)) {
            int dis = world.getReceivedRedstonePower(pos) * 2;
            world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(dis), e -> (e instanceof LivingEntity && !e.isSpectator() && !EnchantmentMethods.hasEnchantment(((LivingEntity) e).getEquippedStack(EquipmentSlot.CHEST), EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) && e.getScoreboardTags().contains("MagnetCraft.isAttracting")).forEach(e -> ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(EffectRegistries.UNATTRACT_EFFECT, 2, 0, false, false, false)));
        }
    }
}
