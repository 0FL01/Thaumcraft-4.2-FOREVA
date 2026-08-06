package thaumcraft.common.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.container.ContainerMagicBox;
import thaumcraft.common.tiles.TileMagicBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MagicBoxVerticalSliceParityTest {

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
    }

    @Test
    public void blockKeepsTc4PropertiesAndHiddenCreativeSurface() {
        BlockMagicBox block = new BlockMagicBox();
        assertEquals(2.5F, block.getBlockHardness(block.getDefaultState(), null, BlockPos.ORIGIN), 0.0F);
        assertFalse(block.isOpaqueCube(block.getDefaultState()));
        assertTrue(block.isFullCube(block.getDefaultState()));
        assertEquals(EnumBlockRenderType.MODEL, block.getRenderType(block.getDefaultState()));
        TileEntity tile = block.createNewTileEntity(null, 0);
        assertTrue(tile instanceof TileMagicBox);

        NonNullList<ItemStack> subBlocks = NonNullList.create();
        block.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
        assertTrue(subBlocks.isEmpty());
    }

    @Test
    public void inventoryHasTwentySevenPersistentSlots() {
        TileMagicBox box = new TileMagicBox();
        ItemStack stack = new ItemStack(Items.DIAMOND, 80);
        box.setInventorySlotContents(26, stack);
        assertEquals(64, box.getStackInSlot(26).getCount());
        assertEquals(27, box.getSizeInventory());
        assertEquals(27, new ContainerMagicBox(null, box).inventorySlots.size());

        NBTTagCompound saved = new NBTTagCompound();
        box.writeCustomNBT(saved);
        TileMagicBox loaded = new TileMagicBox();
        loaded.readCustomNBT(saved);
        assertEquals(Items.DIAMOND, loaded.getStackInSlot(26).getItem());
        assertEquals(64, loaded.getStackInSlot(26).getCount());
        assertEquals(5, loaded.decrStackSize(26, 5).getCount());
        assertEquals(59, loaded.getStackInSlot(26).getCount());
    }

    @Test
    public void registryGuiDropAndModelSurfacesAreComplete() throws IOException {
        String config = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String block = read("src/main/java/thaumcraft/common/blocks/BlockMagicBox.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String blockModel = read("src/main/resources/assets/thaumcraft/models/block/blockmagicbox.json");

        assertTrue(config.contains("legacyPath(\"blockMagicBox\")")
                && config.contains("new TileRegistration(TileMagicBox.class, \"TileMagicBox\")")
                && config.contains("new net.minecraft.item.ItemBlock(blockMagicBox)"));
        assertTrue(block.contains("InventoryHelper.dropInventoryItems")
                && block.contains("CommonProxy.GUI_MAGIC_BOX"));
        assertTrue(commonProxy.contains("case GUI_MAGIC_BOX:"));
        assertTrue(clientProxy.contains("new GuiMagicBox(player.inventory, tile)"));
        assertTrue(clientProxy.contains("registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockMagicBox), 0, \"blockmagicbox\")"));
        assertTrue(blockModel.contains("\"all\": \"thaumcraft:blocks/woodplain\""));
        assertTrue(Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockmagicbox.json")));
        assertTrue(Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/item/blockmagicbox.json")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
