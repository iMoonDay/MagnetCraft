package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class CreatureMagnetItem extends Item {
    public CreatureMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CREATURE_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("enabled")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("enabled") ? 1.0F : 0.0F;
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtClassMethod.enabledSet(stack);
        NbtClassMethod.usedTickSet(stack);
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtClassMethod.enabledSet(stack);
        NbtClassMethod.usedTickSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.creature_magnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.creature_magnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean sneaking = user.isSneaking();
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        boolean flying = user.getAbilities().flying;
        if (sneaking && flying) {
            sneaking = false;
        }
        if ((sneaking && !rightClickReversal) || (!sneaking && rightClickReversal)) {
            if (!enableSneakToSwitch) {
                return super.use(world, user, hand);
            }
            NbtClassMethod.enabledSwitch(world, user, hand);
            user.getItemCooldownManager().set(this, 20);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        NbtClassMethod.enabledCheck(stack);
        NbtClassMethod.usedTickCheck(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        boolean sneaking = user.isSneaking();
        boolean enabled = stack.getOrCreateNbt().getBoolean("enabled");
        boolean cooling = user.getItemCooldownManager().isCoolingDown(this);
        boolean entityCanAttract = !(entity.isPlayer()) && !(entity instanceof EnderDragonEntity) && !(entity instanceof WitherEntity);
        boolean creative = user.isCreative();
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        if ((((!sneaking && !rightClickReversal) || (sneaking && rightClickReversal)) || !enableSneakToSwitch) && enabled && !cooling && entityCanAttract) {
            if (!entity.addScoreboardTag(user.getEntityName())) {
//                entity.removeScoreboardTag(user.getEntityName());
                NbtCompound tag = new NbtCompound();
                entity.readNbt(tag);// 获取实体的NBT
                NbtCompound attracterTag = new NbtCompound(); // 创建一个新的CompoundTag
                attracterTag.putUuid("playerUUID", user.getUuid()); // 将玩家的UUID放入CompoundTag
                tag.put("Attracter", attracterTag); // 将CompoundTag放入实体的NBT
                entity.writeNbt(tag); // 设置实体的NBT
            }
            if (!creative) {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return ActionResult.PASS;
    }

}
