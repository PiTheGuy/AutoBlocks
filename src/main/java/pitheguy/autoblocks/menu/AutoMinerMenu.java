package pitheguy.autoblocks.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import pitheguy.autoblocks.AllBlocks;
import pitheguy.autoblocks.blockentity.AbstractMinerBlockEntity;
import pitheguy.autoblocks.init.ModMenuTypes;

public class AutoMinerMenu extends AbstractMinerMenu {
    public AutoMinerMenu(int windowID, Inventory playerInv, AbstractMinerBlockEntity tile) {
        super(windowID, playerInv, tile, ModMenuTypes.AUTO_MINER.get());
    }

    public AutoMinerMenu(int windowID, Inventory playerInv, FriendlyByteBuf data) {
        super(windowID, playerInv, data, ModMenuTypes.AUTO_MINER.get());
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, AllBlocks.AUTO_MINER.get());
    }
}
