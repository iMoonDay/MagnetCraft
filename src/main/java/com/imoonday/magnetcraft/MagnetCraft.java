package com.imoonday.magnetcraft;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.*;
import com.imoonday.magnetcraft.registries.special.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class MagnetCraft implements ModInitializer {

    public static final String MOD_ID = "magnetcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("MagnetCraft");

    @Override
    public void onInitialize() {
        ModConfig.register();
        ItemRegistries.register();
        FluidRegistries.register();
        BlockRegistries.register();
        EffectRegistries.register();
        PotionRegistries.register();
        EnchantmentRegistries.register();
        ItemGroupRegistries.register();
        GlobalReceiverRegistries.serverPlayNetworkingRegister();
        CustomStatRegistries.register();
        CommandRegistries.register();
        ScreenRegistries.register();
//        EventRegistries.register();
        RecipeRegistries.register();
        EntityRegistries.register();
    }

    public static class AttractMethods {

        public static void attractItems(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
            int degaussingDis = ModConfig.getValue().degaussingDis;
            if (!world.isClient) {
                boolean blockCanAttract = world.getOtherEntities(null, Box.from(pos).expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && pos.isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator())).isEmpty();
                if (blockCanAttract) {
                    tryAttract(world, pos, dis, filter, allowedItems);
                }
            }
        }

        public static void tryAttract(Entity entity, double dis) {
            if (entity.world.isClient) {
                return;
            }
            int degaussingDis = ModConfig.getValue().degaussingDis;
            boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
            boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
            ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
            ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
            entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(dis), targetEntity -> ((targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.getPos().isInRange(entity.getPos(), dis) && !targetEntity.getPos().isInRange(entity.getPos(), 0.5))).forEach(targetEntity -> {
                boolean pass = true;
                if (targetEntity instanceof ItemEntity itemEntity) {
                    String item = Registries.ITEM.getId(itemEntity.getStack().getItem()).toString();
                    boolean mainhandStackListPass = true;
                    boolean offhandStackListPass = true;
                    boolean controllerListPass = true;
                    if (entity instanceof LivingEntity livingEntity) {
                        if (livingEntity.getMainHandStack().getNbt() != null && livingEntity.getMainHandStack().getNbt().contains("Filterable")) {
                            mainhandStackListPass = isSameStack(livingEntity.getMainHandStack(), itemEntity);
                        }
                        if (livingEntity.getOffHandStack().getNbt() != null && livingEntity.getOffHandStack().getNbt().contains("Filterable")) {
                            offhandStackListPass = isSameStack(livingEntity.getOffHandStack(), itemEntity);
                        }
                        if (entity instanceof PlayerEntity player && player.getInventory().containsAny(stack -> ((stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) && stack.getNbt() != null && stack.getNbt().contains("Filterable") && !isSameStack(stack, itemEntity)) || ((Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getNbt() != null && stack.getNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).filter(nbtCompound -> nbtCompound.getString("id").equals(Registries.ITEM.getId(ItemRegistries.MAGNET_CONTROLLER_ITEM).toString())).peek(nbtCompound -> nbtCompound.remove("Slot")).map(ItemStack::fromNbt).anyMatch(stack1 -> stack1.getNbt() != null && stack1.getNbt().contains("Filterable") && !isSameStack(stack1, itemEntity)))))) {
                            controllerListPass = false;
                        }
                    }
                    boolean StackListPass = mainhandStackListPass && offhandStackListPass && controllerListPass;
                    boolean whitelistPass = whitelist.contains(item);
                    boolean blacklistPass = !blacklist.contains(item);
                    boolean hasDegaussingPlayer = !targetEntity.world.getOtherEntities(targetEntity, targetEntity.getBoundingBox().expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && targetEntity.getPos().isInRange(otherEntity.getPos(), degaussingDis))).isEmpty();
                    pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && StackListPass && !hasDegaussingPlayer;
                }
                if (pass) {
                    boolean hasNearerPlayer;
                    boolean hasNearerEntity = false;
                    if (entity instanceof PlayerEntity) {
                        hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, MagnetCraftEntity::isAttracting) != entity;
                    } else {
                        hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, MagnetCraftEntity::isAttracting) != null;
                        hasNearerEntity = !targetEntity.world.getOtherEntities(targetEntity, entity.getBoundingBox().expand(dis), otherEntity -> (!(otherEntity instanceof PlayerEntity) && otherEntity.distanceTo(targetEntity) < entity.distanceTo(targetEntity) && otherEntity.isAttracting() && otherEntity.getEnable() && otherEntity.isAlive())).isEmpty();
                    }
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        Vec3d vec = entity.getPos().add(0, 0.5, 0).subtract(targetEntity.getPos()).multiply(0.05);
                        targetEntity.setVelocity(targetEntity.horizontalCollision ? vec.multiply(1, 0, 1).add(0, 0.25, 0) : vec);
                        PlayerLookup.tracking(targetEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                    }
                }
            });
        }

        private static void tryAttract(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
            int degaussingDis = ModConfig.getValue().degaussingDis;
            boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
            boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
            ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
            ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
            world.getOtherEntities(null, Box.from(pos).expand(dis), targetEntity -> (targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity && targetEntity.getPos().isInRange(pos, dis) && !targetEntity.getPos().isInRange(pos, 0.5))).forEach(targetEntity -> {
                float f = (float) (pos.getX() - targetEntity.getX());
                float g = (float) (pos.getY() - targetEntity.getY());
                float h = (float) (pos.getZ() - targetEntity.getZ());
                float blockDistanceTo = MathHelper.sqrt(f * f + g * g + h * h);
                boolean pass = true;
                if (targetEntity instanceof ItemEntity itemEntity) {
                    String item = Registries.ITEM.getId(itemEntity.getStack().getItem()).toString();
                    boolean noDegaussingEntity = targetEntity.world.getOtherEntities(targetEntity, targetEntity.getBoundingBox().expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity && ((LivingEntity) otherEntity).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && targetEntity.getPos().isInRange(otherEntity.getPos(), degaussingDis))).isEmpty();
                    pass = (!whitelistEnable || whitelist.contains(item)) && (!blacklistEnable || !blacklist.contains(item)) && noDegaussingEntity && (!filter || allowedItems.contains(itemEntity.getStack().getItem()));
                }
                if (pass && !world.isClient) {
                    boolean hasNearerEntity = !world.getOtherEntities(targetEntity, Box.from(pos).expand(dis), otherEntity -> (!(otherEntity instanceof PlayerEntity) && otherEntity.getPos().isInRange(targetEntity.getPos(), blockDistanceTo) && (otherEntity.isAttracting()))).isEmpty();
                    boolean hasNearerPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), dis, MagnetCraftEntity::isAttracting) != null;
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        Vec3d vec = pos.subtract(targetEntity.getPos()).multiply(0.05);
                        targetEntity.setVelocity(targetEntity.horizontalCollision ? vec.multiply(1, 0, 1).add(0, 0.25, 0) : vec);
                        PlayerLookup.tracking(targetEntity).forEach(player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                    }
                }
            });
        }

        private static boolean isSameStack(ItemStack stack, ItemEntity entity) {
            if (stack == null || stack.getNbt() == null || !stack.getNbt().getBoolean("Filterable")) {
                return true;
            }
            boolean stackDamagePass = true;
            boolean stackNbtPass = true;
            NbtList list = stack.getNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
            String item = Registries.ITEM.getId(entity.getStack().getItem()).toString();
            boolean inList = list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString("id").equals(item));
            if (stack.getNbt().getBoolean("CompareDamage") && inList) {
                stackDamagePass = list.stream()
                        .filter(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString("id").equals(item))
                        .map(nbtElement -> (NbtCompound) nbtElement)
                        .anyMatch(NbtCompound -> NbtCompound.getInt("Damage") == entity.getStack().getDamage());
            }
            if (stack.getNbt().getBoolean("CompareNbt") && inList) {
                NbtCompound nbt = entity.getStack().getNbt();
                NbtCompound nbtWithoutDamage = new NbtCompound();
                if (nbt != null) {
                    nbtWithoutDamage = nbt.copy();
                    nbtWithoutDamage.remove("Damage");
                }
                NbtCompound finalNbt = nbtWithoutDamage;
                stackNbtPass = list.stream()
                        .filter(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString("id").equals(item))
                        .map(nbtElement -> (NbtCompound) nbtElement)
                        .peek(NbtCompound -> NbtCompound.getCompound("tag").remove("Damage"))
                        .anyMatch(NbtCompound -> NbtCompound.getCompound("tag").equals(finalNbt));
            }
            boolean isWhitelist = stack.getNbt().getBoolean("Whitelist");
            return (!isWhitelist || inList && stackDamagePass && stackNbtPass) && (isWhitelist || !inList || !stackDamagePass || !stackNbtPass);
        }

    }

    public static class CooldownMethods {

        public static void setCooldown(PlayerEntity player, ItemStack stack, int cooldown) {
            int percent = ModConfig.getValue().coolingPercentage;
            if (EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT)) {
                int level = EnchantmentMethods.getEnchantmentLvl(stack, EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT);
                cooldown -= cooldown * level / 10;
            }
            player.getItemCooldownManager().set(stack.getItem(), cooldown * percent / 100);
        }

    }

    public static class DamageMethods {

        public static void addDamage(LivingEntity user, Hand hand, int damage, boolean unbreaking) {
            ItemStack stack = user.getStackInHand(hand);
            if ((user instanceof PlayerEntity player && player.getAbilities().creativeMode && damage > 0) || !stack.isDamageable()) {
                return;
            }
            int stackDamage = stack.getDamage();
            int stackMaxDamage = stack.getMaxDamage();
            int finalDamage = stackDamage + damage;
            if (unbreaking) {
                stack.damage(finalDamage > stackMaxDamage ? 0 : Math.max(damage, 0), user.getRandom(), user instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
            } else {
                stack.setDamage(finalDamage > stackMaxDamage ? stackMaxDamage : Math.max(finalDamage, 0));
            }
        }

        public static void addDamage(ItemStack stack, Random random, int damage, boolean unbreaking) {
            if (!stack.isDamageable()) {
                return;
            }
            int stackDamage = stack.getDamage();
            int stackMaxDamage = stack.getMaxDamage();
            int finalDamage = stackDamage + damage;
            if (unbreaking) {
                stack.damage(finalDamage > stackMaxDamage ? 0 : Math.max(damage, 0), random, null);
            } else {
                stack.setDamage(finalDamage > stackMaxDamage ? stackMaxDamage : Math.max(finalDamage, 0));
            }
        }

        public static boolean isEmptyDamage(LivingEntity player, Hand hand) {
            if (hand == null) {
                return false;
            }
            ItemStack stack = player.getStackInHand(hand);
            return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
        }

        public static boolean isEmptyDamage(ItemStack stack) {
            return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
        }

    }

    public static class EnchantmentMethods {

        public static boolean hasEnchantment(LivingEntity entity, EquipmentSlot equipmentSlot, Enchantment enchantment) {
            return getEnchantmentLvl(entity, equipmentSlot, enchantment) > 0;
        }

        public static boolean hasEnchantment(LivingEntity entity, Enchantment enchantment) {
            return getEnchantmentLvl(entity, enchantment) > 0;
        }

        public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
            return getEnchantmentLvl(stack, enchantment) > 0;
        }

        public static int getEnchantmentLvl(LivingEntity entity, EquipmentSlot equipmentSlot, Enchantment enchantment) {
            return EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(equipmentSlot));
        }

        public static int getEnchantmentLvl(LivingEntity entity, Enchantment enchantment) {
            return Arrays.stream(EquipmentSlot.values()).mapToInt(slot -> EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(slot))).sum();
        }

        public static int getEnchantmentLvl(ItemStack stack, Enchantment enchantment) {
            return EnchantmentHelper.getLevel(enchantment, stack);
        }

    }

}