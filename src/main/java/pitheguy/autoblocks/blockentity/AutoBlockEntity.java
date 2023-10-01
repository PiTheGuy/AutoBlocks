package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import pitheguy.autoblocks.*;
import pitheguy.autoblocks.util.ModItemHandler;

public abstract class AutoBlockEntity extends BlockEntity implements MenuProvider {
    protected final ModItemHandler inventory;
    private final int baseRange;
    private final int rangeIncreaseWithUpgrade;
    protected final ActionArea actionArea;
    public int offsetX;
    public int offsetY;
    public int offsetZ;
    public int cooldown = 0;
    public double currentFuelConsumption = 0;
    protected EnergizerBlockEntity oldFuelSource = null;
    public EnergizerBlockEntity fuelSource = null;


    public AutoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int baseRange, int rangeIncreaseWithUpgrade, ActionArea actionArea) {
        super(type, pos, state);
        this.inventory = new ModItemHandler(inventorySize);
        this.baseRange = baseRange;
        this.rangeIncreaseWithUpgrade = rangeIncreaseWithUpgrade;
        this.actionArea = actionArea;
        offsetX = -baseRange;
        offsetY = actionArea.getDirection();
        offsetZ = -baseRange;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory.toNonNullList());
        tag.putInt("OffsetX", offsetX);
        tag.putInt("OffsetY", offsetY);
        tag.putInt("OffsetZ", offsetZ);
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inv);
        this.inventory.setNonNullList(inv);
        offsetX = tag.getInt("OffsetX");
        offsetY = tag.getInt("OffsetY");
        offsetZ = tag.getInt("OffsetZ");
        cooldown = tag.getInt("Cooldown");
    }

    public Component getName() {
        return this.getDefaultName();
    }

    protected abstract Component getDefaultName();

    public abstract ItemStack getUpgrade1();

    public abstract ItemStack getUpgrade2();

    public abstract void runAction();

    public abstract boolean canRunAtPosition(BlockPos pos);

    public abstract Status getStatus();

    public int getCooldown() {
        int cooldown = Config.baseCooldown;
        if (getUpgrade1().is(AllItems.SPEED_UPGRADE.get())) cooldown /= 2;
        if (getUpgrade2().is(AllItems.SPEED_UPGRADE.get())) cooldown /= 2;
        return cooldown;
    }

    public int getMaxBlocksPerTick() {
        int maxBlocksPerTick = Config.maxBlocksPerTick;
        if (getUpgrade1().is(AllItems.SPEED_UPGRADE.get())) maxBlocksPerTick *= 2;
        if (getUpgrade2().is(AllItems.SPEED_UPGRADE.get())) maxBlocksPerTick *= 2;
        return maxBlocksPerTick;
    }

    public int getRange() {
        int range = baseRange;
        if (getUpgrade1().is(AllItems.RANGE_UPGRADE.get())) range += rangeIncreaseWithUpgrade;
        if (getUpgrade2().is(AllItems.RANGE_UPGRADE.get())) range += rangeIncreaseWithUpgrade;
        return range;
    }

    public double getFuelConsumption() {
        double fuelConsumption = 1;
        if (getUpgrade1().is(AllItems.ENERGY_UPGRADE.get())) fuelConsumption /= 2;
        if (getUpgrade2().is(AllItems.ENERGY_UPGRADE.get())) fuelConsumption /= 2;
        return fuelConsumption;
    }

    protected void findFuelSource() {
        this.oldFuelSource = fuelSource;
        this.fuelSource = null;
        double minDistance = Double.MAX_VALUE;
        for (int x = -Config.energizerSearchRadius; x <= Config.energizerSearchRadius; x++) {
            for (int y = -Config.energizerSearchRadius; y <= Config.energizerSearchRadius; y++) {
                for (int z = -Config.energizerSearchRadius; z <= Config.energizerSearchRadius; z++) {
                    double distance = x * x + y * y + z * z;
                    if (this.getLevel().getBlockState(this.worldPosition.offset(x, y, z)).getBlock() == AllBlocks.ENERGIZER.get() && distance < minDistance) {
                        EnergizerBlockEntity energizer = (EnergizerBlockEntity) this.getLevel().getBlockEntity(this.worldPosition.offset(x, y, z));
                        if (energizer.hasFuel()) {
                            this.fuelSource = energizer;
                            minDistance = distance;
                        }
                    }
                }
            }
        }
        if (fuelSource != oldFuelSource) {
            if (oldFuelSource != null) oldFuelSource.removeFuelConsumption(currentFuelConsumption);
            if (fuelSource != null) fuelSource.addFuelConsumption(currentFuelConsumption);
        }
    }

    @Override
    public Component getDisplayName() {
        return this.getName().copy().append(" (").append(Component.translatable(getStatus().getLanguageKey())).append(")");
    }

    public void tick() {
        if (getStatus().isRunning()) {
            double fuelConsumption = getFuelConsumption();
            if (currentFuelConsumption != fuelConsumption) {
                fuelSource.removeFuelConsumption(currentFuelConsumption);
                fuelSource.addFuelConsumption(fuelConsumption);
                currentFuelConsumption = fuelConsumption;
            }
            if (cooldown > 0) cooldown--;
            else {
                BlockPos currentPos = this.getBlockPos().offset(offsetX, offsetY, offsetZ);
                int checksLeft = getMaxBlocksPerTick();
                while (!canRunAtPosition(currentPos)) {
                    advanceToNextPosition();
                    currentPos = this.getBlockPos().offset(offsetX, offsetY, offsetZ);
                    checksLeft--;
                    if (checksLeft <= 0) return;
                }
                cooldown = getCooldown();
                runAction();
                advanceToNextPosition();
            }

        } else if (fuelSource != null) {
            fuelSource.removeFuelConsumption(currentFuelConsumption);
            fuelSource = null;
        }
        update();
    }

    public void discard(Level level, BlockPos pos) {
        this.getInventory().toNonNullList().forEach(item -> {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), item);
            level.addFreshEntity(itemEntity);
        });
        if (fuelSource != null) this.fuelSource.removeFuelConsumption(this.currentFuelConsumption);
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

    public void update() {
        requestModelDataUpdate();
        setChanged();
    }

    public ModItemHandler getInventory() {
        return inventory;
    }

    public enum Status {
        RUNNING("running", true),
        STOPPED("stopped", false),
        FINISHED("finished", false),
        NOT_ENOUGH_FUEL("not_enough_fuel", false),
        INVENTORY_FULL("inventory_full", false),
        NOT_ENOUGH_MATERIALS("not_enough_materials", false);

        final String id;
        final boolean running;

        Status(String id, boolean running) {
            this.id = id;
            this.running = running;
        }

        public String getLanguageKey() {
            return "container.autoblocks.status." + id;
        }

        public boolean isRunning() {
            return this.running;
        }
    }

    public enum ActionArea {
        ABOVE(1),
        BELOW(-1);
        private final int direction;

        ActionArea(int direction) {
            this.direction = direction;
        }

        public int getDirection() {
            return direction;
        }
    }
}
