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
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;
import pitheguy.autoblocks.init.ModMenuTypes;
import pitheguy.autoblocks.menu.itemhandlers.*;
import pitheguy.autoblocks.util.FunctionalIntDataSlot;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AutoPlacerMenu extends AbstractContainerMenu {
    public AutoPlacerBlockEntity tileEntity;
    protected final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntDataSlot cooldown;

    //Server constructor
    public AutoPlacerMenu(final int windowID, final Inventory playerInv, final AutoPlacerBlockEntity tile) {
        super(ModMenuTypes.AUTO_PLACER.get(), windowID);
        this.canInteractWithCallable = ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos());
        this.tileEntity = tile;
        final int slotSizePlus2 = 18;
        final int startX = 8;
        //Miner Inventory
        this.addSlot(new UpgradeSlotItemHandler(tile.getInventory(), 0, 177, 90));
        this.addSlot(new UpgradeSlotItemHandler(tile.getInventory(), 1, 177, 108));
        this.addDataSlot(cooldown = new FunctionalIntDataSlot(() -> this.tileEntity.cooldown,
                value -> this.tileEntity.cooldown = value));

        int inventoryStartY = 18;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new ExcludeUpgradesSlotItemHandler(tile.getInventory(), 2+(row*9)+col, startX + (col * slotSizePlus2), inventoryStartY + (row * slotSizePlus2)));
            }
        }


        //Hotbar
        int hotbarY = 198;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, (startX + (col * slotSizePlus2)), hotbarY));
        }
        //Main Player Inventory
        int startY = 140;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, 9+(row*9)+col, startX + (col * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }

    }
    //Client constructor
    public AutoPlacerMenu(final int windowID, final Inventory playerInv, final FriendlyByteBuf data) {
        this(windowID, playerInv, getTileEntity(playerInv, data));
    }

    private static AutoPlacerBlockEntity getTileEntity(final Inventory playerInv, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInv, "playerInv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInv.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof AutoPlacerBlockEntity placer) {
            return placer;
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
        return stillValid(canInteractWithCallable, player, AllBlocks.AUTO_PLACER.get());
    }

    @OnlyIn(Dist.CLIENT)
    public int getCooldownScaled() {
        int timeOnCooldown = this.tileEntity.getCooldown() - this.cooldown.get();
        return timeOnCooldown != 0 ? timeOnCooldown * 70 / this.tileEntity.getCooldown() : 0;
    }
}