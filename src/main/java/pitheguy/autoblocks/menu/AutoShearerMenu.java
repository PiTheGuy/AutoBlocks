package pitheguy.autoblocks.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import pitheguy.autoblocks.AllBlocks;
import pitheguy.autoblocks.blockentity.AutoShearerBlockEntity;
import pitheguy.autoblocks.init.ModMenuTypes;
import pitheguy.autoblocks.menu.itemhandlers.*;
import pitheguy.autoblocks.util.FunctionalIntDataSlot;

import java.util.Objects;

public class AutoShearerMenu extends AutoBlockMenu {
    public AutoShearerBlockEntity tileEntity;
    protected final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntDataSlot cooldown;

    //Server constructor
    public AutoShearerMenu(final int windowId, final Inventory playerInv, final AutoShearerBlockEntity tile) {
        super(ModMenuTypes.AUTO_SHEARER.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos());
        this.tileEntity = tile;
        final int slotSizePlus2 = 18;
        final int startX = 8;
        //Shearer Inventory
        this.addSlot(new PredicateSlotItemHandler(tile.getInventory(), 0, 177, 18, item -> item instanceof ShearsItem));
        this.addSlot(new UpgradeSlotItemHandler(tile.getInventory(), 1, 177, 72));
        this.addSlot(new UpgradeSlotItemHandler(tile.getInventory(), 2, 177, 90));
        this.addSlot(new UpgradeSlotItemHandler(tile.getInventory(), 3, 177, 108));
        this.addDataSlot(cooldown = new FunctionalIntDataSlot(() -> this.tileEntity.cooldown,
                value -> this.tileEntity.cooldown = value));

        int inventoryStartY = 18;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new ExcludeUpgradesSlotItemHandler(tile.getInventory(), 4+(row*9)+col, startX + (col * slotSizePlus2), inventoryStartY + (row * slotSizePlus2)));
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
    public AutoShearerMenu(final int windowID, final Inventory playerInv, final FriendlyByteBuf data) {
        this(windowID, playerInv, getTileEntity(playerInv, data));
    }

    private static AutoShearerBlockEntity getTileEntity(final Inventory playerInv, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInv, "playerInv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInv.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof AutoShearerBlockEntity shearer) {
            return shearer;
        }
        throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, AllBlocks.AUTO_SHEARER.get());
    }

    public int getCooldownScaled() {
        int timeOnCooldown = this.tileEntity.getCooldown() - this.cooldown.get();
        return timeOnCooldown != 0 ? timeOnCooldown * 70 / this.tileEntity.getCooldown() : 0;
    }
}
