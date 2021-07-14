package io.magician.common.cache;

import java.util.Set;

public class MagicianCacheManager {

    /**
     * 扫描出来的类
     */
    private static Set<String> classList;

    public static Set<String> getScanClassList() {
        return classList;
    }

    public static void saveScanClassList(Set<String> classList) {
        MagicianCacheManager.classList = classList;
    }
}
