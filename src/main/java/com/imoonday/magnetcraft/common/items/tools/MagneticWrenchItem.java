package com.imoonday.magnetcraft.common.items.tools;

import com.imoonday.magnetcraft.common.blocks.maglev.MaglevRailBlock;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntity;
import com.imoonday.magnetcraft.common.tags.BlockTags;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.Vanishable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class MagneticWrenchItem extends CustomPickaxeItem implements Vanishable {

    public static final MagneticWrenchMaterial MATERIAL = MagneticWrenchMaterial.INSTANCE;
    public static final int ATTACK_DAMAGE = 4;
    public static final float ATTACK_SPEED = -1.45f;
    public static final int START_USING_TICK = 5;
    public static final float SPEED = 2.5f;

    public MagneticWrenchItem(Settings settings) {
        super(MATERIAL, ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(BlockTags.MAGLEV_RAILS) ? super.getMiningSpeedMultiplier(stack, state) * 10 : super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        if (i < START_USING_TICK) {
            return;
        }
        if (!world.isClient) {
            stack.damage(1, player, p -> p.sendToolBreakStatus(user.getActiveHand()));
            boolean criticalHit = world.getRandom().nextFloat() < 0.33f;
            MagneticWrenchEntity wrenchEntity = new MagneticWrenchEntity(world, player, stack, criticalHit);
            int lvl = stack.getEnchantmentLvl(EnchantmentRegistries.ACCUMULATOR_ENCHANTMENT);
            float speedMultiplier = MathHelper.clamp((float) i / 20, 0.0f, 1.0f + lvl * 0.05f);
            float damageMultiplier = MathHelper.clamp((float) i / 20, 0.0f, 1.0f + lvl * 0.25f);
            wrenchEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0f, SPEED * speedMultiplier, 1.0f);
            if (player.getAbilities().creativeMode) {
                wrenchEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            world.spawnEntity(wrenchEntity.withDamageMultiplier(damageMultiplier));
            world.playSoundFromEntity(null, wrenchEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (!player.getAbilities().creativeMode) {
                player.getInventory().removeOne(stack);
            }
        }
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.isBroken()) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        Hand hand = context.getHand();
        ItemStack stack = context.getStack();
        if (player == null) {
            return ActionResult.FAIL;
        }
        if (state.isIn(BlockTags.MAGLEV_RAILS) && hand == Hand.MAIN_HAND) {
            boolean passable = state.get(MaglevRailBlock.PASSABLE);
            if (player.isSneaking()) {
                ArrayList<BlockPos> changePos = new ArrayList<>();
                HashMap<Vec3i, Direction> offsets = new HashMap<>();
                RailShape shape = getShape(state);
                if (shape == null) {
                    return ActionResult.FAIL;
                }
                changePos.add(pos);
                if (shape.isAscending()) {
                    String directionName = shape.getName().split("_")[1];
                    Direction direction = Direction.byName(directionName);
                    if (direction != null) {
                        offsets.put(Vec3i.ZERO.offset(direction).up(), direction);
                        offsets.put(Vec3i.ZERO.offset(direction.getOpposite()), direction.getOpposite());
                    }
                } else {
                    String[] directionNames = shape.getName().split("_");
                    Direction[] directions = new Direction[]{Direction.byName(directionNames[0]), Direction.byName(directionNames[1])};
                    if (directions[0] != null && directions[1] != null) {
                        offsets.put(Vec3i.ZERO.offset(directions[0]), directions[0]);
                        offsets.put(Vec3i.ZERO.offset(directions[1]), directions[1]);
                    }
                }
                HashMap<BlockPos, Direction> newTwoPos = new HashMap<>();
                offsets.forEach((vec3i, direction) -> newTwoPos.put(pos.add(vec3i), direction));
                newTwoPos.forEach((newPos, direction) -> {
                    int count = 0;
                    while (true) {
                        if (count++ > 16) {
                            break;
                        }
                        if (!hasNext(world, passable, newPos, state)) {
                            BlockPos down = newPos.down();
                            if (hasNext(world, passable, down, state)) {
                                newPos = down;
                            } else {
                                break;
                            }
                        }
                        BlockState currentState = world.getBlockState(newPos);
                        RailShape currentShape = getShape(currentState);
                        if (currentShape == null) {
                            break;
                        }
                        changePos.add(newPos);
                        if (currentShape.isAscending()) {
                            newPos = Direction.byName(currentShape.getName().split("_")[1]) == direction ? newPos.offset(direction).up() : newPos.offset(direction);
                        } else {
                            String[] directions = currentShape.getName().split("_");
                            Direction firstDirection = Direction.byName(directions[0]);
                            Direction secondDirection = Direction.byName(directions[1]);
                            if (firstDirection != null && secondDirection != null) {
                                direction = firstDirection == secondDirection.getOpposite() ? direction : direction == firstDirection.getOpposite() ? secondDirection : firstDirection;
                                newPos = newPos.offset(direction);
                            } else {
                                break;
                            }
                        }
                    }
                });
                changePos.forEach(pos1 -> {
                    BlockState state1 = world.getBlockState(pos1);
                    world.setBlockState(pos1, state1.with(MaglevRailBlock.PASSABLE, !passable), Block.NOTIFY_LISTENERS);
                    world.addParticle(ParticleTypes.HAPPY_VILLAGER.getType(), pos1.toCenterPos().x, pos1.getY(), pos1.toCenterPos().z, 0, 0, 0);
                });
                if (player.getRandom().nextBoolean()) {
                    stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                }
            } else {
                world.setBlockState(pos, state.with(MaglevRailBlock.PASSABLE, !passable), Block.NOTIFY_LISTENERS);
                world.addParticle(ParticleTypes.HAPPY_VILLAGER.getType(), pos.toCenterPos().x, pos.getY(), pos.toCenterPos().z, 0, 0, 0);
            }
            world.playSound(pos.toCenterPos().x, pos.getY(), pos.toCenterPos().z, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.VOICE, 0.5f, 1.5f, true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static boolean hasNext(World world, boolean passable, BlockPos down, BlockState state) {
        BlockState currentState = world.getBlockState(down);
        return currentState.isIn(BlockTags.MAGLEV_RAILS) && currentState.get(MaglevRailBlock.PASSABLE) == passable && currentState.getBlock() == state.getBlock();
    }

    @Nullable
    private static RailShape getShape(BlockState state) {
        return state.contains(Properties.STRAIGHT_RAIL_SHAPE) ? state.get(Properties.STRAIGHT_RAIL_SHAPE) : (state.contains(Properties.RAIL_SHAPE) ? state.get(Properties.RAIL_SHAPE) : null);
    }

    protected static class MagneticWrenchMaterial implements ToolMaterial {

        public static final MagneticWrenchMaterial INSTANCE = new MagneticWrenchMaterial();

        @Override
        public int getDurability() {
            return 512;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 7.0f;
        }

        @Override
        public float getAttackDamage() {
            return 0;
        }

        @Override
        public int getMiningLevel() {
            return 2;
        }

        @Override
        public int getEnchantability() {
            return 1;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ItemRegistries.MAGNETIC_IRON_INGOT);
        }
    }

}
