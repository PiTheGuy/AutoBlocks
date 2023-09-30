package pitheguy.autoblocks.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;
import pitheguy.autoblocks.menu.AutoPlacerMenu;

public class StartPlacerPacket extends SimplePacketBase {

    public StartPlacerPacket() {
    }

    @Override
    public void write(FriendlyByteBuf buffer) {

    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof AutoPlacerMenu menu)) return;
            AutoPlacerBlockEntity placer = menu.tileEntity;
            placer.start();
        });
        return true;
    }
}
