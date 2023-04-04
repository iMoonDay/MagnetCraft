package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.items.armors.MagneticShulkerBackpackItem;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerInventory inventory = (PlayerInventory) (Object) this;
        PlayerEntity player = inventory.player;
        ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isOf(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM)) {
            if (MagneticShulkerBackpackItem.insertStack(player, 38, armorStack, stack, MagneticShulkerBackpackItem.getItems(armorStack))) {
                inventory.markDirty();
                cir.setReturnValue(true);
            }
        }
    }

}
