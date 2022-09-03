package io.magician.common.util;

import io.magician.common.cache.MagicianCacheManager;

import java.util.HashSet;
import java.util.Set;

/**
 * scanning tools
 */
public class ScanUtil {


    /**
     * scan the specified package
     * @param packageName
     * @return
     * @throws Exception
     */
    public static Set<String> loadClass(String packageName) throws Exception {
        if(packageName == null || packageName.equals("")){
            throw new NullPointerException("Please configure the package name to scan");
        }

        Set<String> packageSet = MagicianCacheManager.getScanClassList();
        if(packageSet != null && packageSet.size() > 0){
            return packageSet;
        }

        String[] packages = packageName.split(",");
        packageSet = new HashSet<>();
        for(String packageItem : packages){
            packageSet.add(packageItem.trim());
        }

        return scanClassList(packageSet);
    }

    /**
     * Scan all classes in the specified package name
     * @param packageName
     * @return
     * @throws Exception
     */
    private static Set<String> scanClassList(Set<String> packageName) throws Exception {
        Set<String> scanClassList = new HashSet<>();
        for(String pkName : packageName){
            Set<String> classList = ReadClassUtil.loadClassList(pkName);
            scanClassList.addAll(classList);
        }
        MagicianCacheManager.saveScanClassList(scanClassList);
        return scanClassList;
    }
}
