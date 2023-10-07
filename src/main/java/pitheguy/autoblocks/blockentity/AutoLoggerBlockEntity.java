package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoLoggerMenu;

public class AutoLoggerBlockEntity extends AbstractMinerBlockEntity {
    public AutoLoggerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_LOGGER.get(), pos, state, 15, 5, ActionArea.BELOW);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_logger");
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        BlockState minedBlock = level.getBlockState(pos);
        return super.canRunAtPosition(pos) && (minedBlock.is(BlockTags.LOGS) || minedBlock.is(BlockTags.LEAVES));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoLoggerMenu(containerId, playerInventory, this);
    }
}
