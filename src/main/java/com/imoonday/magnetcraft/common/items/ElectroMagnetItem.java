package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.methods.TeleportMethod;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ElectroMagnetItem extends Item {
    public ElectroMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("enabled")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("enabled") ? 1.0F : 0.0F;
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean("enabled", true);
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtClassMethod.enabledSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.electromagnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.electromagnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        boolean sneaking = user.isSneaking();
        boolean emptyDamage = NbtClassMethod.checkEmptyDamage(user, hand);
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        double dis = config.value.electromagnetTeleportMinDis;
        if (sneaking) {
            if (!enableSneakToSwitch) {
                return super.use(world, user, hand);
            }
            NbtClassMethod.enabledSwitch(world, user, hand);
        } else if (!emptyDamage) {
            NbtClassMethod.addDamage(user, hand, 1);
            TeleportMethod.teleportItems(world, user, dis, hand);
        }
        user.getItemCooldownManager().set(this, 20);
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        NbtClassMethod.enabledCheck(stack);
    }

    public static ItemStack getStack() {
        ItemStack stack = new ItemStack(ItemRegistries.ELECTROMAGNET_ITEM);
        stack.getOrCreateNbt().putBoolean("enabled", true);
        return stack;
    }

}