package pitheguy.autoblocks.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import pitheguy.autoblocks.AllItems;
import pitheguy.autoblocks.Config;

public abstract class BlockBasedAutoBlockEntity extends AutoBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final ActionArea actionArea;
    public int offsetX;
    public int offsetY;
    public int offsetZ;

    public BlockBasedAutoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int baseRange, int rangeIncreaseWithUpgrade, ActionArea actionArea) {
        super(type, pos, state, inventorySize, baseRange, rangeIncreaseWithUpgrade);
        this.actionArea = actionArea;
        offsetX = -baseRange;
        offsetY = actionArea.getDirection();
        offsetZ = -baseRange;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("OffsetX", offsetX);
        tag.putInt("OffsetY", offsetY);
        tag.putInt("OffsetZ", offsetZ);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        offsetX = tag.getInt("OffsetX");
        offsetY = tag.getInt("OffsetY");
        offsetZ = tag.getInt("OffsetZ");
    }

    @Override
    public boolean canRun() {
        throw new UnsupportedOperationException("Use canRunAtPosition instead");
    }

    public abstract boolean canRunAtPosition(BlockPos pos);

    public int getMaxBlocksPerTick() {
        int maxBlocksPerTick = Config.maxBlocksPerTick;
        if (getUpgrade1().is(AllItems.SPEED_UPGRADE.get())) maxBlocksPerTick *= 2;
        if (getUpgrade2().is(AllItems.SPEED_UPGRADE.get())) maxBlocksPerTick *= 2;
        if (getUpgrade3().is(AllItems.SPEED_UPGRADE.get())) maxBlocksPerTick *= 2;
        return maxBlocksPerTick;
    }

    public void tick() {
        if (getStatus().isRunning()) {
            if (cooldown > 0) cooldown--;
            else {
                BlockPos currentPos = getRunningPosition();
                int checksLeft = getMaxBlocksPerTick();
                while (!canRunAtPosition(currentPos)) {
                    advanceToNextPosition();
                    currentPos = this.getBlockPos().offset(offsetX, offsetY, offsetZ);
                    checksLeft--;
                    if (checksLeft <= 0) return;
                }
                cooldown = getCooldown();
                if (runAction()) advanceToNextPosition();
                if (fuelSource != null) fuelSource.useFuel(getFuelPerAction());
                else LOGGER.warn("Unable to take fuel because fuelSource is null");
            }

        }
        update();
    }

    protected BlockPos getRunningPosition() {
        return this.getBlockPos().offset(offsetX, offsetY, offsetZ);
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
                if (offsetY > range + 1|| offsetY < range - 1) offsetY = actionArea.getDirection();
            }
        }
    }

    public enum ActionArea {
        ABOVE(BASE_FUEL_PER_ACTION),
        BELOW(-BASE_FUEL_PER_ACTION);
        private final int direction;

        ActionArea(int direction) {
            this.direction = direction;
        }

        public int getDirection() {
            return direction;
        }
    }
}
