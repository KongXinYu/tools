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

    /**
     * 通过文件夹名称获取目录下匹配到的文件夹
     * @param path 文件路劲
     * @param express 表达式
     * @return
     */
    public static List<String> matchDirsByName(String path, String express){
        return matchDirs(path, express, false);
    }

    /**
     * 获取目录下匹配到的文件夹
     * @param path 文件路劲
     * @param express 表达式
     * @param isRegular 是否支持正则
     * @return
     */
    public static List<String> matchDirs(String path, String express, boolean isRegular) {
        if (!checkPath(path)) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        File file = new File(path);

        if (file.isDirectory()) {
            // 匹配到对应文件夹添加,否则继续递归匹配
            if (doMatch(file, express, isRegular)) {
                paths.add(file.getPath());
            } else {
                File[] subFiles = file.listFiles();
                if (null != subFiles || subFiles.length == 0 ) {
                    for (File subFile : subFiles) {
                        List<String> subPaths = matchDirs(subFile.getPath(), express, isRegular);
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

    /**
     * 批量删除文件
     * @param paths 需要删除的文件路径
     * @return
     */
    public static void deleteFiles(List<String> paths) {
        File file;
        for(String path : paths) {
            file = new File(path);
            if (file.isDirectory()) {
                deleteDirectory(path);
            } else {
                deleteFile(path);
            }
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String path = "E:\\wzy\\V2";
        String dir = "target";
        List<String> dirs = matchDirsByName(path, dir);
        dirs.forEach(obj->{
            System.out.println(obj + " ");
        });
        deleteFiles(dirs);
    }

}
