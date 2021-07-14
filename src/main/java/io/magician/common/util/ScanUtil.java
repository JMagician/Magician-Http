package io.magician.common.util;

import io.magician.common.cache.MagicianCacheManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 扫描工具类
 */
public class ScanUtil {


    /**
     * 扫描指定的包
     * @param packageName
     * @return
     * @throws Exception
     */
    public static Set<String> loadClass(String packageName) throws Exception {
        if(packageName == null || packageName.equals("")){
            throw new NullPointerException("请配置要扫描的包名");
        }

        Set<String> packageSet = MagicianCacheManager.getScanClassList();
        if(packageSet != null && packageSet.size() > 0){
            return packageSet;
        }

        String[] packages = packageName.split(",");
        packageSet = new HashSet<>();
        for(String packageItem : packages){
            packageSet.add(packageItem);
        }

        return scanClassList(packageSet);
    }

    /**
     * 扫描框架的类
     * @param packageName 要扫描的包名
     * @return 扫描出来的包
     * @throws Exception 异常
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
