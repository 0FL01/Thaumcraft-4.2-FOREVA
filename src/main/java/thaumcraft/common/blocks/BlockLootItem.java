package thaumcraft.common.blocks;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockLootItem extends ItemBlock {

    public BlockLootItem(BlockLoot block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case 1:
                return EnumRarity.UNCOMMON;
            case 2:
                return EnumRarity.RARE;
            default:
                return EnumRarity.COMMON;
        }
    }
}
