package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.api.SwitchableItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PortableDemagnetizerItem extends SwitchableItem {
    public PortableDemagnetizerItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        enabledSwitch(world, user, hand);
        user.getItemCooldownManager().set(this, 20);
        return super.use(world, user, hand);
    }
}
