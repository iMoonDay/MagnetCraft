package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * @author iMoonDay
 */
public class DemagnetizerEntity extends BlockEntity {

    public DemagnetizerEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.DEMAGNETIZER_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {
        if (world.isReceivingRedstonePower(pos)) {
            world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(world.getReceivedRedstonePower(pos) * 2), entity -> (entity instanceof LivingEntity livingEntity && !entity.isSpectator() && !livingEntity.getEquippedStack(EquipmentSlot.CHEST).hasEnchantment(EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT))).stream().map(entity -> (LivingEntity) entity).forEach(DemagnetizerEntity::addUnattractEffect);
        }
    }

    private static void addUnattractEffect(LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity player && !player.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT)) {
            player.sendMessage(Text.translatable("text.magnetcraft.message.unattract_zone"), true);
        }
        entity.addStatusEffect(new StatusEffectInstance(EffectRegistries.UNATTRACT_EFFECT, 2, 0, false, false, false));
    }
}
