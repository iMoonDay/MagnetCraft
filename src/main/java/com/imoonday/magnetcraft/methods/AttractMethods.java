package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.FluidRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;

public class AttractMethods {

    public static void attractItems(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
        int degaussingDis = ModConfig.getValue().degaussingDis;
        if (!world.isClient) {
            boolean blockCanAttract = world.getOtherEntities(null, Box.from(pos).expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && pos.isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator())).isEmpty();
            if (blockCanAttract) {
                attracting(world, pos, dis, filter, allowedItems);
            }
        }
    }

    public static void attracting(Entity entity, double dis) {
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
                    if (targetEntity.horizontalCollision) {
                        vec = vec.multiply(1, 0, 1).add(0, 0.25, 0);
                    }
                    targetEntity.setVelocity(vec);
                    PlayerLookup.tracking(targetEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                }
            }
        });
    }

    public static void attracting(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
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
                    if (targetEntity.horizontalCollision) {
                        vec = vec.multiply(1, 0, 1).add(0, 0.25, 0);
                    }
                    targetEntity.setVelocity(vec);
                    PlayerLookup.tracking(targetEntity).forEach(player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                }
            }
        });
    }

    public static boolean isSameStack(ItemStack stack, ItemEntity entity) {
        if (stack != null && stack.getNbt() != null && stack.getNbt().getBoolean("Filterable")) {
            boolean stackDamagePass = true;
            boolean stackNbtPass = true;
            NbtList list = stack.getNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
            String item = Registries.ITEM.getId(entity.getStack().getItem()).toString();
            boolean inList = list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString("id").equals(item));
            if (stack.getNbt().getBoolean("CompareDamage") && inList) {
                stackDamagePass = list.stream()
                        .filter(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString("id").equals(item))
                        .anyMatch(nbtElement -> ((NbtCompound) nbtElement).getInt("Damage") == entity.getStack().getDamage());
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
                        .peek(nbtElement -> ((NbtCompound) nbtElement).getCompound("tag").remove("Damage"))
                        .anyMatch(nbtElement -> ((NbtCompound) nbtElement).getCompound("tag").equals(finalNbt));
            }
            boolean isWhitelist = stack.getNbt().getBoolean("Whitelist");
            return (!isWhitelist || inList && stackDamagePass && stackNbtPass) && (isWhitelist || !inList || !stackDamagePass || !stackNbtPass);
        }
        return true;
    }

    public static void tickCheck(LivingEntity entity) {
        ModConfig.DefaultValue value = ModConfig.getConfig().value;
        double[] minDis = new double[]{value.electromagnetAttractMinDis, value.permanentMagnetAttractMinDis, value.polarMagnetAttractMinDis};//电磁铁,永磁铁,无极磁铁最小范围
        double creatureDis = value.creatureMagnetAttractDis;
        double horseArmorAttractDis = value.horseArmorAttractDis;
        double magnetHandSpacing = value.magnetHandSpacing;
        double attractDefaultDis = value.attractDefaultDis;
        double disPerAmplifier = value.disPerAmplifier;
        double enchDefaultDis = value.enchDefaultDis;
        double disPerLvl = value.disPerLvl;
        double magnetSetMultiplier = value.magnetSetMultiplier >= 1 ? value.magnetSetMultiplier : 1.5;
        double netheriteMagnetSetMultiplier = value.netheriteMagnetSetMultiplier >= 1 ? value.netheriteMagnetSetMultiplier : 2;
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        ItemStack mainhandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offhandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        boolean mainhandEnabled = mainhandStack.getNbt() != null && mainhandStack.getNbt().getBoolean("Enable");
        boolean offhandEnabled = offhandStack.getNbt() != null && offhandStack.getNbt().getBoolean("Enable");
        boolean mainhandElectromagnet = mainhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPermanent = mainhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPolar = mainhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandCreature = mainhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;
        boolean offhandElectromagnet = offhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && offhandEnabled;
        boolean offhandPermanent = offhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && offhandEnabled;
        boolean offhandPolar = offhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && offhandEnabled;
        boolean offhandCreature = offhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && offhandEnabled;
        boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;
        boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
        boolean handPermanent = mainhandPermanent || offhandPermanent;
        boolean handPolar = mainhandPolar || offhandPolar;
        boolean handCreature = mainhandCreature || offhandCreature;
        boolean handMagnet = mainhandMagnet || offhandMagnet;
        boolean hasEnch = EnchantmentMethods.hasEnchantment(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(mainhandStack, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(offhandStack, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandMagnetHasEnch = mainhandMagnet && mainhandHasEnch;
        boolean offhandMagnetHasEnch = offhandMagnet && offhandHasEnch;
        boolean handMagnetHasEnch = mainhandMagnetHasEnch || offhandMagnetHasEnch;
        boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
        boolean horseArmorAttracting = entity instanceof HorseEntity horseEntity && horseEntity.getArmorType().isOf(ItemRegistries.MAGNETIC_IRON_HORSE_ARMOR);
        boolean isAttracting = (hasEnch || handMagnet || hasEffect || horseArmorAttracting) && entity.canAttract() && entity.isAlive();
        boolean display = ModConfig.getConfig().displayActionBar;
        boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
        boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
        boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
        int enchLvl = EnchantmentMethods.getEnchantmentLvl(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        int mainhandUsedTick = entity.getMainHandStack().getNbt() != null ? entity.getMainHandStack().getNbt().getInt("UsedTick") : 0;
        int offhandUsedTick = entity.getOffHandStack().getNbt() != null ? entity.getOffHandStack().getNbt().getInt("UsedTick") : 0;
        int tickPerDamage = value.secPerDamage * 20;
        double enchMinDis = enchDefaultDis + disPerLvl;
        double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
        double telDis;
        double finalTelDis = 0;
        while (mainhandCreature && mainhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", mainhandUsedTick - tickPerDamage);
            entity.getMainHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, Hand.MAIN_HAND, 1, true);
            mainhandUsedTick -= tickPerDamage;
        }
        while (mainhandCreature && offhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", offhandUsedTick - tickPerDamage);
            entity.getMainHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, Hand.OFF_HAND, 1, true);
            offhandUsedTick -= tickPerDamage;
        }
        if (entity instanceof PlayerEntity player && player.isSubmergedIn(FluidTags.MAGNETIC_FLUID)) {
            boolean success = false;
            int mainhandRepair = 0;
            int offhandRepair = 0;
            BlockPos pos = new BlockPos(player.getEyePos());
            BlockState state = player.world.getBlockState(pos);
            if (!player.world.isClient && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                Random random = entity.getRandom();
                if (mainhandStack.isIn(ItemTags.MAGNETS) && mainhandStack.isDamageable() && mainhandStack.isDamaged()) {
                    int damage = mainhandStack.getDamage();
                    int maxDamage = mainhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        DamageMethods.addDamage(entity, Hand.MAIN_HAND, -maxDamage / 10, false);
                        mainhandRepair = damage - mainhandStack.getDamage();
                        success = true;
                    }
                }
                if (offhandStack.isIn(ItemTags.MAGNETS) && offhandStack.isDamageable() && offhandStack.isDamaged()) {
                    int damage = offhandStack.getDamage();
                    int maxDamage = offhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        DamageMethods.addDamage(entity, Hand.OFF_HAND, -maxDamage / 10, false);
                        offhandRepair = damage - offhandStack.getDamage();
                        success = true;
                    }
                }
                if (success && random.nextBetween(1, 100) <= mainhandRepair + offhandRepair && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                    player.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                }
            }
            player.getInventory().markDirty();
        }
        //检测吸引
        if (isAttracting) {
            int amplifier;
            double dis;
            if (handMagnet || hasEnch) {
                for (int i = 0; i < handItems.length; i++) {
                    if (handItems[i]) {
                        dis = minDis[i];
                        if (mainhandItems[i]) {
                            dis += magnetHandSpacing;
                            if (offhandItems[i]) {
                                dis += magnetHandSpacing;
                            }
                        }
                        if (handMagnetHasEnch) {
                            dis += enchMinDis + (enchLvl - 1) * disPerLvl;
                        }
                        if (dis > finalDis) {
                            finalDis = dis;
                        }
                    }
                }
            }
            if (hasEffect) {
                amplifier = Objects.requireNonNull(entity.getStatusEffect(EffectRegistries.ATTRACT_EFFECT)).getAmplifier();
                dis = attractDefaultDis + amplifier * disPerAmplifier;
                if (dis > finalDis) {
                    finalDis = dis;
                }
            }
            if (horseArmorAttracting) {
                dis = horseArmorAttractDis;
                if (dis > finalDis) {
                    finalDis = dis;
                }
            }
            if (MagneticIronArmorItem.isInMagneticIronSuit(entity)) finalDis *= magnetSetMultiplier;
            if (NetheriteMagneticIronArmorItem.isInNetheriteMagneticIronSuit(entity)) finalDis *= netheriteMagnetSetMultiplier;
            entity.setAttracting(true, finalDis);
        } else {
            entity.setAttracting(false);
        }
        //信息栏
        if ((isAttracting || handCreature) && display && entity instanceof PlayerEntity player) {
            String message;
            Text text;
            if (isAttracting) {
                message = ": " + finalDis;
                text = Text.translatable("text.magnetcraft.message.attract").append(message);
                if (handElectromagnet || handPermanent) {
                    if (mainhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis + value.magnetHandSpacing;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (offhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (mainhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis + value.magnetHandSpacing;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (offhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    message = ": " + finalTelDis;
                    text = text.copy().append(" ").append(Text.translatable("text.magnetcraft.message.teleport").append(message));
                }
                if (handCreature) {
                    message = ": " + creatureDis;
                    text = text.copy().append(" ").append(Text.translatable("text.magnetcraft.message.creature_attract").append(message));
                }
            } else {
                message = ": " + creatureDis;
                text = Text.translatable("text.magnetcraft.message.creature_attract").append(message);
            }
            if (!player.world.isClient && (!EnchantmentMethods.hasEnchantment(feet, EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT) || !player.getMagneticLevitationMode() && player.getLevitationTick() <= 0)) {
                player.sendMessage(text, true);
            }
        }
        if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
            entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
        }
    }

}

