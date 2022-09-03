package io.magician.common.cache;

import java.util.Set;

/**
 * Cache management, used to save the scanned classes in memory for subsequent functions
 */
public class MagicianCacheManager {

    /**
     * Scanned class
     */
    private static Set<String> classList;

    public static Set<String> getScanClassList() {
        return classList;
    }

    public static void saveScanClassList(Set<String> classList) {
        MagicianCacheManager.classList = classList;
    }
}
