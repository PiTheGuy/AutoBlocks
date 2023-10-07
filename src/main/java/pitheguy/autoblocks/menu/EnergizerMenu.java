package pitheguy.autoblocks.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import pitheguy.autoblocks.AllBlocks;
import pitheguy.autoblocks.blockentity.EnergizerBlockEntity;
import pitheguy.autoblocks.init.ModMenuTypes;
import pitheguy.autoblocks.menu.itemhandlers.MultiItemSlotItemHandler;
import pitheguy.autoblocks.util.FunctionalIntDataSlot;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EnergizerMenu extends AbstractContainerMenu {
    public EnergizerBlockEntity tileEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntDataSlot fuel;

    //Server constructor
    public EnergizerMenu(final int windowID, final Inventory playerInv, final EnergizerBlockEntity tile) {
        super(ModMenuTypes.ENERGIZER.get(), windowID);
        this.canInteractWithCallable = ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos());
        this.tileEntity = tile;
        final int slotSizePlus2 = 18;
        final int startX = 8;
        //Energizer Inventory
        this.addSlot(new MultiItemSlotItemHandler(tile.getInventory(), 0, 8, 18, EnergizerBlockEntity.FUEL_BY_ITEM.keySet()));
        this.addDataSlot(fuel = new FunctionalIntDataSlot(() -> (int) this.tileEntity.fuel,
                value -> this.tileEntity.fuel = value));
        //Hotbar
        int hotbarY = 108;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, (startX + (col * slotSizePlus2)), hotbarY));
        }
        //Main Player Inventory
        int startY = 50;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, 9+(row*9)+col, startX + (col * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }

    }
    //Client constructor
    public EnergizerMenu(final int windowID, final Inventory playerInv, final FriendlyByteBuf data) {
        this(windowID, playerInv, getTileEntity(playerInv, data));
    }

    private static EnergizerBlockEntity getTileEntity(final Inventory playerInv, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInv, "playerInv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInv.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof EnergizerBlockEntity energizer) {
            return energizer;
        }
        throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            final int containerSlots = this.slots.size() - player.getInventory().items.size();
            if (index < containerSlots) {
                if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, AllBlocks.ENERGIZER.get());
    }

    @OnlyIn(Dist.CLIENT)
    public int getFuelScaled() {
        return this.fuel.get() != 0 && this.tileEntity.fuel != 0 ? this.fuel.get() * 124 / EnergizerBlockEntity.MAX_FUEL : 0;
    }
}
