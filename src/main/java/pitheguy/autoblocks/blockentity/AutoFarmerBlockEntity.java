package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoFarmerMenu;

import java.util.List;
import java.util.stream.IntStream;

public class AutoFarmerBlockEntity extends BlockBasedAutoBlockEntity {
    public static final int[] SLOTS_FOR_UP = new int[]{0, 1};
    public static final int[] SLOTS_FOR_DOWN = IntStream.range(5, 59).toArray();

    public AutoFarmerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_FARMER.get(), pos, state, 59, 10, 2, ActionArea.BELOW);
        this.mainInventoryStartSlot = 5;
        this.mainInventoryEndSlot = 59;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_farmer");
    }

    @Override
    public ItemStack getUpgrade1() {
        return this.inventory.getStackInSlot(2);
    }

    @Override
    public ItemStack getUpgrade2() {
        return this.inventory.getStackInSlot(3);
    }

    @Override
    public ItemStack getUpgrade3() {
        return this.inventory.getStackInSlot(4);
    }

    @Override
    public boolean runAction() {
        BlockPos pos = getRunningPosition();
        BlockState state = level.getBlockState(pos);
        if (canTillBlock(pos)) {
            level.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack hoeStack = this.inventory.getStackInSlot(0);
            if (hoeStack.hurt(1, level.random, null)) hoeStack.shrink(1);
            return true;
        } else if (state.isAir() && level.getBlockState(pos.below()).is(Blocks.FARMLAND)) {
            Item seedItem = getSeedItem();
            level.setBlock(pos, ((BlockItem) seedItem).getBlock().defaultBlockState(), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1, 1);
            removeItemFromInventory(seedItem);
            return true;
        } else if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            List<ItemStack> drops = Block.getDrops(level.getBlockState(pos), (ServerLevel) level, pos, null);
            drops.stream().filter(drop -> !drop.isEmpty()).forEach(this::addItemToInventory);
            level.destroyBlock(pos, false);
            return false;
        } else if (inventory.getStackInSlot(1).is(Items.BONE_MEAL) && state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)) {
            crop.growCrops(level, pos, state);
            level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1, 1);
            inventory.decrStackSize(1, 1);
            return false;
        }
        return true;
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        if (canTillBlock(pos)) return true;
        BlockState state = level.getBlockState(pos);
        if (state.isAir() && level.getBlockState(pos.below()).is(Blocks.FARMLAND)) return true;
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) return true;
        return inventory.getStackInSlot(1).is(Items.BONE_MEAL) && state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state);
    }

    private boolean canTillBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH)) && level.getBlockState(pos.above()).isAir();
    }

    protected void advanceToNextPosition() {
        int range = getRange();
        offsetX++;
        if (offsetX > range) {
            offsetX = -range;
            offsetZ++;
            if (offsetZ > range) {
                offsetZ = -range;
                offsetY += actionArea.getDirection();
                if (offsetY > range + 1 || offsetY < range - 1) offsetY = 0;
            }
        }
    }

    @Override
    public int getMaxBlocksPerTick() {
        return super.getMaxBlocksPerTick() * 10;
    }

    private static boolean isSeedItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) return blockItem.getBlock() instanceof CropBlock;
        return false;
    }

    @Override
    public Status getStatus() {
        if (!hasItemInInventory(AutoFarmerBlockEntity::isSeedItem)) return Status.NOT_ENOUGH_MATERIALS;
        if (this.inventory.getStackInSlot(0).isEmpty()) return Status.MISSING_TOOL;
        if (!hasInventorySpace()) return Status.INVENTORY_FULL;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        return Status.RUNNING;
    }

    protected Item getSeedItem() {
        for (int i = mainInventoryStartSlot; i < mainInventoryEndSlot; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (isSeedItem(stack)) return stack.getItem();
        }
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoFarmerMenu(containerId, playerInventory, this);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_UP;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return (index == 0 && stack.getItem() instanceof HoeItem) || (index == 1 && stack.is(Items.BONE_MEAL));
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 5;
    }
}
