package pitheguy.autoblocks.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;

public class GetRemainingMaterialsPacket extends SimplePacketBase {
    private final BlockPos placerPos;

    public GetRemainingMaterialsPacket(BlockPos placerPos) {
        this.placerPos = placerPos;
    }

    public GetRemainingMaterialsPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(placerPos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if (!level.hasChunkAt(placerPos)) return;
            if (!(level.getBlockEntity(placerPos) instanceof AutoPlacerBlockEntity placer)) return;
            AllPackets.CHANNEL.send(PacketDistributor.PLAYER.with(context::getSender), new RemainingMaterialsResponsePacket(placer.getRemainingMaterials()));
        });
        return true;
    }
}
