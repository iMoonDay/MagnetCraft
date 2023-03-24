package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.MagnetCraft;
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

/**
 * @author iMoonDay
 */
public class PermanentMagnetItem extends AbstractFilterableItem {

    public static final String ENABLE = "Enable";
    public static final String FILTERABLE = "Filterable";

    public PermanentMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.PERMANENT_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getNbt() == null || !itemStack.getNbt().contains(ENABLE) ? 0.0F : itemStack.getOrCreateNbt().getBoolean(ENABLE) ? 1.0F : 0.0F);
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
        double dis = ModConfig.getValue().permanentMagnetTeleportMinDis;
        boolean sneaking = user.isSneaking();
        if (sneaking && user.getAbilities().flying) {
            sneaking = false;
        }
        ItemStack stackInHand = user.getStackInHand(hand);
        if (sneaking != reversal) {
            if (stackInHand.getOrCreateNbt().getBoolean(FILTERABLE)) {
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
        MagnetCraft.CooldownMethods.setCooldown(user, stackInHand, 10);
        return TypedActionResult.success(stackInHand);
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