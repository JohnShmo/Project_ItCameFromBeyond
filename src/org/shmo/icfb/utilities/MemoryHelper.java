package org.shmo.icfb.utilities;

import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class MemoryHelper {
    public static <T> T get(@NotNull MemoryAPI memory, @NotNull String key) {
        try {
            return (T)memory.get(key);
        } catch(ClassCastException ignored) {
            return null;
        }
    }

    public static <T> T set(@NotNull MemoryAPI memory, @NotNull String key, T object) {
        if (object == null) {
            if (memory.contains(key))
                memory.unset(key);
            return null;
        }
        memory.set(key, object);
        return object;
    }
}
