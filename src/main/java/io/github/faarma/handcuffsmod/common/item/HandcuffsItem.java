package io.github.faarma.handcuffsmod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class HandcuffsItem extends Item {
    public HandcuffsItem() {
        super(new Item.Properties().stacksTo(4).tab(ItemGroup.TAB_TOOLS));
    }
}
