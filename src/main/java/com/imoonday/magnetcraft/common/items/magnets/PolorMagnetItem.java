package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.FilterableMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.EnabledNbtMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class PolorMagnetItem extends FilterableMagnetItem {

    public PolorMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.POLAR_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        EnabledNbtMethods.enabledSet(stack);
        return stack;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.POLAR_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        EnabledNbtMethods.enabledSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.polar_magnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        boolean sneaking = user.isSneaking();
        boolean flying = user.getAbilities().flying;
        if (sneaking && flying) {
            sneaking = false;
        }
        if ((sneaking && !rightClickReversal) || (!sneaking && rightClickReversal)) {
            if (user.getStackInHand(hand).getOrCreateNbt().getBoolean("Filterable")) {
                if (!user.world.isClient) {
                    openScreen(user, hand, this);
                }
            } else {
                if (!enableSneakToSwitch) {
                    return super.use(world, user, hand);
                }
                EnabledNbtMethods.enabledSwitch(world, user, hand);
            }
            user.getItemCooldownManager().set(this, 30);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        EnabledNbtMethods.enabledCheck(stack);
    }

}
