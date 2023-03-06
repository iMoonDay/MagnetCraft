package com.imoonday.magnetcraft.mixin;

//import com.imoonday.magnetcraft.config.ModConfig;

import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.methods.TeleportMethods;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected double serverHeadYaw;

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkAttract(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).world;
            if (world == null) return;
            if (entity.isPlayer() && entity.isSpectator()) return;
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
            boolean mainhandEmptyDamage = DamageMethods.isEmptyDamage(entity, Hand.MAIN_HAND);
            boolean offhandEmptyDamage = DamageMethods.isEmptyDamage(entity, Hand.OFF_HAND);
            boolean display = config.displayActionBar;
            boolean player = entity.isPlayer();
            boolean client = entity.world.isClient;
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
                DamageMethods.addDamage(entity, Hand.MAIN_HAND, 1,true);
            }
            if (offhandCreature && offhandUsedTick >= 200) {
                NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
                nbt.putInt("usedTick", 0);
                entity.getOffHandStack().setNbt(nbt);
                DamageMethods.addDamage(entity, Hand.OFF_HAND, 1,true);
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
                if (!client) {
                    ((ServerPlayerEntity) entity).sendMessage(text, true);
                }
            }
            if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && EnchantmentMethods.hasEnchantment(entity.getEquippedStack(EquipmentSlot.CHEST), EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
                entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "drop")
    void drop(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            Entity sourceEntity = source.getSource();
            if (sourceEntity != null) {
                LivingEntity entity = (LivingEntity) (Object) this;
                World world = player.world;
                ItemStack stack;
                stack = sourceEntity instanceof TridentEntity ? ((TridentEntity) sourceEntity).asItemStack() : player.getMainHandStack();
                boolean hasEnchantment = EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_LOOTING_ENCHANTMENT);
                if (hasEnchantment) {
                    world.getOtherEntities(null, entity.getBoundingBox(), e -> ((e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.age == 0)).forEach(e -> {
                        if (e instanceof ExperienceOrbEntity) {
                            int amount = ((ExperienceOrbEntity) e).getExperienceAmount();
                            player.addExperience(amount);
                        } else {
                            TeleportMethods.giveItemStackToPlayer(world, player, ((ItemEntity) e).getStack());
                        }
                        e.kill();
                    });
                }
            }
        }
    }
}