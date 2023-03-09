package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class AttractMethods {

    public enum Hand {
        MAINHAND,
        OFFHAND,
        HAND,
        NONE
    }

    public static void attractItems(@Nullable ItemStack mainhandStack, @Nullable ItemStack offhandStack, LivingEntity entity, boolean selected, double dis, Hand hand) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean isMainhand = hand == Hand.MAINHAND;
        boolean isOffhand = hand == Hand.OFFHAND;
        boolean isHand = hand == Hand.HAND;
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.MAINHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.OFFHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean equipmentsHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.HEAD, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.FEET, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.LEGS, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandEmpty = selected && !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && isMainhand && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = selected && !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && isOffhand && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = selected && !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && isHand && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean client = entity.world.isClient;
        boolean entityCanAttract;
        if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT)) {
            return;
        }
        if (!client) {
            entityCanAttract = entity.world.getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (!entity.isPlayer() && !entity.world.getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof PlayerEntity && ((PlayerEntity) e).getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean("Enable")))).isEmpty()) {
                return;
            }
            if (!magnetOff && entityCanAttract && !isEmpty) {
                attracting(entity, dis);
            }
        }
    }

    public static void attractItems(Entity entity, double dis) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean client = entity.world.isClient;
        boolean entityCanAttract;
        if (!client) {
            entityCanAttract = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (entityCanAttract) {
                attracting(entity, dis);
            }
        }
    }

    public static void attractItems(World world, Vec3d pos, double dis) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean client = world.isClient;
        boolean entityCanAttract;
        if (!client) {
            entityCanAttract = world.getOtherEntities(null, new Box(pos.getX() - degaussingDis, pos.getY() - degaussingDis, pos.getZ() - degaussingDis, pos.getX() + degaussingDis, pos.getY() + degaussingDis, pos.getZ() + degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (entityCanAttract) {
                attracting(world, pos, dis);
            }
        }
    }

    public static void attracting(Entity entity, double dis) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
        boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
        ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
        ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
        entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity && e.distanceTo(entity) <= dis && e.distanceTo(entity) > 0.5)).forEach(e -> {
            boolean hasNearerPlayer;
            boolean hasNearerEntity = false;
            boolean player = entity.isPlayer();
            boolean client = entity.world.isClient;
            boolean pass = true;
            if (e instanceof ItemEntity) {
                String item = Registries.ITEM.getId(((ItemEntity) e).getStack().getItem()).toString();
                boolean StackListPass;
                boolean mainhandStackListPass = true;
                boolean offhandStackListPass = true;
                boolean controllerListPass = true;
                if (entity instanceof LivingEntity) {
                    if (((LivingEntity) entity).getMainHandStack().getNbt() != null && ((LivingEntity) entity).getMainHandStack().getNbt().contains("Filterable")) {
                        mainhandStackListPass = isSameStack(((LivingEntity) entity).getMainHandStack(), (ItemEntity) e);
                    }
                    if (((LivingEntity) entity).getOffHandStack().getNbt() != null && ((LivingEntity) entity).getOffHandStack().getNbt().contains("Filterable")) {
                        offhandStackListPass = isSameStack(((LivingEntity) entity).getOffHandStack(), (ItemEntity) e);
                    }
                    if (entity instanceof PlayerEntity && ((PlayerEntity) entity).getInventory().containsAny(stack -> (stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) && stack.getNbt() != null && stack.getNbt().contains("Filterable") && !isSameStack(stack, (ItemEntity) e)))) {
                        controllerListPass = false;
                    }
                }
                StackListPass = mainhandStackListPass && offhandStackListPass && controllerListPass;
                boolean whitelistPass = whitelist.contains(item);
                boolean blacklistPass = !blacklist.contains(item);
                boolean hasDegaussingPlayer = !e.world.getOtherEntities(e, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(o) <= degaussingDis)).isEmpty();
                pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && StackListPass && !hasDegaussingPlayer;
            }
            if (pass) {
                if (!client) {
                    if (player) {
                        hasNearerPlayer = e.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != entity;
                    } else {
                        hasNearerPlayer = e.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != null;
                        hasNearerEntity = !e.world.getOtherEntities(e, entity.getBoundingBox().expand(dis), o -> (!(o.isPlayer()) && o.distanceTo(e) < entity.distanceTo(e) && o.getScoreboardTags().contains("MagnetCraft.isAttracting"))).isEmpty();
                    }
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        double move_x = (entity.getX() - e.getX()) * 0.05;
                        double move_y = (entity.getEyeY() - e.getY()) * 0.05;
                        double move_z = (entity.getZ() - e.getZ()) * 0.05;
                        boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0) && (e.getVelocity().getY() > 0.0 || e.getVelocity().getY() < -0.12) && !(e.getX() == entity.getX() && e.getZ() == entity.getY());
                        if (stop) {
                            e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                            e.setVelocityClient(move_x, 0.25, move_z);
                        } else {
                            e.setVelocity(new Vec3d(move_x, move_y, move_z));
                            e.setVelocityClient(move_x, move_y, move_z);
                        }
                        PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    }
                }
            }
        });
    }

    public static void attracting(World world, Vec3d pos, double dis) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
        boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
        ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
        ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
        world.getOtherEntities(null, new Box(pos.getX() - dis, pos.getY() - dis, pos.getZ() - dis, pos.getX() + dis, pos.getY() + dis, pos.getZ() + dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) <= dis && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) > 0.5)).forEach(e -> {
            float f = (float) (pos.getX() - e.getX());
            float g = (float) (pos.getY() - e.getY());
            float h = (float) (pos.getZ() - e.getZ());
            float blockDistanceTo = MathHelper.sqrt(f * f + g * g + h * h);
            boolean client = world.isClient;
            boolean hasNearerPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != null;
            String item;
            boolean whitelistPass;
            boolean blacklistPass;
            boolean hasDegaussingPlayer;
            boolean pass = true;
            boolean hasNearerEntity;
            if (e instanceof ItemEntity) {
                item = Registries.ITEM.getId(((ItemEntity) e).getStack().getItem()).toString();
                whitelistPass = whitelist.contains(item);
                blacklistPass = !blacklist.contains(item);
                hasDegaussingPlayer = !e.world.getOtherEntities(e, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(o) <= degaussingDis)).isEmpty();
                pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && !hasDegaussingPlayer;
            }
            if (pass) {
                if (!client) {
                    hasNearerEntity = !world.getOtherEntities(e, new Box(pos.getX() - dis, pos.getY() - dis, pos.getZ() - dis, pos.getX() + dis, pos.getY() + dis, pos.getZ() + dis), o -> (!(o.isPlayer()) && o.distanceTo(e) < blockDistanceTo && o.getScoreboardTags().contains("MagnetCraft.isAttracting"))).isEmpty();
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        double move_x = (pos.getX() - e.getX()) * 0.05;
                        double move_y = (pos.getY() - e.getY()) * 0.05;
                        double move_z = (pos.getZ() - e.getZ()) * 0.05;
                        boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0) && (e.getVelocity().getY() > 0.0 || e.getVelocity().getY() < -0.12) && !(e.getX() == pos.getX() && e.getZ() == pos.getZ());
                        if (stop) {
                            e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                            e.setVelocityClient(move_x, 0.25, move_z);
                        } else {
                            e.setVelocity(new Vec3d(move_x, move_y, move_z));
                            e.setVelocityClient(move_x, move_y, move_z);
                        }
                        PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    }
                }
            }
        });
    }

    public static boolean isSameStack(ItemStack stack, ItemEntity entity) {
        String item = Registries.ITEM.getId(entity.getStack().getItem()).toString();
        boolean stackDamagePass = true;
        boolean stackNbtPass = true;
        if (stack != null && stack.getNbt() != null && stack.getNbt().getBoolean("Filterable")) {
            NbtList list = stack.getNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
            boolean inList = list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && Objects.equals(((NbtCompound) nbtElement).getString("id"), item));
            if (stack.getNbt().getBoolean("CompareDamage") && inList) {
                for (int i = 0; i < list.size(); i++) {
                    if (Objects.equals(list.getCompound(i).getString("id"), item)) {
                        if (list.getCompound(i).getCompound("tag").getInt("Damage") != entity.getStack().getDamage()) {
                            stackDamagePass = false;
                        }
                    }
                }
            }
            if (stack.getNbt().getBoolean("CompareNbt") && inList) {
                for (int i = 0; i < list.size(); i++) {
                    if (Objects.equals(list.getCompound(i).getString("id"), item)) {
                        NbtList newList = list.copy();
                        newList.getCompound(i).getCompound("tag").remove("Damage");
                        ItemStack entityStack = entity.getStack();
                        NbtCompound nbt = entityStack.getNbt();
                        if (nbt != null) {
                            NbtCompound newNbt = nbt.copy();
                            newNbt.remove("Damage");
                            if (!Objects.equals(newList.getCompound(i).getCompound("tag"), newNbt)) {
                                stackNbtPass = false;
                            }
                        }
                    }
                }
            }
            boolean isWhitelist = stack.getNbt().getBoolean("Whitelist");
            return (!isWhitelist || inList && stackDamagePass && stackNbtPass) && (isWhitelist || !inList || !stackDamagePass || !stackNbtPass);
        }
        return true;
    }

    public static boolean isAttracting(Entity entity) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        boolean hasTag = entity.getScoreboardTags().contains("MagnetCraft.isAttracting");
        boolean noDegaussingEffect = entity.world.getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), o -> o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && o.distanceTo(entity) <= degaussingDis && !o.isSpectator()).isEmpty();
        boolean isLivingEntity = entity instanceof LivingEntity;
        boolean isPlayerEntity = entity instanceof PlayerEntity;
        if (hasTag && noDegaussingEffect) {
            if (isLivingEntity) {
                boolean hasStatusEffect = ((LivingEntity) entity).hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
                if (!hasStatusEffect) {
                    if (!isPlayerEntity) {
                        return entity.world.getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), o -> o instanceof PlayerEntity && ((PlayerEntity) o).getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean("Enable"))).isEmpty();
                    }
                    return true;
                }
                return false;
            }
            return true;
        } else if (entity instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) entity).getStack();
            return stack.isIn(ItemTags.ATTRACTIVE_MAGNETS) && stack.getOrCreateNbt().getBoolean("Enable");
        }
        return false;
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
        ItemStack mainhand = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offhand = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        boolean mainhandEnabled = mainhand.getNbt() != null && mainhand.getNbt().getBoolean("Enable");
        boolean offhandEnabled = offhand.getNbt() != null && offhand.getNbt().getBoolean("Enable");
        boolean mainhandElectromagnet = mainhand.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPermanent = mainhand.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPolar = mainhand.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandCreature = mainhand.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;
        boolean offhandElectromagnet = offhand.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && offhandEnabled;
        boolean offhandPermanent = offhand.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && offhandEnabled;
        boolean offhandPolar = offhand.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && offhandEnabled;
        boolean offhandCreature = offhand.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && offhandEnabled;
        boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;
        boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
        boolean handPermanent = mainhandPermanent || offhandPermanent;
        boolean handPolar = mainhandPolar || offhandPolar;
        boolean handCreature = mainhandCreature || offhandCreature;
        boolean handMagnet = mainhandMagnet || offhandMagnet;
        boolean hasEnch = (EnchantmentMethods.hasEnchantment(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT));
        boolean hasTag = entity.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.MAINHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.OFFHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean selected = mainhandMagnet || mainhandHasEnch || offhandMagnet || offhandHasEnch || mainhandCreature || offhandCreature;
        boolean horseArmorAttracting = entity instanceof HorseEntity && ((HorseEntity) entity).getArmorType().isOf(ItemRegistries.MAGNETIC_IRON_HORSE_ARMOR);
        boolean isAttracting = (hasEnch || handMagnet || hasEffect || horseArmorAttracting) && !hasTag;
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
        boolean player = entity.isPlayer();
        boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
        boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
        boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
        int enchLvl = EnchantmentMethods.getEnchantmentLvl(entity, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        int mainhandUsedTick = entity.getMainHandStack().getNbt() != null ? entity.getMainHandStack().getNbt().getInt("usedTick") : 0;
        int offhandUsedTick = entity.getOffHandStack().getNbt() != null ? entity.getOffHandStack().getNbt().getInt("usedTick") : 0;
        double enchMinDis = enchDefaultDis + disPerLvl;
        double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
        double telDis;
        double finalTelDis = 0;
        AttractMethods.Hand playerHand = AttractMethods.Hand.NONE;
        ItemStack mainhandStack = ItemStack.EMPTY;
        ItemStack offhandStack = ItemStack.EMPTY;
        if (mainhandMagnet || mainhandHasEnch || mainhandCreature) {
            mainhandStack = entity.getMainHandStack();
            playerHand = AttractMethods.Hand.MAINHAND;
        }
        if (offhandMagnet || offhandHasEnch || offhandCreature) {
            offhandStack = entity.getOffHandStack();
            if (mainhandMagnet || mainhandCreature) playerHand = AttractMethods.Hand.HAND;
            else playerHand = AttractMethods.Hand.OFFHAND;
        }
        if (playerHand == AttractMethods.Hand.HAND && (mainhandEmptyDamage || offhandEmptyDamage)) {
            if (mainhandEmptyDamage) playerHand = AttractMethods.Hand.OFFHAND;
            if (offhandEmptyDamage) playerHand = AttractMethods.Hand.MAINHAND;
        }
        if (mainhandCreature && mainhandUsedTick >= 200) {
            NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
            nbt.putInt("usedTick", 0);
            entity.getMainHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, net.minecraft.util.Hand.MAIN_HAND, 1, true);
        }
        if (offhandCreature && offhandUsedTick >= 200) {
            NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
            nbt.putInt("usedTick", 0);
            entity.getOffHandStack().setNbt(nbt);
            DamageMethods.addDamage(entity, net.minecraft.util.Hand.OFF_HAND, 1, true);
        }
        if (handCreature && !hasTag && ((playerHand == AttractMethods.Hand.MAINHAND && !mainhandEmptyDamage) || (playerHand == AttractMethods.Hand.OFFHAND && !offhandEmptyDamage) || (playerHand == AttractMethods.Hand.HAND && !mainhandEmptyDamage && !offhandEmptyDamage))) {
            CreatureMagnetItem.attractCreatures(mainhandStack, offhandStack, entity, creatureDis, playerHand);
        }
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
            AttractMethods.attractItems(mainhandStack, offhandStack, entity, selected, finalDis, playerHand);
            entity.addScoreboardTag("MagnetCraft.isAttracting");
        } else {
            entity.removeScoreboardTag("MagnetCraft.isAttracting");
        }
        if ((isAttracting || handCreature) && display && player) {
            String message;
            Text text;
            if (finalDis > 0) {
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
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) entity).sendMessage(text, true);
            }
        }
        if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && EnchantmentMethods.hasEnchantment(entity.getEquippedStack(EquipmentSlot.CHEST), EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
            entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
        }
    }

}

