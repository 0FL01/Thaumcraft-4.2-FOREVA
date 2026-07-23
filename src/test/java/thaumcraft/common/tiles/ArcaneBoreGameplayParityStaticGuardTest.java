package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcaneBoreGameplayParityStaticGuardTest {

    @Test
    public void serverLoopKeepsReferenceTargetDelayAndBreakStages() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");

        assertTrue(source.contains("private int count = 0;")
                && source.contains("private float radInc = 0.0F;")
                && source.contains("if (--this.count > 0) return;")
                && source.contains("this.currentRadius += this.radInc;")
                && source.contains("this.world.rayTraceBlocks(start,")
                && source.contains("this.count = this.getDigDelay(state, target);")
                && source.contains("this.toDig = true;")
                && source.contains("this.sendDigEvent(target);"));
        assertFalse(source.contains("private int scanIndex"));
        assertFalse(source.contains("private int digCooldown"));
    }

    @Test
    public void miningKeepsToolUpgradesRepairAndLampSupport() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");

        assertTrue(source.contains("Utils.findSpecialMiningResult(drop, 0.2F + (float) dropFortune * 0.075F")
                && source.contains("this.repairCounter++ % 40L == 0L")
                && source.contains("pickaxe.updateAnimation(this.world, this.fakePlayer, 0, true);")
                && source.contains("Config.enchRepair")
                && source.contains("instanceof IRepairableExtended")
                && source.contains("instanceof TileArcaneLamp")
                && source.contains("withProperty(BlockAiry.TYPE, 3)"));
    }

    @Test
    public void clientLoopUsesSelectedTargetAndDedicatedBoreBeams() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");

        assertTrue(source.contains("private int beamLength = 0;")
                && source.contains("this.updateAimTarget(target);")
                && source.contains("this.updateAimEasing();")
                && source.contains("Thaumcraft.proxy.beamBore(")
                && source.contains("TCSounds.RUMBLE")
                && source.contains("this.world.addBlockEvent(this.pos, ConfigBlocks.blockWoodenDevice, 99,")
                && source.contains("public boolean receiveClientEvent(int id, int type)"));
    }

    @Test
    public void borePlacementRequiresVerticalArcaneBoreBaseSupport() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockWoodenDeviceItem.java");

        assertTrue(source.contains("public boolean canPlaceBlockOnSide(")
                && source.contains("side != EnumFacing.UP && side != EnumFacing.DOWN")
                && source.contains("support.getBlock() != ConfigBlocks.blockWoodenDevice")
                && source.contains("support.getValue(BlockWoodenDevice.TYPE) != 4")
                && source.contains("EnumFacing.getDirectionFromEntityLiving(pos, player)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
