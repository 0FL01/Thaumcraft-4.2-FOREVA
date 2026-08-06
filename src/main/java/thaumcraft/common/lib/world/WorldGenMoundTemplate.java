package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class WorldGenMoundTemplate {

    static final int SIZE = 19;
    static final String[] LAYERS = {
        "........................................................................................................................CCCCCCC............CMCCMCC............CCCMMCC............CCCCCCC............CMCMCMC............CMCMMCC............CCCCCCC........................................................................................................................",
        "........................................................................................................................MMCMCCM............CAA.ACC............CAAAACC............MLAA.CM............MAAAACC............CAA.ACM............CCMMCCM........................................................................................................................",
        "........................................................................................................................CCCCCCC............CAAAACC............CAAAACC............CLAAACC............CAAAAMC............CAAAACC............CCCCCCC........................................................................................................................",
        "........................................................................................................................CCCCCCC............CAAAACC............MAAAACC............CLAAAMC............CAAAACC............CAAAACC............CCCMCCC........................................................................................................................",
        "............................................................CCCCCCCCCCCCC......CCCCCCCMCCCCC......CCCCCMCCCCCCC......CCCC3333CCCCC......CCC1AAAA0CCCC......CCC1AAAA0MMCC......CCCCLAAA0CCCC......CCM1AAAA0CCCC......CCC1AAAA0CCCC......CCCC2222CCCCC......CCMCCCCCCMCCC......CCCCCCCCCCCMC......CCCCCCCCCCCCC............................................................",
        "............................................................CCCCCCCCCCCCC......C.AAAAAAAAACC......CAAAAAAAAAACC......CAAMAAAACAACC......MAAAAAAAAAACC......MAAAAAAAAAA3C......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CAACAAAACAAAC......CAAAAAAAAAAAC......C.AAAAAAAAAAC......CCCMCCCCCCCCC............................................................",
        "............................................................CCMCCMCCMCCCC......CAAAAAAAAAACC......CAAAAAAAAAAMC......CAAMAAAAMAACC......CAAAAAAAAAA3C......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CAAAAAAAAAAAM......CAAAAAAAAAAAC......CAACAAAACAAAC......MAAAAAAAAAAAC......CAAAAAAAAAAAC......CCCCMCCMCCCMC............................................................",
        "............................................................CMCCCCCCCCCCC......CAAAAAAAAAACC......CAAAAAAAAAACC......CAACAAAACAA3C......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CAAAAAAAAAAAC......MAAAAAAAAAAAM......CAACAAAAMAAAC......CAAAAAAAAAAAC......CAAAAAAAAAAAC......CCCCCCCCCCCCC............................................................",
        "....DDDDDDDDDDD.......DDDDDDDDDDDDD.....DDDDDDDDDDDDDDD...DDCCCCCCCCCCCCCDD.DDDCMMCCCCMCMCCCDDDDDDCMCCCMCCCCC3CDDDDDDCCCC7777CCCACDDDDDCMCC5AAAA4MCACDDDCCCCCC5AAAA4MCACDDDCCMCMC5AAAA4CCACDDDCCCCMC5AAAA4CCCCDDDDDCCCC5AAAA4CMMCDDDDDDCCCM6666CCCMCDDDDDDCCMCCCMCCCCCCDDDDDDCCCCMCCCCMMCCDDD.DDCCCCCCCCCCCCCDD...DDDDDDDDDDDDDDD.....DDDDDDDDDDDDD.......DDDDDDDDDDD....",
        "....GGGGGDDGGGG.......GDDDDDDDDDDDG.....GDDDDDDDDDDDDDG...GDCCCCCCCCCCCCCDG.GDDCAAAAAAAAAAACDDGGDDCAAAAAAAAAAACDDGGDDCAAMAAAACAAACDDGGDDCAAAAAAAAAAAMDDD0CCCAAAAAAAAAAACDDDAAAIAAAAAAAAAAACDDG0CCCAAAAAAAAAAACDDGGDDCAAAAAAAAAAACDDGGDDMAACAAAAMAAACDDGGDDCAAAAAAAAAAACDDGGDDCAAAAAAAAAAACDDG.GDCCCCCCMCCCCCCDG...GDDDDDDDDDDDDDG.....GDDDDDDDDDDDD.......GGGGGGGGGGG....",
        ".........GG..T.........GGGDGDDDGGG.......GDDDDDDDDDDDD....TGCCCCCMMCCCMCCD..TGDMAAAAAAAAAAACDG.TGDMAAAAAAAAAAAMDGT.DDCAACAAAACAAACDG..DDMAAAAAAAAAAACDGGA0CCAAAAAAAAAAAMDGGAAAIAAAAAAAAAAACDG.A0MCAAAAAAAAAAAMDG..GDCAAAAAAAAAAAMDD..GDCAAMAAAAMAAACDGT.GDCAAAAAAAAAAACDGT.GDCAAAAAAAAAA.MDGT.TGCMCCCMMCMCCCCG.....GDDDDDDDDDDDD.......GGGDDGGDDDGG............TT........",
        "..........................G.GGG.TT........GDGGGDGGDDGG.....TGDCCMCCCMCCDDG...TGDCAAAAAAAAACDD...TGCAA6AAAA6AAAMD...GDCA4C5AA4C5AACG...GDCAA7AAA.7AAACGT..ACCAAAAAAAAAAACG...ABMAAAAAAAAAAACG...AMCAAAAAAAAAAAMG....DCAA6AAAA6AAACDG..TGCA4C5AA4C5AACD...TGCAA7AAAA7AAACG...TGDMAAAAAAAAACDG.....GDCMCCCCCCCGG.......GGGGGDDGGGGG.........TGG.TGGG........................",
        "...........................................G.T.GT.GG.T.......GGGGGGGGGGGGT.....GGCCCCCCCCCGGG.....GCCCCCCCCCCCGG....GGCCMCCCCCMCCGT....GGCCCCMMCCCCCGT....GGCCCCCCCCCCCGT....GGCCMMCCMCCCCD.....GGCCCCCCCCCCCDT....GGCCCCCCCCMCCGG.....GCMCCCCMCCCCGG.....GCCCCCCCCCCCG.....TGDCCCCCCCCCGG.......GDGGGGGGGGTT............GG...TT................T........................",
        "...........................................T...T.............TT...TTT...T.......TGGGGGGGGG.T.......GGGGGGDGGGG........GGGGDDDDGGG.......TGGGDDDGDDGGT......TGGGDDDDDDGG........GGGDDDDDGGGG.......GGGGDDDDDGGG.......GGGGGDDGGGG........GGGGGGGGGGGT.......GGGGGGGGGGGT.......GGGGGGGGGG.T........G......................................................................",
        ".....................................................................................T...................G............T...GGGG...........T..GGG.GG.............GGGGGG..........T..GGGGG...........T...GGGGG...........T...GG.TTT........TTTTT.TTTTT........TTT.TT.TTTT........TTTTT...T...........T......................................................................",
        "............................................................................................................................T...................................TTTT...............TTTT................TTTT.............................................................................................................................................................."
    };

    private WorldGenMoundTemplate() {
    }

    static void place(World world, BlockPos origin) {
        for (int y = 0; y < LAYERS.length; y++) {
            String layer = LAYERS[y];
            for (int z = 0; z < SIZE; z++) {
                for (int x = 0; x < SIZE; x++) {
                    IBlockState state = stateFor(layer.charAt(z * SIZE + x));
                    if (state != null) {
                        world.setBlockState(origin.add(x, y, z), state, 3);
                    }
                }
            }
        }
    }

    static IBlockState stateFor(char symbol) {
        switch (symbol) {
            case 'A': return Blocks.AIR.getDefaultState();
            case 'D': return Blocks.DIRT.getDefaultState();
            case 'C': return Blocks.COBBLESTONE.getDefaultState();
            case 'G': return Blocks.GRASS.getDefaultState();
            case 'M': return Blocks.MOSSY_COBBLESTONE.getDefaultState();
            case 'I': return Blocks.IRON_BARS.getDefaultState();
            case 'B': return Blocks.STONEBRICK.getStateFromMeta(3);
            case 'L': return Blocks.LADDER.getStateFromMeta(5);
            case 'T': return Blocks.TALLGRASS.getStateFromMeta(1);
            case '0': case '1': case '2': case '3':
            case '4': case '5': case '6': case '7':
                return Blocks.STONE_STAIRS.getStateFromMeta(symbol - '0');
            default: return null;
        }
    }
}
