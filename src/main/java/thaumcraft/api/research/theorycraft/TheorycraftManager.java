package thaumcraft.api.research.theorycraft;

import java.util.HashMap;

public class TheorycraftManager {
    public static final HashMap<String, ITheorycraftAid> aids = new HashMap<>();
    public static final HashMap<String, Class<TheorycraftCard>> cards = new HashMap<>();

    public static void registerAid(ITheorycraftAid aid) {
        aids.putIfAbsent(aid.getClass().getName(), aid);
    }

    @SuppressWarnings("unchecked")
    public static void registerCard(Class card) {
        cards.putIfAbsent(card.getName(), (Class<TheorycraftCard>) card);
    }
}
