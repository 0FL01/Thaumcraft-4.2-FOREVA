package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityCultistBehaviorContractTest {

    @Test
    public void cultistKeepsHomeNbtAndFactionTargetingContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityCultist.java");

        assertTrue("EntityCultist must restore home from HomeD/HomeX/HomeY/HomeZ NBT keys",
                source.contains("if (nbt.hasKey(\"HomeD\"))")
                        && source.contains("new BlockPos(nbt.getInteger(\"HomeX\"), nbt.getInteger(\"HomeY\"), nbt.getInteger(\"HomeZ\"))")
                        && source.contains("this.setHomePosAndDistance("));
        assertTrue("EntityCultist must persist home with HomeD/HomeX/HomeY/HomeZ keys",
                source.contains("nbt.setInteger(\"HomeD\"")
                        && source.contains("nbt.setInteger(\"HomeX\"")
                        && source.contains("nbt.setInteger(\"HomeY\"")
                        && source.contains("nbt.setInteger(\"HomeZ\""));
        assertTrue("EntityCultist must treat cultists and cultist leader as same team",
                source.contains("entityIn instanceof EntityCultist || entityIn instanceof EntityCultistLeader"));
        assertTrue("EntityCultist must not attack cultist subclasses",
                source.contains("if (cls == EntityCultistCleric.class || cls == EntityCultistLeader.class || cls == EntityCultistKnight.class)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
