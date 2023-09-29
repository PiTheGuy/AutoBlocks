package pitheguy.autoblocks.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import pitheguy.autoblocks.client.gui.SchematicCreatorScreen;

public class SchematicCreatorItem extends Item {
    BlockPos firstCorner = null;
    BlockPos secondCorner = null;

    public SchematicCreatorItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (Screen.hasShiftDown()) {
            firstCorner = null;
            secondCorner = null;
            return InteractionResult.SUCCESS;
        }
        if (context.getLevel().isClientSide) {
            if (firstCorner == null) {
                firstCorner = context.getClickedPos();
                context.getPlayer().displayClientMessage(Component.literal("First corner set."), true);
                return InteractionResult.SUCCESS;
            } else if (secondCorner == null) {
                secondCorner = context.getClickedPos();
                context.getPlayer().displayClientMessage(Component.literal("Second corner set."), true);
                return InteractionResult.SUCCESS;
            } else {
                Minecraft.getInstance().setScreen(new SchematicCreatorScreen(firstCorner, secondCorner));
            }
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (Screen.hasShiftDown()) {
            firstCorner = null;
            secondCorner = null;
            return InteractionResultHolder.success(stack);
        }
        if (firstCorner == null) {
            player.displayClientMessage(Component.literal("Click on a block to set the first corner."), true);
            return InteractionResultHolder.pass(stack);
        } else if (secondCorner == null) {
            player.displayClientMessage(Component.literal("Click on a block to set the second corner."), true);
            return InteractionResultHolder.pass(stack);
        } else if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new SchematicCreatorScreen(firstCorner, secondCorner));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
