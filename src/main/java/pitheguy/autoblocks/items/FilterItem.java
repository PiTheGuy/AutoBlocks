package pitheguy.autoblocks.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilterItem extends Item {

    public FilterItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
        ItemStack filterStack = context.getItemInHand();
        if (Screen.hasShiftDown()) removeFilterBlock(filterStack);
        else setFilterBlock(filterStack, clickedBlock);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack filterStack = player.getItemInHand(usedHand);
        if (Screen.hasShiftDown()) {
            removeFilterBlock(filterStack);
            return InteractionResultHolder.success(filterStack);
        }
        return InteractionResultHolder.pass(filterStack);
    }

    public static void setFilterBlock(ItemStack stack, Block filterBlock) {
        ResourceLocation blockKey = ForgeRegistries.BLOCKS.getKey(filterBlock);
        if (blockKey != null) stack.getOrCreateTag().putString("Filter", blockKey.toString());
    }

    public static void removeFilterBlock(ItemStack stack) {
        stack.getOrCreateTag().remove("Filter");
    }

    public static boolean hasFilterBlock(ItemStack stack) {
        return stack.getOrCreateTag().contains("Filter", Tag.TAG_STRING);
    }

    public static Block getFilterBlock(ItemStack stack) {
        if (hasFilterBlock(stack)) {
            String blockId = stack.getOrCreateTag().getString("Filter");
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
        } else return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (hasFilterBlock(stack))
            tooltip.add(Component.translatable(getFilterBlock(stack).getDescriptionId()).withStyle(ChatFormatting.GRAY));
    }
}
