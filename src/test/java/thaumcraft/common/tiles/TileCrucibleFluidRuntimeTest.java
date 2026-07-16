package thaumcraft.common.tiles;

import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileCrucibleFluidRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void waterAndAspectsRemainSeparateAcrossUpdatePackets() {
        TestCrucible source = new TestCrucible();
        NBTTagCompound initial = new NBTTagCompound();
        new FluidStack(FluidRegistry.WATER, 500).writeToNBT(initial);
        source.readCustomNBT(initial);
        source.aspects.add(Aspect.WATER, 50);

        assertTrue(source.hasWater());
        assertEquals(0.775F, source.getFluidHeight(), 0.0001F);

        TestCrucible clientCopy = new TestCrucible();
        clientCopy.readCustomNBT(source.getUpdatePacket().getNbtCompound());
        assertTrue(clientCopy.hasWater());
        assertEquals(50, clientCopy.aspects.getAmount(Aspect.WATER));

        NBTTagCompound empty = new NBTTagCompound();
        empty.setString("Empty", "");
        source.aspects.writeToNBT(empty);
        source.readCustomNBT(empty);
        assertFalse(source.hasWater());
        assertEquals(0.65F, source.getFluidHeight(), 0.0001F);

        NBTTagCompound emptyPacket = source.getUpdatePacket().getNbtCompound();
        clientCopy.readCustomNBT(emptyPacket);
        assertFalse("an empty packet must clear previously synchronized water", clientCopy.hasWater());
        assertEquals("residual aspects remain independently stored like TC4",
                50, clientCopy.aspects.getAmount(Aspect.WATER));
    }

    private static class TestCrucible extends TileCrucible {
        @Override
        public void markDirty() {
        }
    }
}
