package pitheguy.autoblocks.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import pitheguy.autoblocks.AutoBlocks;

import java.util.function.*;

public class AllPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AutoBlocks.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        new PacketType<>(StartPlacerPacket.class, buffer -> new StartPlacerPacket()).register();
        new PacketType<>(LoadSchematicPacket.class, LoadSchematicPacket::new).register();
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) context.setPacketHandled(true);
            };
            this.type = type;
        }

        private void register() {
            CHANNEL.registerMessage(index++, type, encoder, decoder, handler);
        }
    }

}
