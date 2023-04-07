package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.items.armors.MagneticShulkerBackpackItem;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.FluidRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AlibabaUndefineMagicConstant")
@Mixin(ItemEntity.class)
public class ItemEntityMixin extends EntityMixin {

    protected Vec3d attractSource;

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (entity != null) {
            World world = ((ItemEntity) (Object) this).world;
            if (world == null) {
                return;
            }
            ItemStack stack = entity.getStack();
            boolean isAttracting = stack.isIn(ItemTags.ATTRACTIVE_MAGNETS) && stack.getOrCreateNbt().getBoolean("Enable");
            int dis = ModConfig.getValue().droppedMagnetAttractDis;
            if (isAttracting && this.canAttract()) {
                this.setAttracting(true, dis);
            }
            if (entity.isSubmergedIn(FluidTags.MAGNETIC_FLUID)) {
                boolean success = false;
                int mainhandRepair = 0;
                int offhandRepair = 0;
                BlockState state = entity.getBlockStateAtPos();
                if (!entity.world.isClient && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                    Random random = entity.world.random;
                    if (stack.isIn(ItemTags.MAGNETS) && stack.isDamageable() && stack.isDamaged()) {
                        int damage = stack.getDamage();
                        int maxDamage = stack.getMaxDamage();
                        if (random.nextBetween(1, maxDamage * 400) <= damage) {
                            stack.addDamage(random, -maxDamage / 10, false);
                            mainhandRepair = damage - stack.getDamage();
                            success = true;
                        }
                    }
                    if (success && random.nextBetween(1, 200) <= mainhandRepair + offhandRepair) {
                        entity.world.setBlockState(entity.getBlockPos(), Blocks.WATER.getDefaultState());
                    }
                }
            }
            if (this.getAttractSource() != null && entity.isOnGround()) {
                this.setAttractSource(null);
            }
        }
    }

    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean insertStack(PlayerInventory instance, ItemStack stack) {
        if (instance.insertStack(stack)) {
            instance.markDirty();
            return true;
        }
        PlayerEntity player = instance.player;
        ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isOf(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM)) {
            boolean insertStack = MagneticShulkerBackpackItem.insertStack(player, 38, armorStack, stack, MagneticShulkerBackpackItem.getItems(armorStack));
            instance.markDirty();
            return insertStack;
        }
        return false;
    }

    @Override
    public Vec3d getAttractSource() {
        return this.attractSource;
    }

    @Override
    public void setAttractSource(Vec3d pos) {
        this.attractSource = pos;
    }

}