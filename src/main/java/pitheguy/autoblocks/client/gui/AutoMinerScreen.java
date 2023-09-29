package pitheguy.autoblocks.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import pitheguy.autoblocks.menu.AutoMinerMenu;

public class AutoMinerScreen extends AbstractMinerScreen<AutoMinerMenu>{
    public AutoMinerScreen(AutoMinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
