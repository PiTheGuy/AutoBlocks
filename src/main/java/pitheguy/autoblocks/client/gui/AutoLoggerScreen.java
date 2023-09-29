package pitheguy.autoblocks.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import pitheguy.autoblocks.menu.AutoLoggerMenu;

public class AutoLoggerScreen extends AbstractMinerScreen<AutoLoggerMenu> {
    public AutoLoggerScreen(AutoLoggerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
