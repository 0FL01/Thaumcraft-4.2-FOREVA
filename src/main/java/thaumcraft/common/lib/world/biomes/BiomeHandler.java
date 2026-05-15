package thaumcraft.common.lib.world.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.api.aspects.Aspect;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeHandler {

    public static HashMap<BiomeDictionary.Type, List> biomeInfo = new HashMap<>();

    public static void registerBiomeInfo(BiomeDictionary.Type type, int auraLevel, Aspect tag,
                                          boolean greatwood, float greatwoodchance) {
        java.util.ArrayList info = new java.util.ArrayList();
        info.add(auraLevel);
        info.add(tag);
        info.add(greatwood);
        info.add(greatwoodchance);
        biomeInfo.put(type, info);
    }

    public static int getBiomeAura(Biome biome) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
        if (types.isEmpty()) return 100;
        int total = 0;
        int count = 0;
        for (BiomeDictionary.Type type : types) {
            List info = biomeInfo.get(type);
            if (info != null) {
                total += (int) info.get(0);
                count++;
            }
        }
        return count > 0 ? total / count : 100;
    }

    public static Aspect getRandomBiomeTag(Biome biome, Random random) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
        if (types.isEmpty()) return null;
        BiomeDictionary.Type[] typeArr = types.toArray(new BiomeDictionary.Type[0]);
        BiomeDictionary.Type type = typeArr[random.nextInt(typeArr.length)];
        List info = biomeInfo.get(type);
        if (info != null) {
            return (Aspect) info.get(1);
        }
        return null;
    }

    public static float getBiomeSupportsGreatwood(Biome biome) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
        for (BiomeDictionary.Type type : types) {
            List info = biomeInfo.get(type);
            if (info != null && Boolean.TRUE.equals(info.get(2))) {
                return (float) info.get(3);
            }
        }
        return 0.0f;
    }
}
