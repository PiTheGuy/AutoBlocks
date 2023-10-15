package pitheguy.autoblocks.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class EntityBasedAutoBlockEntity extends AutoBlockEntity {
    private final Class<? extends Entity> entityClass;

    public EntityBasedAutoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, int baseRange, int rangeIncreaseWithUpgrade, Class<? extends Entity> entityClass) {
        super(type, pos, state, inventorySize, baseRange, rangeIncreaseWithUpgrade);
        this.entityClass = entityClass;
    }

    protected List<? extends Entity> findNearbyEntities() {
        return level.getEntitiesOfClass(entityClass, this.getRenderBoundingBox().inflate(getRange()), this::isValidEntity);
    }

    protected abstract <E extends Entity> boolean isValidEntity(E entity);

    protected Entity getAffectedEntity() {
        List<? extends Entity> possibleEntities = findNearbyEntities();
        if (possibleEntities.isEmpty()) return null;
        return possibleEntities.get(level.random.nextInt(possibleEntities.size()));
    }
}
