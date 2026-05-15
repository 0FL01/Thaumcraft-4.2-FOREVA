package thaumcraft.common.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileOwned;

public class BlockArcaneDoor extends BlockDoor {

    public BlockArcaneDoor() {
        super(Material.IRON);
        this.setSoundType(SoundType.METAL);
        this.setResistance(999.0F);
        if (Config.wardedStone) {
            this.setBlockUnbreakable();
        } else {
            this.setHardness(15.0F);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileOwned();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        BlockPos base = state.getValue(HALF) == EnumDoorHalf.UPPER ? pos.down() : pos;
        TileEntity te = worldIn.getTileEntity(base);
        if (!(te instanceof TileOwned)) {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        }

        TileOwned owned = (TileOwned) te;
        String playerName = playerIn.getName();
        boolean allowed = playerName.equals(owned.owner)
                || owned.accessList.contains("0" + playerName)
                || owned.accessList.contains("1" + playerName);

        if (!allowed) {
            playerIn.sendStatusMessage(new TextComponentString("The door refuses to budge."), true);
            worldIn.playSound(null, pos, TCSounds.DOORFAIL, SoundCategory.BLOCKS, 0.66F, 1.0F);
            return true;
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        if (Config.wardedStone) {
            return Items.AIR;
        }
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : ConfigItems.itemArcaneDoor;
    }
}
