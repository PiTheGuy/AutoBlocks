package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
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

public class AutoFarmerBlockEntity extends BlockBasedAutoBlockEntity {
    public AutoFarmerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_FARMER.get(), pos, state, 58, 10, 2, ActionArea.BELOW);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_farmer");
    }

    @Override
    public ItemStack getUpgrade1() {
        return this.inventory.getStackInSlot(1);
    }

    @Override
    public ItemStack getUpgrade2() {
        return this.inventory.getStackInSlot(2);
    }

    @Override
    public ItemStack getUpgrade3() {
        return this.inventory.getStackInSlot(3);
    }

    @Override
    public void runAction() {
        BlockPos pos = getRunningPosition();
        BlockState state = level.getBlockState(pos);
        if (canTillBlock(pos)){
            level.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack hoeStack = this.inventory.getStackInSlot(0);
            if (hoeStack.hurt(1, level.random, null)) hoeStack.shrink(1);
        } else if (state.isAir() && level.getBlockState(pos.below()).is(Blocks.FARMLAND)) {
            Item seedItem = getSeedItem();
            level.setBlock(pos, ((BlockItem) seedItem).getBlock().defaultBlockState(), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1, 1);
            removeItemFromInventory(seedItem);
        } else if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            List<ItemStack> drops = Block.getDrops(level.getBlockState(pos), (ServerLevel) level, pos, null);
            drops.stream().filter(drop -> !drop.isEmpty()).forEach(this::addItemToInventory);
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        if (canTillBlock(pos)) return true;
        BlockState state = level.getBlockState(pos);
        if (state.isAir() && level.getBlockState(pos.below()).is(Blocks.FARMLAND)) return true;
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
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

    private static boolean isSeedItem(Item item) {
        if (item instanceof BlockItem blockItem) return blockItem.getBlock() instanceof CropBlock;
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
            if (isSeedItem(stack.getItem())) return stack.getItem();
        }
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoFarmerMenu(containerId, playerInventory, this);
    }
}
