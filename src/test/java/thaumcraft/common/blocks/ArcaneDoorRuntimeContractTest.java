package thaumcraft.common.blocks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcaneDoorRuntimeContractTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void metadataKeepsVanillaCompatibleHalfBitAndTc4DoorPayload() {
        BlockArcaneDoor door = new BlockArcaneDoor();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            for (boolean open : new boolean[]{false, true}) {
                int meta = door.getMetaFromState(door.getDefaultState()
                        .withProperty(BlockArcaneDoor.FACING, facing)
                        .withProperty(BlockArcaneDoor.OPEN, open));
                assertEquals(0, meta & 8);
                assertEquals(facing, door.getStateFromMeta(meta).getValue(BlockArcaneDoor.FACING));
                assertEquals(open, door.getStateFromMeta(meta).getValue(BlockArcaneDoor.OPEN));
            }
        }

        assertEquals(8, door.getMetaFromState(door.getDefaultState()
                .withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockArcaneDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)));
        assertEquals(9, door.getMetaFromState(door.getDefaultState()
                .withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockArcaneDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)));
        assertEquals(BlockDoor.EnumDoorHalf.UPPER,
                door.getStateFromMeta(10).getValue(BlockArcaneDoor.HALF));
        assertEquals(BlockDoor.EnumHingePosition.LEFT,
                door.getStateFromMeta(10).getValue(BlockArcaneDoor.HINGE));
    }

    @Test
    public void blockAndItemDoNotInheritVanillaDoorIdentityFallbacks() {
        assertFalse(BlockDoor.class.isAssignableFrom(BlockArcaneDoor.class));
        assertFalse(ItemDoor.class.isAssignableFrom(ItemArcaneDoor.class));
        BlockArcaneDoor door = new BlockArcaneDoor();
        assertEquals(EnumBlockRenderType.MODEL, door.getRenderType(door.getDefaultState()));
    }

    @Test
    public void blockstateMatchesAllVanillaDoorGeometryRoutesWithoutPoweredStubs() throws IOException {
        JsonObject variants = new JsonParser().parse(read(
                "src/main/resources/assets/thaumcraft/blockstates/blockarcanedoor.json"))
                .getAsJsonObject().getAsJsonObject("variants");
        assertEquals(32, variants.entrySet().size());

        Map<String, Integer> closedRotation = rotations(0, 90, 180, 270);
        Map<String, Integer> openLeftRotation = rotations(90, 180, 270, 0);
        Map<String, Integer> openRightRotation = rotations(270, 0, 90, 180);
        for (String facing : closedRotation.keySet()) {
            for (String half : new String[]{"lower", "upper"}) {
                for (String hinge : new String[]{"left", "right"}) {
                    for (boolean open : new boolean[]{false, true}) {
                        String key = "facing=" + facing + ",half=" + half + ",hinge=" + hinge + ",open=" + open;
                        assertTrue("Missing Arcane Door variant " + key, variants.has(key));
                        assertFalse(key.contains("powered"));

                        JsonObject variant = variants.getAsJsonObject(key);
                        boolean rightModel = open ? hinge.equals("left") : hinge.equals("right");
                        String model = "thaumcraft:blockarcanedoor_" + (half.equals("lower") ? "bottom" : "top")
                                + (rightModel ? "_rh" : "");
                        assertEquals(model, variant.get("model").getAsString());
                        int expectedRotation = open
                                ? (hinge.equals("left") ? openLeftRotation.get(facing) : openRightRotation.get(facing))
                                : closedRotation.get(facing);
                        assertEquals(expectedRotation, variant.has("y") ? variant.get("y").getAsInt() : 0);
                    }
                }
            }
        }
    }

    @Test
    public void sourceKeepsOwnershipPressurePlateProtectionAndExplicitItemContracts() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockArcaneDoor.java");
        String item = read("src/main/java/thaumcraft/common/blocks/ItemArcaneDoor.java");

        assertTrue(block.contains("new ItemStack(ConfigItems.itemArcaneDoor)")
                && block.contains("public Item getItemDropped")
                && block.contains("public ItemStack getPickBlock"));
        assertTrue(block.contains("blockIn == ConfigBlocks.blockWoodenDevice")
                && block.contains("plateState.getValue(BlockWoodenDevice.TYPE)")
                && block.contains("this.setDoorOpen(world, pos, true)"));
        assertTrue(block.contains("public boolean canEntityDestroy")
                && block.contains("public void onBlockExploded")
                && block.contains("public boolean canHarvestBlock"));
        assertTrue(item.contains("world.setBlockState(pos, lower, 2)")
                && item.contains("world.setBlockState(pos.up(), upper, 2)")
                && item.contains("setOwner(world, pos, owner)")
                && item.contains("setOwner(world, pos.up(), owner)"));
    }

    private static Map<String, Integer> rotations(int east, int south, int west, int north) {
        Map<String, Integer> rotations = new LinkedHashMap<>();
        rotations.put("east", east);
        rotations.put("south", south);
        rotations.put("west", west);
        rotations.put("north", north);
        return rotations;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
