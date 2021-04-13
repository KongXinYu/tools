package com.wzy.tools.file;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * 操作文件相关工具类
 * @author: WuZY
 * @time: 2021/4/12 0012
 */
public class FileUtils {

    /**
     * 递归获取目录下所有文件路径
     * @param path
     * @return
     */
    public static List<String> findAll(String path) {
        if (!checkPath(path)) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        File file = new File(path);
        if (file.isFile()) {
            paths.add(path);
        } else {
            File[] subFiles = file.listFiles();
            if (null != subFiles || subFiles.length == 0 ) {
                for (File subFile : subFiles) {
                    if (subFile.isFile()) {
                        paths.add(subFile.getPath());
                    } else {
                        List<String> subPaths = findAll(subFile.getPath());
                        if (subPaths == null || subPaths.isEmpty()) {
                            continue;
                        }
                        paths.addAll(subPaths);
                    }
                }
            }
        }
        return paths;
    }

    /**
     * 普通模式下获取目录下匹配到的文件
     * @param path 文件路劲
     * @param express 表达式
     * @return
     */
    public static List<String> matchFilesByName(String path, String express){
        return matchFiles(path, express, false);
    }

    /**
     * 正则模式下获取目录下匹配到的文件
     * @param path 文件路劲
     * @param express 表达式
     * @return
     */
    public static List<String> matchFilesUseRegular(String path, String express){
        return matchFiles(path, express, true);
    }

    /**
     * 获取目录下匹配到的文件
     * @param path 文件路劲
     * @param express 表达式
     * @param isRegular 是否支持正则
     * @return
     */
    public static List<String> matchFiles(String path, String express, boolean isRegular) {
        if (!checkPath(path)) {
            return null;
        }
        List<String> paths = new ArrayList<String>();
        File file = new File(path);

        if (file.isFile()) {
            paths.add(path);
        } else {
            File[] subFiles = file.listFiles();
            if (null != subFiles || subFiles.length == 0 ) {
                for (File subFile : subFiles) {
                    if (subFile.isFile()) {
                        if (doMatch(subFile, express, isRegular)) {
                            paths.add(subFile.getPath());
                        }
                    } else {
                        List<String> subPaths = matchFiles(subFile.getPath(), express, isRegular);
                        if (subPaths == null || subPaths.isEmpty()) {
                            continue;
                        }
                        paths.addAll(subPaths);
                    }
                }
            }
        }
        return paths;
    }

    private static boolean doMatch(File file, String express, boolean isRegular) {
        if (isRegular) {
            if (file.getName().matches(express)) {
                return true;
            }
        } else {
            if (file.getName().equalsIgnoreCase(express)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPath(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() || file.isHidden()) {
            return false;
        }
        return true;
    }

}
