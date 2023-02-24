package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.methods.EnabledNbtMethods;
import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CreatureMagnetItem extends Item {
    public CreatureMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CREATURE_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        EnabledNbtMethods.enabledSet(stack);
        usedTickSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GOLDEN_APPLE) || super.canRepair(stack, ingredient);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        EnabledNbtMethods.enabledSet(stack);
        usedTickSet(stack);
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
            EnabledNbtMethods.enabledSwitch(world, user, hand);
            user.getItemCooldownManager().set(this, 20);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        EnabledNbtMethods.enabledCheck(stack);
        usedTickCheck(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        boolean sneaking = user.isSneaking();
        boolean enabled = stack.getOrCreateNbt().getBoolean("Enable");
        boolean cooling = user.getItemCooldownManager().isCoolingDown(this);
        boolean entityCanAttract = !(entity.isPlayer()) && !(entity instanceof EnderDragonEntity) && !(entity instanceof WitherEntity);
        boolean creative = user.isCreative();
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        if ((((!sneaking && !rightClickReversal) || (sneaking && rightClickReversal)) || !enableSneakToSwitch) && enabled && !cooling && entityCanAttract) {
            if (!entity.addScoreboardTag(user.getEntityName())) {
                entity.removeScoreboardTag(user.getEntityName());
            }
            if (!creative) {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return ActionResult.PASS;
    }

    public static void attractCreatures(ItemStack mainhandStack, ItemStack offhandStack, LivingEntity entity, double dis, AttractMethods.Hand hand) {
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean isMainhand = hand == AttractMethods.Hand.MAINHAND;
        boolean isOffhand = hand == AttractMethods.Hand.OFFHAND;
        boolean isHand = hand == AttractMethods.Hand.HAND;
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.MAINHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.OFFHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean equipmentsHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.HEAD, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.FEET, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.LEGS, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandEmpty = !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && isMainhand && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && isOffhand && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && isHand && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean player = entity.isPlayer();
        boolean client = entity.getWorld().isClient;
        boolean spectator = entity.isSpectator();
        boolean creative = player && ((PlayerEntity) entity).isCreative();
        boolean entityCanAttract;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!client) {
            entityCanAttract = entity.getWorld().getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()).isEmpty();
            if (!magnetOff && entityCanAttract && !isEmpty) {
                entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(dis), e -> (e.getScoreboardTags().contains(entity.getEntityName()) && e instanceof LivingEntity && e.distanceTo(entity) <= dis)).forEach(e -> {
                    double move_x = (entity.getX() - e.getX()) * 0.05;
                    double move_y = (entity.getY() - e.getY()) * 0.05;
                    double move_z = (entity.getZ() - e.getZ()) * 0.05;
                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false));
                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10 * 20, 0, false, false));
                    e.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1, entity.getZ()));
                    boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0);
                    if (stop) {
                        e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                        e.setVelocityClient(move_x, 0.25, move_z);
                    } else {
                        e.setVelocity(new Vec3d(move_x, move_y, move_z));
                        e.setVelocityClient(move_x, move_y, move_z);
                    }
                    PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    if (!spectator && !creative) {
                        ItemStack stack;
                        if (isMainhand || isHand) {
                            stack = entity.getMainHandStack();
                            int tick = stack.getOrCreateNbt().getInt("UsedTick") + 1;
                            stack.getOrCreateNbt().putInt("UsedTick", tick);
                        } else {
                            stack = entity.getOffHandStack();
                            int tick = stack.getOrCreateNbt().getInt("UsedTick") + 1;
                            stack.getOrCreateNbt().putInt("UsedTick", tick);
                        }
                    }
                });
            }
        }
    }

    public static void usedTickCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("UsedTick")) {
            usedTickSet(stack);
        }
    }

    public static void usedTickSet(ItemStack stack) {
        stack.getOrCreateNbt().putInt("UsedTick", 0);
    }

}
