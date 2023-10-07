package pitheguy.autoblocks.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import pitheguy.autoblocks.*;
import pitheguy.autoblocks.util.ModItemHandler;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class AutoBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int BASE_FUEL_PER_ACTION = 60;
    protected final ModItemHandler inventory;
    protected final int inventorySize;
    private final int baseRange;
    private final int rangeIncreaseWithUpgrade;
    public int cooldown = 0;
    public EnergizerBlockEntity fuelSource = null;
    protected int mainInventoryStartSlot = 4;
    protected int mainInventoryEndSlot = 58;


    public AutoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int baseRange, int rangeIncreaseWithUpgrade) {
        super(type, pos, state);
        this.inventory = new ModItemHandler(inventorySize);
        this.inventorySize = inventorySize;
        this.baseRange = baseRange;
        this.rangeIncreaseWithUpgrade = rangeIncreaseWithUpgrade;
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
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inv);
        this.inventory.setNonNullList(inv);
        cooldown = tag.getInt("Cooldown");
    }

    public Component getName() {
        return this.getDefaultName();
    }

    protected abstract Component getDefaultName();

    public abstract ItemStack getUpgrade1();

    public abstract ItemStack getUpgrade2();

    public abstract ItemStack getUpgrade3();

    public abstract void runAction();

    public abstract boolean canRun();

    public abstract Status getStatus();

    public int getCooldown() {
        int cooldown = Config.baseCooldown;
        if (getUpgrade1().is(AllItems.SPEED_UPGRADE.get())) cooldown /= 2;
        if (getUpgrade2().is(AllItems.SPEED_UPGRADE.get())) cooldown /= 2;
        if (getUpgrade3().is(AllItems.SPEED_UPGRADE.get())) cooldown /= 2;
        return cooldown;
    }

    public int getRange() {
        int range = baseRange;
        if (getUpgrade1().is(AllItems.RANGE_UPGRADE.get())) range += rangeIncreaseWithUpgrade;
        if (getUpgrade2().is(AllItems.RANGE_UPGRADE.get())) range += rangeIncreaseWithUpgrade;
        if (getUpgrade3().is(AllItems.RANGE_UPGRADE.get())) range += rangeIncreaseWithUpgrade;
        return range;
    }

    public double getFuelPerAction() {
        double fuelPerAction = BASE_FUEL_PER_ACTION;
        if (getUpgrade1().is(AllItems.ENERGY_UPGRADE.get())) fuelPerAction /= 2;
        if (getUpgrade2().is(AllItems.ENERGY_UPGRADE.get())) fuelPerAction /= 2;
        if (getUpgrade3().is(AllItems.ENERGY_UPGRADE.get())) fuelPerAction /= 2;
        return fuelPerAction;
    }

    protected void findFuelSource() {
        this.fuelSource = null;
        double minDistance = Double.MAX_VALUE;
        for (int x = -Config.energizerSearchRadius; x <= Config.energizerSearchRadius; x++) {
            for (int y = -Config.energizerSearchRadius; y <= Config.energizerSearchRadius; y++) {
                for (int z = -Config.energizerSearchRadius; z <= Config.energizerSearchRadius; z++) {
                    double distance = x * x + y * y + z * z;
                    if (this.getLevel().getBlockState(this.worldPosition.offset(x, y, z)).getBlock() == AllBlocks.ENERGIZER.get() && distance < minDistance) {
                        EnergizerBlockEntity energizer = (EnergizerBlockEntity) this.getLevel().getBlockEntity(this.worldPosition.offset(x, y, z));
                        if (energizer.hasFuel(getFuelPerAction())) {
                            this.fuelSource = energizer;
                            minDistance = distance;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return this.getName().copy().append(" (").append(Component.translatable(getStatus().getLanguageKey())).append(")");
    }

    public void tick() {
        boolean dirty = false;
        if (getStatus().isRunning()) {
            if (cooldown > 0) {
                cooldown--;
                dirty = true;
            } else {
                if (canRun()) {
                    runAction();
                    cooldown = getCooldown();
                    if (fuelSource != null) fuelSource.useFuel(getFuelPerAction());
                    else LOGGER.warn("Unable to take fuel because fuelSource is null");
                }
            }

        }
        if (dirty) update();
    }

    public void discard(Level level, BlockPos pos) {
        this.getInventory().toNonNullList().forEach(item -> {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), item);
            level.addFreshEntity(itemEntity);
        });
    }

    protected void addItemToInventory(ItemStack itemStack) {
        if (!this.canAddItem(itemStack)) return;
        for (int i = mainInventoryStartSlot; i < mainInventoryEndSlot; i++) {
            itemStack = this.inventory.insertItem(i, itemStack, false);
            if (itemStack.isEmpty()) break;
        }
    }

    protected boolean canAddItem(ItemStack stack) {
        if (this.hasInventorySpace()) return true;
        int count = stack.getCount();
        for (int i = mainInventoryStartSlot; i < mainInventoryEndSlot; i++) {
            if (this.inventory.getStackInSlot(i).getItem() == stack.getItem()) {
                count -= 64 - this.inventory.getStackInSlot(i).getCount();
                if (count <= 0) return true;
            }
        }
        return false;
    }

    protected boolean hasInventorySpace() {
        return IntStream.range(mainInventoryStartSlot, mainInventoryEndSlot).anyMatch(i -> this.inventory.getStackInSlot(i).isEmpty());
    }

    protected void removeItemFromInventory(Predicate<Item> predicate) {
        for (int i = mainInventoryStartSlot; i < mainInventoryEndSlot; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (predicate.test(stack.getItem())) {
                this.inventory.decrStackSize(i, BASE_FUEL_PER_ACTION);
                return;
            }
        }
    }

    protected void removeItemFromInventory(Item item) {
        removeItemFromInventory(slotItem -> item == slotItem);
    }

    protected boolean hasItemInInventory(Predicate<Item> predicate) {
        for (int i = mainInventoryStartSlot; i < mainInventoryEndSlot; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (predicate.test(stack.getItem())) return true;
        }
        return false;
    }

    protected boolean hasItemInInventory(Item item) {
        return hasItemInInventory(slotItem -> item == slotItem);
    }

    public void update() {
        requestModelDataUpdate();
        setChanged();
    }

    public ModItemHandler getInventory() {
        return inventory;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory.toNonNullList(), slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory.toNonNullList(), slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public int getContainerSize() {
        return inventorySize;
    }

    public enum Status {
        RUNNING("running", true),
        STOPPED("stopped", false),
        WAITING("waiting", false),
        FINISHED("finished", false),
        NOT_ENOUGH_FUEL("not_enough_fuel", false),
        INVENTORY_FULL("inventory_full", false),
        NOT_ENOUGH_MATERIALS("not_enough_materials", false),
        MISSING_TOOL("missing_tool", false);

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
}
