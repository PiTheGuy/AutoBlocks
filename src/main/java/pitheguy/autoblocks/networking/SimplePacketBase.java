package pitheguy.autoblocks.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class SimplePacketBase {

    public abstract void write(FriendlyByteBuf buffer);

    public abstract boolean handle(NetworkEvent.Context context);

}
