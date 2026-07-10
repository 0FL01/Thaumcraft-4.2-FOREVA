package thaumcraft.common.container;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ContainerAlchemyFurnaceLayoutContractTest {

    @Test
    public void furnaceSlotsMatchTheTc4GuiTextureAnchors() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/container/ContainerAlchemyFurnace.java")),
                StandardCharsets.UTF_8);

        assertTrue(source.contains("new SlotLimitedHasAspects(furnace, 0, 80, 8)"));
        assertTrue(source.contains("new Slot(furnace, 1, 80, 48)"));
        assertTrue(source.contains("8 + col * 18, 84 + row * 18"));
        assertTrue(source.contains("8 + col * 18, 142"));
    }
}
