package thaumcraft.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public abstract class BlockCosmeticWoodSlab extends BlockSlab {

    public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class);
    public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");

    protected BlockCosmeticWoodSlab() {
        super(Material.WOOD);
        IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, Type.GREATWOOD);
        if (this.isDouble()) {
            state = state.withProperty(SEAMLESS, false);
        } else {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
            this.setCreativeTab(Thaumcraft.tabTC);
        }
        this.setDefaultState(state);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);
        this.setLightOpacity(0);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ConfigBlocks.blockSlabWood);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ConfigBlocks.blockSlabWood, 1, state.getValue(VARIANT).getMeta());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMeta();
    }

    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey() + "." + Type.byMeta(meta).getName();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Type.byMeta(stack.getMetadata() & 7);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isDouble()) {
            return;
        }
        for (Type type : Type.values()) {
            list.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(VARIANT, Type.byMeta(meta & 7));
        if (this.isDouble()) {
            return state.withProperty(SEAMLESS, (meta & 8) != 0);
        }
        return state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(VARIANT).getMeta();
        if (this.isDouble()) {
            return state.getValue(SEAMLESS) ? meta | 8 : meta;
        }
        return state.getValue(HALF) == EnumBlockHalf.TOP ? meta | 8 : meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        if (this.isDouble()) {
            return new BlockStateContainer(this, SEAMLESS, VARIANT);
        }
        return new BlockStateContainer(this, HALF, VARIANT);
    }

    public enum Type implements IStringSerializable {
        GREATWOOD(0, "greatwood"),
        SILVERWOOD(1, "silverwood");

        private static final Type[] META_LOOKUP = new Type[values().length];

        static {
            for (Type type : values()) {
                META_LOOKUP[type.meta] = type;
            }
        }

        private final int meta;
        private final String name;

        Type(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return this.meta;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static Type byMeta(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                return GREATWOOD;
            }
            return META_LOOKUP[meta];
        }
    }

    public static final class Half extends BlockCosmeticWoodSlab {
        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static final class Double extends BlockCosmeticWoodSlab {
        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
