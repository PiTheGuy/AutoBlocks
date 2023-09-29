package pitheguy.autoblocks.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import pitheguy.autoblocks.blockentity.placer.AutoPlacerBlockEntity;
import pitheguy.autoblocks.init.ModBlocKEntityTypes;

public class AutoPlacerBlock extends BaseEntityBlock {

    public AutoPlacerBlock() {
        super(Properties.of()
                .strength(6.5F, 8)
                .sound(SoundType.METAL));
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoPlacerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(blockEntityType, ModBlocKEntityTypes.AUTO_PLACER.get(), ((level1, pos, state1, blockEntity) -> blockEntity.tick()));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level != null && !level.isClientSide()) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof AutoPlacerBlockEntity logger) {
                NetworkHooks.openScreen((ServerPlayer) player, logger, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof AutoPlacerBlockEntity placer) placer.discard(level, pos);
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) level.removeBlockEntity(pos);
    }
}
