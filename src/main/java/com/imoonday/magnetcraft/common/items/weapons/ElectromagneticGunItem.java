package com.imoonday.magnetcraft.common.items.weapons;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ElectromagneticGunItem extends ElectromagneticTransmitterItem {

    public ElectromagneticGunItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_GUN_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.isDamaged() ? 0.0F : 1.0F);
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_GUN_ITEM, new Identifier("aiming"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !cannotUse(entity, stack) ? 1.0f : 0.0f);
    }

    @Override
    protected boolean isExplosive() {
        return true;
    }

}
