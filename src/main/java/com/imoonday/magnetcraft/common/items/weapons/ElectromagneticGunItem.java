package com.imoonday.magnetcraft.common.items.weapons;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ElectromagneticGunItem extends ElectromagneticTransmitterItem {

    public ElectromagneticGunItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_GUN_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> livingEntity instanceof PlayerEntity player && (player.getItemCooldownManager().isCoolingDown(ItemRegistries.ELECTROMAGNETIC_GUN_ITEM) || itemStack.isBroken()) ? 0.0F : 1.0F);
    }

    @Override
    protected boolean isExplosive() {
        return true;
    }

}
