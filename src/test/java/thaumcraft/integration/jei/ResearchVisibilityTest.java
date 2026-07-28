package thaumcraft.integration.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import thaumcraft.client.integration.jei.ResearchVisibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ResearchVisibilityTest {
    @Test
    public void visibilityUsesExactRecipeAndUidAndOnlyAppliesChanges() {
        Object recipeA = new Object();
        Object recipeB = new Object();
        List<ResearchVisibility.Entry<Object>> entries = Arrays.asList(
                new ResearchVisibility.Entry<Object>(recipeA, "thaumcraft.arcane", "RESEARCH"),
                new ResearchVisibility.Entry<Object>(recipeB, "thaumcraft.infusion", "RESEARCH"));
        List<Call> calls = new ArrayList<Call>();
        ResearchVisibility<Object> visibility = new ResearchVisibility<Object>(entries,
                (recipe, uid, visible) -> calls.add(new Call(recipe, uid, visible)));

        visibility.initializeHidden();
        assertEquals(2, calls.size());
        assertSame(recipeA, calls.get(0).recipe);
        assertEquals("thaumcraft.arcane", calls.get(0).uid);

        Object player = new Object();
        Object world = new Object();
        visibility.update(player, world, Collections.singleton("RESEARCH"));
        assertEquals(4, calls.size());
        visibility.update(player, world, new HashSet<String>(Collections.singleton("RESEARCH")));
        assertEquals("Unchanged copied snapshots must not touch JEI", 4, calls.size());

        visibility.update(player, world, Collections.emptySet());
        assertEquals(6, calls.size());
        assertEquals(false, calls.get(4).visible);
        assertSame(recipeA, calls.get(4).recipe);
    }

    @Test
    public void nullAndWorldIdentityChangesFailClosed() {
        Object recipe = new Object();
        List<Call> calls = new ArrayList<Call>();
        ResearchVisibility<Object> visibility = new ResearchVisibility<Object>(Collections.singletonList(
                new ResearchVisibility.Entry<Object>(recipe, "thaumcraft.crucible", "R")),
                (value, uid, visible) -> calls.add(new Call(value, uid, visible)));
        Object player = new Object();
        Object firstWorld = new Object();

        visibility.initializeHidden();
        visibility.update(player, firstWorld, Collections.singleton("R"));
        visibility.update(player, new Object(), Collections.singleton("R"));
        visibility.update(null, null, null);

        assertEquals(5, calls.size());
        assertEquals(true, calls.get(1).visible);
        assertEquals(false, calls.get(2).visible);
        assertEquals(true, calls.get(3).visible);
        assertEquals(false, calls.get(4).visible);
    }

    private static final class Call {
        private final Object recipe;
        private final String uid;
        private final boolean visible;

        private Call(Object recipe, String uid, boolean visible) {
            this.recipe = recipe;
            this.uid = uid;
            this.visible = visible;
        }
    }
}
