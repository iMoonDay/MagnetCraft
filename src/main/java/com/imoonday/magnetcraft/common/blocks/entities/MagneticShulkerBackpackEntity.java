package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.common.blocks.MagneticShulkerBackpackBlock;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class MagneticShulkerBackpackEntity extends LootableContainerBlockEntity implements SidedInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();

    public MagneticShulkerBackpackEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state) {
        Vec3d vec3d = pos.toCenterPos().offset(state.get(MagneticShulkerBackpackBlock.FACING).getOpposite(), 0.25).add(0, 0.25, 0);
        world.attractItems(vec3d, 5, false, new ArrayList<>());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory);
        super.writeNbt(nbt);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return stack.getItem().canBeNested();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
