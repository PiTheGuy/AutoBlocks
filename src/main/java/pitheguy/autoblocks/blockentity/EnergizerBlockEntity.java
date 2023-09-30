package pitheguy.autoblocks.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pitheguy.autoblocks.init.ModBlocKEntityTypes;
import pitheguy.autoblocks.menu.EnergizerMenu;
import pitheguy.autoblocks.util.ModItemHandler;

public class EnergizerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ModItemHandler inventory;
    public double fuel = 0;
    public double fuelConsumption = 0;
    public static final int MAX_FUEL = 20000;
    public static final int FUEL_PER_ITEM = 200;

    public EnergizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocKEntityTypes.ENERGIZER.get(), pos, state);
        this.inventory = new ModItemHandler(1);
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
        tag.putDouble("Fuel", fuel);
        tag.putDouble("FuelConsumption", fuelConsumption);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inv);
        this.inventory.setNonNullList(inv);
        this.fuel = tag.getDouble("Fuel");
        this.fuelConsumption = tag.getDouble("FuelConsumption");
    }

    public Component getName() {
        return this.getDefaultName();
    }

    private Component getDefaultName() {
        return Component.translatable("container.autoblocks.energizer");
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnergizerMenu(containerId, playerInventory, this);
    }

    public void tick() {
        boolean dirty = false;
        if (level != null && !level.isClientSide) {
            while (!inventory.getStackInSlot(0).isEmpty() && fuel <= MAX_FUEL - FUEL_PER_ITEM) {
                fuel += FUEL_PER_ITEM;
                inventory.decrStackSize(0, 1);
                dirty = true;
            }
            if (hasFuel() && fuelConsumption > 0) {
                fuel -= fuelConsumption;
                update();
            }
        }
        if (dirty) this.update();
    }

    public boolean hasFuel() {
        return fuel > fuelConsumption;
    }

    public void addFuelConsumption(double amount) {
        fuelConsumption += amount;
    }

    public void removeFuelConsumption(double amount) {
        if (fuelConsumption >= amount) fuelConsumption -= amount;
        else {
            LOGGER.error("Tried to remove more fuel consumption than was registered");
            throw new IllegalStateException("Tried to remove more fuel consumption than was registered");
        }
    }



    public void update() {
        requestModelDataUpdate();
        setChanged();
    }

    public ModItemHandler getInventory() {
        return inventory;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return new int[]{};
        else return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return stack.is(Items.REDSTONE);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
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
}
