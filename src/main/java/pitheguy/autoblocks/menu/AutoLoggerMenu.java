package pitheguy.autoblocks.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import pitheguy.autoblocks.AllBlocks;
import pitheguy.autoblocks.blockentity.AbstractMinerBlockEntity;
import pitheguy.autoblocks.init.ModMenuTypes;

public class AutoLoggerMenu extends AbstractMinerMenu {
    public AutoLoggerMenu(int windowID, Inventory playerInv, AbstractMinerBlockEntity tile) {
        super(windowID, playerInv, tile, ModMenuTypes.AUTO_LOGGER.get());
    }

    public AutoLoggerMenu(int windowID, Inventory playerInv, FriendlyByteBuf data) {
        super(windowID, playerInv, data, ModMenuTypes.AUTO_LOGGER.get());
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, AllBlocks.AUTO_LOGGER.get());
    }
}
