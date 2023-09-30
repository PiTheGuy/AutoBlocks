package pitheguy.autoblocks.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;
import pitheguy.autoblocks.blockentity.placer.PlacerLoadException;
import pitheguy.autoblocks.menu.AutoPlacerMenu;

public class LoadSchematicPacket extends SimplePacketBase {
    private final String schematicName;

    public LoadSchematicPacket(String schematicName) {
        this.schematicName = schematicName;
    }

    public LoadSchematicPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(schematicName);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof AutoPlacerMenu menu)) return;
            AutoPlacerBlockEntity placer = menu.tileEntity;
            try {
                placer.loadSchematic(schematicName);
            } catch (PlacerLoadException e) {
                player.closeContainer();
                player.displayClientMessage(Component.literal(e.getMessage()), true);
                return;
            }
            placer.setSchematicName(schematicName);
        });
        return true;
    }
}
