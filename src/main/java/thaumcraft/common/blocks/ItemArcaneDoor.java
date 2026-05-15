package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileOwned;

public class ItemArcaneDoor extends ItemDoor {

    public ItemArcaneDoor() {
        super(ConfigBlocks.blockArcaneDoor);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        BlockPos placePos = pos.up();
        Block block = ConfigBlocks.blockArcaneDoor;
        if (!player.canPlayerEdit(placePos, facing, stack) || !player.canPlayerEdit(placePos.up(), facing, stack)) {
            return EnumActionResult.FAIL;
        }
        if (!block.canPlaceBlockAt(worldIn, placePos)) {
            return EnumActionResult.FAIL;
        }

        EnumFacing dir = EnumFacing.fromAngle(player.rotationYaw);
        ItemDoor.placeDoor(worldIn, placePos, dir, block, false);

        if (worldIn.getTileEntity(placePos) instanceof TileOwned) {
            ((TileOwned) worldIn.getTileEntity(placePos)).owner = player.getName();
        }
        if (worldIn.getTileEntity(placePos.up()) instanceof TileOwned) {
            ((TileOwned) worldIn.getTileEntity(placePos.up())).owner = player.getName();
        }

        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
