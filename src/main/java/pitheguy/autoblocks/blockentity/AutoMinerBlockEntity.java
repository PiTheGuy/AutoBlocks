package pitheguy.autoblocks.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pitheguy.autoblocks.init.ModBlockEntityTypes;
import pitheguy.autoblocks.menu.AutoMinerMenu;

public class AutoMinerBlockEntity extends AbstractMinerBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AutoMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AUTO_MINER.get(), pos, state, 5, 2, ActionArea.BELOW);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.autoblocks.auto_miner");
    }

    @Override
    public boolean canRunAtPosition(BlockPos pos) {
        BlockState minedBlock = level.getBlockState(pos);
        if (!minedBlock.is(BlockTags.MINEABLE_WITH_PICKAXE) && !minedBlock.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            //LOGGER.debug("Block at {} skipped because it isn't the right tool", pos);
            return false;
        }
        return super.canRunAtPosition(pos);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoMinerMenu(containerId, playerInventory, this);
    }
}
