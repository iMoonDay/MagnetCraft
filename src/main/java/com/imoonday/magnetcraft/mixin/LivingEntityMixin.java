package com.imoonday.magnetcraft.mixin;

//import com.imoonday.magnetcraft.config.ModConfig;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.methods.CreatureMethod;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
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
            World world = ((LivingEntity) (Object) this).getWorld();
            if (world == null) return;
            if (entity instanceof PlayerEntity && entity.isSpectator()) return;
            ModConfig config = ModConfig.getConfig();
            double[] minDis = new double[]{config.value.electromagnetAttractMinDis, config.value.permanentMagnetAttractMinDis, config.value.polarMagnetAttractMinDis};//电磁铁,永磁铁,无极磁铁最小范围
            double creatureDis = config.value.creatureMagnetAttractDis;
            double magnetHandSpacing = config.value.magnetHandSpacing;
            double enchDefaultDis = config.value.enchDefaultDis;
            double disPerLvl = config.value.disPerLvl;
            double magnetSetMultiplier = config.value.magnetSetMultiplier;
            double netheriteMagnetSetMultiplier = config.value.netheriteMagnetSetMultiplier;
            if (magnetSetMultiplier < 1) magnetSetMultiplier = 1.5;
            if (netheriteMagnetSetMultiplier < 1) netheriteMagnetSetMultiplier = 2;
            ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
            ItemStack mainhand = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            ItemStack offhand = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            boolean mainhandEnabled = mainhand.getOrCreateNbt().getBoolean("enabled");
            boolean offhandEnabled = offhand.getOrCreateNbt().getBoolean("enabled");
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
            boolean hasEnch = (NbtClassMethod.hasEnchantment(entity, null, "magnetcraft:attract"));
            boolean hasTag = entity.getScoreboardTags().contains("MagnetOFF");
            boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
            boolean mainhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
            boolean offhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");
            boolean selected = mainhandMagnet || mainhandHasEnch || offhandMagnet || offhandHasEnch || mainhandCreature || offhandCreature;
            boolean isAttracting = (hasEnch || handMagnet || hasEffect) && !hasTag;
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
            boolean mainhandEmptyDamage = NbtClassMethod.checkEmptyDamage(entity, Hand.MAIN_HAND);
            boolean offhandEmptyDamage = NbtClassMethod.checkEmptyDamage(entity, Hand.OFF_HAND);
            boolean display = config.displayActionBar;
            boolean player = entity instanceof PlayerEntity;
            boolean client = entity.getWorld().isClient;
            boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
            boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
            boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
            int enchLvl = NbtClassMethod.getEnchantmentLvl(entity, null, "magnetcraft:attract");
            int mainhandUsedTick = entity.getMainHandStack().getOrCreateNbt().getInt("usedTick");
            int offhandUsedTick = entity.getOffHandStack().getOrCreateNbt().getInt("usedTick");
            double enchMinDis = enchDefaultDis + disPerLvl;
            double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
            double telDis;
            double finalTelDis = 0;
            String playerHand = null;
            ItemStack mainhandStack = ItemStack.EMPTY;
            ItemStack offhandStack = ItemStack.EMPTY;
            if (mainhandMagnet || mainhandHasEnch || mainhandCreature) {
                mainhandStack = entity.getMainHandStack();
                playerHand = "mainhand";
            }
            if (offhandMagnet || offhandHasEnch || offhandCreature) {
                offhandStack = entity.getOffHandStack();
                if (mainhandMagnet || mainhandCreature) playerHand = "hand";
                else playerHand = "offhand";
            }
            if (Objects.equals(playerHand, "hand") && (mainhandEmptyDamage || offhandEmptyDamage)) {
                if (mainhandEmptyDamage) playerHand = "offhand";
                if (offhandEmptyDamage) playerHand = "mainhand";
            }
            if (mainhandCreature && mainhandUsedTick >= 200) {
                NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
                nbt.putInt("usedTick", 0);
                entity.getMainHandStack().setNbt(nbt);
                NbtClassMethod.addDamage(entity, Hand.MAIN_HAND, 1);
            }
            if (offhandCreature && offhandUsedTick >= 200) {
                NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
                nbt.putInt("usedTick", 0);
                entity.getOffHandStack().setNbt(nbt);
                NbtClassMethod.addDamage(entity, Hand.OFF_HAND, 1);
            }
            if (handCreature && !hasTag && ((Objects.equals(playerHand, "mainhand") && !mainhandEmptyDamage) || (Objects.equals(playerHand, "offhand") && !offhandEmptyDamage) || (Objects.equals(playerHand, "hand") && !mainhandEmptyDamage && !offhandEmptyDamage))) {
                CreatureMethod.attractCreatures(mainhandStack, offhandStack, entity, creatureDis, playerHand);
            }
            if (isAttracting) {
                int amplifier;
                double dis;
                if (handMagnet || hasEnch) {
                    for (int i = 0; i <= 2; i++) {
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
                    dis = 20 + amplifier * 2;
                    if (dis > finalDis) {
                        finalDis = dis;
                    }
                }
                if (hasMagneticIronSuit) finalDis *= magnetSetMultiplier;
                if (hasNetheriteMagneticIronSuit) finalDis *= netheriteMagnetSetMultiplier;
                AttractMethod.attractItems(mainhandStack, offhandStack, entity, selected, finalDis, playerHand);
                entity.addScoreboardTag("isAttracting");
            } else {
                entity.removeScoreboardTag("isAttracting");
            }
            if ((isAttracting || handCreature) && display && player) {
                String message;
                if (finalDis > 0) {
                    message = "吸引: " + finalDis;
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
                        message = message + " 传送: " + finalTelDis;
                    }
                    if (handCreature) {
                        message = message + " 生物吸引: " + creatureDis;
                    }
                } else {
                    message = "生物吸引: " + creatureDis;
                }
                if (!client) {
                    ((ServerPlayerEntity) entity).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal(message)));
                }
            }
        }
    }
}