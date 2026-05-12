package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockCustomOre extends Block {

    public static final String[] oreTypes = {"cinnabar", "infusedAir", "infusedFire", "infusedWater", "infusedEarth", "infusedOrder", "infusedEntropy", "amber"};

    public BlockCustomOre() {
        super(Material.ROCK);
        this.setHardness(3.0f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i <= 7; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        int meta = this.getMetaFromState(state);
        if (meta >= 1 && meta <= 6) {
            return 1 + random.nextInt(1 + fortune);
        }
        return 1;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 0) {
            return Item.getItemFromBlock(this);
        }
        return Items.AIR;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        int meta = this.getMetaFromState(state);
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        if (meta == 0) {
            drops.add(new ItemStack(this, 1, 0));
        } else if (meta == 7) {
            drops.add(new ItemStack(ConfigItems.itemResource, 1 + rand.nextInt(1 + fortune), 6));
        } else if (meta >= 1 && meta <= 6) {
            drops.add(new ItemStack(ConfigItems.itemShard, 1 + rand.nextInt(1 + fortune), meta - 1));
        } else {
            drops.add(new ItemStack(this, 1, meta));
        }
        return drops;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 0) return 2;
        if (meta == 7) return 3;
        if (meta >= 1 && meta <= 6) return 4;
        return 0;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }
}
