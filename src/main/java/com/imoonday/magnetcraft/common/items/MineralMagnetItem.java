package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.methods.MineralMethod;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MineralMagnetItem extends Item {
    public MineralMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MINERAL_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemCooldownManager().isCoolingDown(ItemRegistries.MINERAL_MAGNET_ITEM)) {
                return 0.0F;
            }
            return NbtClassMethod.isEmptyDamage(itemStack) ? 0.0F : 1.0F;
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!NbtClassMethod.isEmptyDamage(user, hand)) {
            boolean success = MineralMethod.SearchMineral(user, hand) > 0;
            if (success && !user.isCreative()) {
                user.getItemCooldownManager().set(this, 60 * 20);
            } else {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return super.use(world, user, hand);
    }
}
