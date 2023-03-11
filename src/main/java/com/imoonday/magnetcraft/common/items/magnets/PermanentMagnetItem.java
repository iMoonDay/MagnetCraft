package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.FilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.CooldownMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class PermanentMagnetItem extends FilterableItem {

    public PermanentMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.PERMANENT_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.PERMANENT_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.permanent_magnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.permanent_magnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean sneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean reversal = ModConfig.getConfig().rightClickReversal;
        double dis = ModConfig.getConfig().value.permanentMagnetTeleportMinDis;
        boolean sneaking = user.isSneaking();
        if (sneaking && user.getAbilities().flying) {
            sneaking = false;
        }
        if ((sneaking && !reversal) || (!sneaking && reversal)) {
            if (user.getStackInHand(hand).getOrCreateNbt().getBoolean("Filterable")) {
                if (!user.world.isClient) {
                    openScreen(user, hand, this);
                }
            } else {
                if (!sneakToSwitch) {
                    return super.use(world, user, hand);
                }
                enabledSwitch(world, user, hand);
            }
        } else {
            ElectromagnetItem.teleportItems(world, user, dis, hand);
        }
        CooldownMethods.setCooldown(user, user.getStackInHand(hand), 10);
        return super.use(world, user, hand);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 16;
    }

    @Override
    public boolean canTeleportItems() {
        return true;
    }

}