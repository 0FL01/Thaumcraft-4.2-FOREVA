package thaumcraft.common;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ProxyCoreGuiRoutingStaticGuardTest {

    @Test
    public void commonProxyShouldKeepCoreServerEntityAndTileRoutes() throws IOException {
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep golem server container route",
                commonProxySource.contains("case GUI_GOLEM:")
                        && commonProxySource.contains("entity instanceof EntityGolemBase ? new ContainerGolem(player.inventory, (EntityGolemBase) entity) : null;"));
        assertTrue("CommonProxy should keep pech server container route",
                commonProxySource.contains("case GUI_PECH:")
                        && commonProxySource.contains("entity instanceof EntityPech ? new ContainerPech(player.inventory, world, (EntityPech) entity) : null;"));
        assertTrue("CommonProxy should keep traveling trunk server container route",
                commonProxySource.contains("case GUI_TRAVELING_TRUNK:")
                        && commonProxySource.contains("entity instanceof EntityTravelingTrunk ? new ContainerTravelingTrunk(player.inventory, world, (EntityTravelingTrunk) entity) : null;"));
        assertTrue("CommonProxy should keep thaumatorium server container route",
                commonProxySource.contains("case GUI_THAUMATORIUM:")
                        && commonProxySource.contains("tile instanceof TileThaumatorium ? new ContainerThaumatorium(player.inventory, (TileThaumatorium) tile) : null;"));
        assertTrue("CommonProxy should keep deconstruction table server container route",
                commonProxySource.contains("case GUI_DECONSTRUCTION_TABLE:")
                        && commonProxySource.contains("tile instanceof TileDeconstructionTable ? new ContainerDeconstructionTable(player.inventory, (TileDeconstructionTable) tile) : null;"));
        assertTrue("CommonProxy should keep alchemy furnace server container route",
                commonProxySource.contains("case GUI_ALCHEMY_FURNACE:")
                        && commonProxySource.contains("tile instanceof TileAlchemyFurnace ? new ContainerAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile) : null;"));
        assertTrue("CommonProxy should keep research table server container route",
                commonProxySource.contains("case GUI_RESEARCH_TABLE:")
                        && commonProxySource.contains("tile instanceof TileResearchTable ? new ContainerResearchTable(player.inventory, (TileResearchTable) tile) : null;"));
        assertTrue("CommonProxy should keep arcane bore server container route",
                commonProxySource.contains("case GUI_ARCANE_BORE:")
                        && commonProxySource.contains("tile instanceof TileArcaneBore ? new ContainerArcaneBore(player.inventory, (TileArcaneBore) tile) : null;"));
    }

    @Test
    public void clientProxyShouldKeepCoreClientEntityAndTileRoutes() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("ClientProxy should keep golem client GUI route",
                clientProxySource.contains("case GUI_GOLEM:")
                        && clientProxySource.contains("new GuiGolem(player, (EntityGolemBase) entity)"));
        assertTrue("ClientProxy should keep pech client GUI route",
                clientProxySource.contains("case GUI_PECH:")
                        && clientProxySource.contains("new GuiPech(player, world, (EntityPech) entity)"));
        assertTrue("ClientProxy should keep traveling trunk client GUI route",
                clientProxySource.contains("case GUI_TRAVELING_TRUNK:")
                        && clientProxySource.contains("new GuiTravelingTrunk(player, (EntityTravelingTrunk) entity)"));
        assertTrue("ClientProxy should keep thaumatorium client GUI route",
                clientProxySource.contains("case GUI_THAUMATORIUM:")
                        && clientProxySource.contains("new GuiThaumatorium(player.inventory, (TileThaumatorium) tile)"));
        assertTrue("ClientProxy should keep deconstruction table client GUI route",
                clientProxySource.contains("case GUI_DECONSTRUCTION_TABLE:")
                        && clientProxySource.contains("new GuiDeconstructionTable(player.inventory, (TileDeconstructionTable) tile)"));
        assertTrue("ClientProxy should keep alchemy furnace client GUI route",
                clientProxySource.contains("case GUI_ALCHEMY_FURNACE:")
                        && clientProxySource.contains("new GuiAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile)"));
        assertTrue("ClientProxy should keep research table client GUI route",
                clientProxySource.contains("case GUI_RESEARCH_TABLE:")
                        && clientProxySource.contains("new GuiResearchTable(player, (TileResearchTable) tile)"));
        assertTrue("ClientProxy should keep arcane bore client GUI route",
                clientProxySource.contains("case GUI_ARCANE_BORE:")
                        && clientProxySource.contains("new GuiArcaneBore(player.inventory, (TileArcaneBore) tile)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
