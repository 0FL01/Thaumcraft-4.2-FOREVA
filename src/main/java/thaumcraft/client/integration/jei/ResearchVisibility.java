package thaumcraft.client.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/** Small state machine that keeps JEI's identity-based hidden recipe set in sync. */
public final class ResearchVisibility<T> {
    public interface Sink<T> {
        void setVisible(T recipe, String categoryUid, boolean visible);
    }

    public static final class Entry<T> {
        public final T recipe;
        public final String categoryUid;
        public final String research;

        public Entry(T recipe, String categoryUid, String research) {
            this.recipe = recipe;
            this.categoryUid = categoryUid;
            this.research = research;
        }
    }

    private final List<Entry<T>> entries;
    private final Sink<T> sink;
    private final Set<Entry<T>> visible = Collections.newSetFromMap(new IdentityHashMap<Entry<T>, Boolean>());
    private Object player;
    private Object world;
    private Set<String> research = Collections.emptySet();

    public ResearchVisibility(List<Entry<T>> entries, Sink<T> sink) {
        this.entries = new ArrayList<Entry<T>>(entries);
        this.sink = sink;
    }

    public void initializeHidden() {
        this.visible.clear();
        for (Entry<T> entry : this.entries) {
            this.sink.setVisible(entry.recipe, entry.categoryUid, false);
        }
        this.player = null;
        this.world = null;
        this.research = Collections.emptySet();
    }

    public void update(Object currentPlayer, Object currentWorld, Set<String> completedResearch) {
        if (currentPlayer == null || currentWorld == null || completedResearch == null) {
            this.hideVisible();
            this.player = null;
            this.world = null;
            this.research = Collections.emptySet();
            return;
        }

        boolean identityChanged = currentPlayer != this.player || currentWorld != this.world;
        Set<String> snapshot = new HashSet<String>(completedResearch);
        if (!identityChanged && snapshot.equals(this.research)) {
            return;
        }
        if (identityChanged) {
            this.hideVisible();
            this.player = currentPlayer;
            this.world = currentWorld;
        }

        for (Entry<T> entry : this.entries) {
            boolean shouldBeVisible = snapshot.contains(entry.research);
            boolean isVisible = this.visible.contains(entry);
            if (shouldBeVisible == isVisible) {
                continue;
            }
            this.sink.setVisible(entry.recipe, entry.categoryUid, shouldBeVisible);
            if (shouldBeVisible) {
                this.visible.add(entry);
            } else {
                this.visible.remove(entry);
            }
        }
        this.research = snapshot;
    }

    private void hideVisible() {
        for (Entry<T> entry : new ArrayList<Entry<T>>(this.visible)) {
            this.sink.setVisible(entry.recipe, entry.categoryUid, false);
        }
        this.visible.clear();
    }
}
