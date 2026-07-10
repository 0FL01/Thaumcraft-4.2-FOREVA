package thaumcraft.common.tiles;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileMagicWorkbenchAuthorityStaticGuardTest {
    @Test
    public void workbenchShouldKeepPerViewerResultsAndModernInventoryContracts() throws Exception {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileMagicWorkbench.java");
        String container = read("src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java");
        String block = read("src/main/java/thaumcraft/common/blocks/BlockTable.java");

        assertTrue(tile.contains("private final Set<Container> eventHandlers")
                && tile.contains("addWorkbenchListener(Container listener)")
                && tile.contains("removeWorkbenchListener(Container listener)")
                && tile.contains("listener.onCraftMatrixChanged(this)"));
        assertTrue(tile.contains("var5 == 9")
                && tile.contains("if (var3 == 9) continue;")
                && container.contains("private final InventoryCraftResult craftResult")
                && container.contains("this.tileEntity.setInventorySlotContentsSoftly(9, ItemStack.EMPTY)"));
        assertTrue(tile.contains("!((ItemWandCasting) stack.getItem()).isStaff(stack)")
                && tile.contains("CapabilityItemHandler.ITEM_HANDLER_CAPABILITY")
                && tile.contains("new SidedInvWrapper(this, side)"));
        assertTrue(tile.contains("this.world.notifyBlockUpdate(this.pos, state, state, 3)")
                && block.contains("InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());")
                && block.contains("wandstack.setCount(0);"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
