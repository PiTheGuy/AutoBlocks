package pitheguy.autoblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AllTags {
    public static final TagKey<Item> UPGRADES = create("upgrades");

    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(AutoBlocks.MODID, pName));
    }
}
