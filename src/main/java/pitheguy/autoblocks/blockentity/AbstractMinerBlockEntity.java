package pitheguy.autoblocks.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pitheguy.autoblocks.AllItems;
import pitheguy.autoblocks.items.FilterItem;

import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractMinerBlockEntity extends BlockBasedAutoBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AbstractMinerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int baseRange, int rangeIncreaseWithUpgrade, ActionArea actionArea) {
        super(type, pos, state, 58, baseRange, rangeIncreaseWithUpgrade, actionArea);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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
    public boolean runAction() {
        if (level != null && !level.isClientSide) {
            BlockPos minePos = getRunningPosition();
            List<ItemStack> drops = Block.getDrops(level.getBlockState(minePos), (ServerLevel) level, minePos, null);
            drops.stream().filter(drop -> !drop.isEmpty()).forEach(this::addItemToInventory);
            level.destroyBlock(minePos, false);
        }
        return true;
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
                if (offsetY < level.getMinBuildHeight() || offsetY > level.getMaxBuildHeight()) {
                    offsetX = -range;
                    offsetY = actionArea.getDirection();
                    offsetZ = -range;
                }
            }
        }
    }

    private boolean matchesFilter(Block block) {
        ItemStack filterStack = this.inventory.getStackInSlot(0);
        if (!filterStack.is(AllItems.FILTER.get())) return true;
        if (!FilterItem.hasFilterBlock(filterStack)) return true;
        Block filterBlock = FilterItem.getFilterBlock(filterStack);
        return block == filterBlock;
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        BlockState blockStateMined = level.getBlockState(pos);
        if (!level.getFluidState(pos).isEmpty()) {
            //LOGGER.debug("Skipped block at {} because a fluid was detected", pos);
            return false;
        }
        if (level.getBlockEntity(pos) != null) {
            //LOGGER.debug("Skipped block at {} because a block entity was detected", pos);
            return false;
        }
        if (!(blockStateMined.getDestroySpeed(level, pos) >= 0)) {
            //LOGGER.debug("Skipped block at {} because it's unbreakable", pos);
            return false;
        }
        if (!matchesFilter(blockStateMined.getBlock())) {
            //LOGGER.debug("Skipped block at {} because it doesn't match the filter", pos);
            return false;
        }
        return true;
    }

    @Override
    public Status getStatus() {
        if (!hasInventorySpace()) return Status.INVENTORY_FULL;
        findFuelSource();
        if (fuelSource == null) return Status.NOT_ENOUGH_FUEL;
        return Status.RUNNING;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(4, 58).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 4;
    }
}
