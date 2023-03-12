package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;

public class AttractMethods {

    public static void attractItems(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
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
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
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
                    if (entity instanceof PlayerEntity player && player.getInventory().containsAny(stack -> (stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) && stack.getNbt() != null && stack.getNbt().contains("Filterable") && !isSameStack(stack, itemEntity)))) {
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
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, EntityAttractNbt::isAttracting) != entity;
                } else {
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, EntityAttractNbt::isAttracting) != null;
                    hasNearerEntity = !targetEntity.world.getOtherEntities(targetEntity, entity.getBoundingBox().expand(dis), otherEntity -> (!(otherEntity instanceof PlayerEntity) && otherEntity.distanceTo(targetEntity) < entity.distanceTo(targetEntity) && (otherEntity.isAttracting()))).isEmpty();
                }
                if (!hasNearerPlayer && !hasNearerEntity) {
                    Vec3d vec = entity.getPos().subtract(targetEntity.getPos()).multiply(0.05);
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
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
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
                boolean hasNearerPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), dis, EntityAttractNbt::isAttracting) != null;
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

    public static boolean canAttract(Entity entity) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!entity.world.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(degaussingDis), otherEntity -> otherEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && entity.getPos().isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator()).isEmpty()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT)) {
                return false;
            }
            if (!(livingEntity instanceof PlayerEntity)) {
                return livingEntity.world.getEntitiesByClass(PlayerEntity.class, entity.getBoundingBox().expand(degaussingDis), player -> player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean("Enable"))).isEmpty();
            }
            return true;
        }
        return true;
    }

    public static void tickCheck(LivingEntity entity) {
        ModConfig config = ModConfig.getConfig();
        double[] minDis = new double[]{config.value.electromagnetAttractMinDis, config.value.permanentMagnetAttractMinDis, config.value.polarMagnetAttractMinDis};//电磁铁,永磁铁,无极磁铁最小范围
        double creatureDis = config.value.creatureMagnetAttractDis;
        double horseArmorAttractDis = config.value.horseArmorAttractDis;
        double magnetHandSpacing = config.value.magnetHandSpacing;
        double attractDefaultDis = config.value.attractDefaultDis;
        double disPerAmplifier = config.value.disPerAmplifier;
        double enchDefaultDis = config.value.enchDefaultDis;
        double disPerLvl = config.value.disPerLvl;
        double magnetSetMultiplier = config.value.magnetSetMultiplier >= 1 ? config.value.magnetSetMultiplier : 1.5;
        double netheriteMagnetSetMultiplier = config.value.netheriteMagnetSetMultiplier >= 1 ? config.value.netheriteMagnetSetMultiplier : 2;
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
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
        boolean enable = entity.getEnable();
        boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
        boolean horseArmorAttracting = entity instanceof HorseEntity horseEntity && horseEntity.getArmorType().isOf(ItemRegistries.MAGNETIC_IRON_HORSE_ARMOR);
        boolean isAttracting = (hasEnch || handMagnet || hasEffect || horseArmorAttracting) && enable;
        boolean hasMagneticIronHelmet = head.isOf(ItemRegistries.MAGNETIC_IRON_HELMET);
        boolean hasMagneticIronChestcplate = chest.isOf(ItemRegistries.MAGNETIC_IRON_CHESTPLATE);
        boolean hasMagneticIronLeggings = legs.isOf(ItemRegistries.MAGNETIC_IRON_LEGGINGS);
        boolean hasMagneticIronBoots = feet.isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);
        boolean hasNetheriteMagneticIronHelmet = head.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_HELMET);
        boolean hasNetheriteMagneticIronChestcplate = chest.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_CHESTPLATE);
        boolean hasNetheriteMagneticIronLeggings = legs.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_LEGGINGS);
        boolean hasNetheriteMagneticIronBoots = feet.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);
        boolean hasMagneticIronSuit = hasMagneticIronHelmet && hasMagneticIronChestcplate && hasMagneticIronLeggings && hasMagneticIronBoots;
        boolean hasNetheriteMagneticIronSuit = hasNetheriteMagneticIronHelmet && hasNetheriteMagneticIronChestcplate && hasNetheriteMagneticIronLeggings && hasNetheriteMagneticIronBoots;
        boolean mainhandEmptyDamage = DamageMethods.isEmptyDamage(entity, net.minecraft.util.Hand.MAIN_HAND);
        boolean offhandEmptyDamage = DamageMethods.isEmptyDamage(entity, net.minecraft.util.Hand.OFF_HAND);
        boolean display = config.displayActionBar;
        boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
        boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
        boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
        int enchLvl = EnchantmentMethods.getEnchantmentLvl(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        int mainhandUsedTick = entity.getMainHandStack().getNbt() != null ? entity.getMainHandStack().getNbt().getInt("UsedTick") : 0;
        int offhandUsedTick = entity.getOffHandStack().getNbt() != null ? entity.getOffHandStack().getNbt().getInt("UsedTick") : 0;
        double enchMinDis = enchDefaultDis + disPerLvl;
        double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
        double telDis;
        double finalTelDis = 0;
        while (mainhandCreature && mainhandUsedTick >= 200) {
            NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", mainhandUsedTick - 200);
            entity.getMainHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, Hand.MAIN_HAND, 1, true);
            mainhandUsedTick -= 200;
        }
        while (mainhandCreature && offhandUsedTick >= 200) {
            NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
            nbt.putInt("UsedTick", offhandUsedTick - 200);
            entity.getMainHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, Hand.OFF_HAND, 1, true);
            offhandUsedTick -= 200;
        }
        if (enable && ((mainhandCreature && !mainhandEmptyDamage) || (offhandCreature && !offhandEmptyDamage))) {
            CreatureMagnetItem.attractCreatures(entity);
        }
        //检测吸引
        if (isAttracting && canAttract(entity)) {
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
            if (hasMagneticIronSuit) finalDis *= magnetSetMultiplier;
            if (hasNetheriteMagneticIronSuit) finalDis *= netheriteMagnetSetMultiplier;
            entity.setAttracting(true, finalDis);
        } else {
            entity.setAttracting(false);
        }
        //信息栏
        if (((isAttracting && canAttract(entity)) || handCreature) && display && entity instanceof PlayerEntity player) {
            String message;
            Text text;
            if (isAttracting) {
                message = ": " + finalDis;
                text = Text.translatable("text.magnetcraft.message.attract").append(message);
                if (handElectromagnet || handPermanent) {
                    if (mainhandElectromagnet) {
                        telDis = config.value.electromagnetTeleportMinDis + config.value.magnetHandSpacing;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (offhandElectromagnet) {
                        telDis = config.value.electromagnetTeleportMinDis;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (mainhandPermanent) {
                        telDis = config.value.permanentMagnetTeleportMinDis + config.value.magnetHandSpacing;
                        if (telDis > finalTelDis) {
                            finalTelDis = telDis;
                        }
                    }
                    if (offhandPermanent) {
                        telDis = config.value.permanentMagnetTeleportMinDis;
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
            player.sendMessage(text, true);
        }
        if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
            entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
        }
    }

}

