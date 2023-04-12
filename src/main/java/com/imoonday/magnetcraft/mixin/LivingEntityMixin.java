package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.bomb.ElectromagneticPulseBombEntity;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntity;
import com.imoonday.magnetcraft.common.fluids.MagneticFluid;
import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.magnets.AdsorptionMagnetItem;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.common.tags.ItemTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends EntityMixin {

    private static final String ENABLE = "Enable";
    private static final String USED_TICK = "UsedTick";

    @Override
    public void addDamage(Hand hand, int damage, boolean unbreaking) {
        LivingEntity user = (LivingEntity) (Object) this;
        ItemStack stack = user.getStackInHand(hand);
        boolean creative = user instanceof PlayerEntity player && player.getAbilities().creativeMode && damage > 0;
        if (creative || !stack.isDamageable()) {
            return;
        }
        int value = getNextDamage(damage, stack);
        if (unbreaking) {
            stack.damage(value, user.getRandom(), user instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
        } else {
            stack.setDamage(value);
        }
    }

    private static int getNextDamage(int damage, ItemStack stack) {
        int stackDamage = stack.getDamage();
        int stackMaxDamage = stack.getMaxDamage();
        int finalDamage = stackDamage + damage;
        return MathHelper.clamp(finalDamage, 0, stackMaxDamage);
    }

    @Override
    public boolean isBroken(Hand hand) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (hand == null) {
            return false;
        }
        ItemStack stack = entity.getStackInHand(hand);
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
    }

    @Override
    public boolean hasEnchantment(EquipmentSlot equipmentSlot, Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getEnchantmentLvl(equipmentSlot, enchantment) > 0;
    }

    @Override
    public boolean hasEnchantmentOnArmor(Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getEnchantmentLvlOnArmor(enchantment) > 0;
    }

    @Override
    public boolean hasEnchantment(Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getEnchantmentLvl(enchantment) > 0;
    }

    @Override
    public int getEnchantmentLvl(EquipmentSlot equipmentSlot, Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(equipmentSlot));
    }

    @Override
    public int getEnchantmentLvlOnArmor(Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EquipmentSlot[] equipmentSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        return Arrays.stream(equipmentSlots).mapToInt(slot -> EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(slot))).sum();
    }

    @Override
    public int getEnchantmentLvl(Enchantment enchantment) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return Arrays.stream(EquipmentSlot.values()).mapToInt(slot -> EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(slot))).sum();

    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void tick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).world;
            if (world == null || entity.isPlayer() && entity.isSpectator()) {
                return;
            }
            this.attractTick();
            this.usedTickHandler();
            this.repairMagnetsInFluid();
            this.displayMessage();
            MagneticFluid.tick(entity);
            if (world instanceof ServerWorld serverWorld) {
                AdsorptionMagnetItem.tickCheck(serverWorld);
            }
            if (entity.isOnGround()) {
                entity.setIgnoreFallDamage(false);
            }
            if (entity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT) && entity.hasEnchantment(EquipmentSlot.CHEST, EnchantmentRegistries.DEGAUSSING_PROTECTION_ENCHANTMENT)) {
                entity.removeStatusEffect(EffectRegistries.UNATTRACT_EFFECT);
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "drop")
    public void drop(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            Entity sourceEntity = source.getSource();
            if (sourceEntity != null) {
                LivingEntity entity = (LivingEntity) (Object) this;
                World world = player.world;
                ItemStack stack;
                if (sourceEntity instanceof TridentEntity tridentEntity) {
                    stack = tridentEntity.asItemStack();
                } else if (sourceEntity instanceof MagneticWrenchEntity wrench) {
                    stack = wrench.asItemStack();
                } else if (sourceEntity instanceof ElectromagneticPulseBombEntity bomb) {
                    stack = bomb.getUserStack();
                } else {
                    stack = player.getMainHandStack();
                }
                boolean hasEnchantment = stack.hasEnchantment(EnchantmentRegistries.AUTOMATIC_LOOTING_ENCHANTMENT);
                if (hasEnchantment) {
                    world.getOtherEntities(null, entity.getBoundingBox(), targetEntity -> ((targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.age == 0)).forEach(targetEntity -> {
                        if (targetEntity instanceof ExperienceOrbEntity orb) {
                            int amount = orb.getExperienceAmount();
                            player.addExperience(amount);
                        } else {
                            player.getInventory().offerOrDrop(((ItemEntity) targetEntity).getStack());
                        }
                        targetEntity.kill();
                    });
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity) && entity.isAttracting() && entity.getEnable()) {
            entity.setAttracting(false);
            entity.setEnable(false);
        }
    }

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    protected void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.ignoreFallDamage() && source.isOf(DamageTypes.FALL) && !entity.world.isClient) {
            entity.setIgnoreFallDamage(false);
            cir.setReturnValue(false);
        }
        if (entity.hasEnchantmentOnArmor(EnchantmentRegistries.ELECTROMAGNETIC_PROTECTION_ENCHANTMENT) && source.isIndirect()) {
            int lvl = entity.getEnchantmentLvlOnArmor(EnchantmentRegistries.ELECTROMAGNETIC_PROTECTION_ENCHANTMENT);
            if (entity.getRandom().nextFloat() < lvl / 32.0f) {
                entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.VOICE, 1.0f, 1.0f);
                cir.setReturnValue(false);
            }
        }
        if (entity.isShuttling()) {
            cir.setReturnValue(false);
        }
    }

    private void attractTick() {
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean isAttracting = checkAttracting();
        entity.setAttracting(isAttracting, isAttracting ? computeAttractDis() : 0);
    }

    private boolean checkAttracting() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return this.computeAttractDis() > 0 && entity.canAttract() && entity.isAlive();
    }

    private double computeAttractDis() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ModConfig.DefaultValue value = ModConfig.getConfig().value;
        ItemStack mainhandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offhandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        boolean mainhandEnabled = mainhandStack.getNbt() != null && mainhandStack.getNbt().getBoolean(ENABLE);
        boolean offhandEnabled = offhandStack.getNbt() != null && offhandStack.getNbt().getBoolean(ENABLE);
        boolean mainhandElectromagnet = mainhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPermanent = mainhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandPolar = mainhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && mainhandEnabled;
        boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;
        boolean offhandElectromagnet = offhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && offhandEnabled;
        boolean offhandPermanent = offhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && offhandEnabled;
        boolean offhandPolar = offhandStack.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && offhandEnabled;
        boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;
        boolean handMagnet = mainhandMagnet || offhandMagnet;
        boolean hasEnch = entity.hasEnchantment(EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandHasEnch = mainhandStack.hasEnchantment(EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = offhandStack.hasEnchantment(EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);
        boolean horseArmorAttracting = entity instanceof HorseEntity horseEntity && horseEntity.getArmorType().isOf(ItemRegistries.MAGNETIC_IRON_HORSE_ARMOR);
        boolean equippingBackpack = entity.getEquippedStack(EquipmentSlot.CHEST).isOf(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM);
        double[] minDis = new double[]{value.electromagnetAttractMinDis, value.permanentMagnetAttractMinDis, value.polarMagnetAttractMinDis};
        double horseArmorAttractDis = value.horseArmorAttractDis;
        double backpackAttractDis = value.backpackAttractDis;
        double magnetHandSpacing = value.magnetHandSpacing;
        double attractDefaultDis = value.attractDefaultDis;
        double disPerAmplifier = value.disPerAmplifier;
        double enchDefaultDis = value.enchDefaultDis;
        double disPerLvl = value.disPerLvl;
        double magnetSetMultiplier = value.magnetSetMultiplier >= 1 ? value.magnetSetMultiplier : 1.5;
        double netheriteMagnetSetMultiplier = value.netheriteMagnetSetMultiplier >= 1 ? value.netheriteMagnetSetMultiplier : 2;
        boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
        boolean handPermanent = mainhandPermanent || offhandPermanent;
        boolean handPolar = mainhandPolar || offhandPolar;
        boolean mainhandMagnetHasEnch = mainhandMagnet && mainhandHasEnch;
        boolean offhandMagnetHasEnch = offhandMagnet && offhandHasEnch;
        boolean handMagnetHasEnch = mainhandMagnetHasEnch || offhandMagnetHasEnch;
        boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
        boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
        boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};
        int enchLvl = entity.getEnchantmentLvl(EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        double enchMinDis = enchDefaultDis + disPerLvl;
        double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;
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
                    finalDis = Math.max(dis, finalDis);
                }
            }
        }
        if (hasEffect) {
            StatusEffectInstance effect = entity.getStatusEffect(EffectRegistries.ATTRACT_EFFECT);
            if (effect != null) {
                amplifier = effect.getAmplifier();
                dis = attractDefaultDis + amplifier * disPerAmplifier;
                finalDis = Math.max(dis, finalDis);
            }
        }
        if (horseArmorAttracting) {
            dis = horseArmorAttractDis;
            finalDis = Math.max(dis, finalDis);
        }
        if (equippingBackpack) {
            dis = backpackAttractDis;
            finalDis = Math.max(dis, finalDis);
        }
        if (MagneticIronArmorItem.isInMagneticIronSuit(entity)) {
            finalDis *= magnetSetMultiplier;
        }
        if (NetheriteMagneticIronArmorItem.isInNetheriteMagneticIronSuit(entity)) {
            finalDis *= netheriteMagnetSetMultiplier;
        }
        return finalDis;
    }

    private void displayMessage() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ModConfig.DefaultValue value = ModConfig.getConfig().value;
        double creatureDis = value.creatureMagnetAttractDis;
        ItemStack mainhandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offhandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        boolean mainhandElectromagnet = mainhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM);
        boolean mainhandPermanent = mainhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM);
        boolean mainhandCreature = mainhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM);
        boolean offhandElectromagnet = offhandStack.isOf(ItemRegistries.ELECTROMAGNET_ITEM);
        boolean offhandPermanent = offhandStack.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM);
        boolean offhandCreature = offhandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM);
        boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
        boolean handPermanent = mainhandPermanent || offhandPermanent;
        boolean handCreature = mainhandCreature || offhandCreature;
        boolean display = ModConfig.getConfig().displayActionBar;
        boolean isAttracting = entity.isAttracting();
        double finalDis = entity.getAttractDis();
        double finalTelDis = 0;
        if ((isAttracting || handCreature) && display && entity instanceof PlayerEntity player) {
            String message;
            Text text;
            if (isAttracting) {
                message = ": " + finalDis;
                text = Text.translatable("text.magnetcraft.message.attract").append(message);
                if (handElectromagnet || handPermanent) {
                    double telDis = 0;
                    if (mainhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis + value.magnetHandSpacing;
                    } else if (mainhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis + value.magnetHandSpacing;
                    }
                    finalTelDis = Math.max(telDis, finalTelDis);
                    if (offhandElectromagnet) {
                        telDis = value.electromagnetTeleportMinDis;
                    } else if (offhandPermanent) {
                        telDis = value.permanentMagnetTeleportMinDis;
                    }
                    finalTelDis = Math.max(telDis, finalTelDis);
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
            if (!player.world.isClient && (!entity.getEquippedStack(EquipmentSlot.FEET).hasEnchantment(EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT) || !player.getMagneticLevitationMode() && player.getLevitationTick() <= 0)) {
                player.sendMessage(text, true);
            }
        }
    }

    private void repairMagnetsInFluid() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack mainhandStack = entity.getMainHandStack();
        ItemStack offhandStack = entity.getOffHandStack();
        if (entity instanceof PlayerEntity player && player.isSubmergedIn(FluidTags.MAGNETIC_FLUID)) {
            boolean success = false;
            int mainhandRepair = 0;
            int offhandRepair = 0;
            BlockPos pos = new BlockPos((int) player.getEyePos().x, (int) player.getEyePos().y, (int) player.getEyePos().z);
            BlockState state = player.world.getBlockState(pos);
            if (!player.world.isClient && state.isOf(FluidRegistries.MAGNETIC_FLUID)) {
                Random random = entity.getRandom();
                if (mainhandStack.isIn(ItemTags.MAGNETS) && mainhandStack.isDamageable() && mainhandStack.isDamaged()) {
                    int damage = mainhandStack.getDamage();
                    int maxDamage = mainhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        entity.addDamage(Hand.MAIN_HAND, -maxDamage / 10, false);
                        mainhandRepair = damage - mainhandStack.getDamage();
                        success = true;
                    }
                }
                if (offhandStack.isIn(ItemTags.MAGNETS) && offhandStack.isDamageable() && offhandStack.isDamaged()) {
                    int damage = offhandStack.getDamage();
                    int maxDamage = offhandStack.getMaxDamage();
                    if (random.nextBetween(1, maxDamage * 200) <= damage) {
                        entity.addDamage(Hand.OFF_HAND, -maxDamage / 10, false);
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
    }

    private void usedTickHandler() {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack mainHandStack = entity.getMainHandStack();
        ItemStack offHandStack = entity.getOffHandStack();
        int mainhandUsedTick = mainHandStack.getNbt() != null ? mainHandStack.getNbt().getInt(USED_TICK) : 0;
        int offhandUsedTick = offHandStack.getNbt() != null ? offHandStack.getNbt().getInt(USED_TICK) : 0;
        int tickPerDamage = ModConfig.getConfig().value.secPerDamage * 20;
        boolean mainhandCreature = mainHandStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && mainHandStack.getNbt() != null && mainHandStack.getNbt().getBoolean(ENABLE);
        while (mainhandCreature && mainhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = mainHandStack.getOrCreateNbt();
            nbt.putInt(USED_TICK, mainhandUsedTick - tickPerDamage);
            mainHandStack.setNbt(nbt);
            entity.addDamage(Hand.MAIN_HAND, 1, true);
            mainhandUsedTick -= tickPerDamage;
        }
        while (mainhandCreature && offhandUsedTick >= tickPerDamage) {
            NbtCompound nbt = offHandStack.getOrCreateNbt();
            nbt.putInt(USED_TICK, offhandUsedTick - tickPerDamage);
            mainHandStack.setNbt(nbt);
            entity.addDamage(Hand.OFF_HAND, 1, true);
            offhandUsedTick -= tickPerDamage;
        }
    }

}