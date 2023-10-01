package pitheguy.autoblocks.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import pitheguy.autoblocks.client.gui.MaterialsScreen;
import pitheguy.autoblocks.util.BufferUtil;

import java.util.Map;

public class RemainingMaterialsResponsePacket extends SimplePacketBase {
    private final Map<Item, Integer> remainingMaterials;

    public RemainingMaterialsResponsePacket(Map<Item, Integer> remainingMaterials) {
        this.remainingMaterials = remainingMaterials;
    }

    public RemainingMaterialsResponsePacket(FriendlyByteBuf buffer) {
        this(BufferUtil.readMap(buffer, () -> buffer.readById(BuiltInRegistries.ITEM), buffer::readVarInt));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        BufferUtil.writeMap(buffer, remainingMaterials, item -> buffer.writeId(BuiltInRegistries.ITEM, item), buffer::writeVarInt);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (!(Minecraft.getInstance().screen instanceof MaterialsScreen screen)) return;
            screen.setMaterials(remainingMaterials);
        });
        return true;
    }
}
