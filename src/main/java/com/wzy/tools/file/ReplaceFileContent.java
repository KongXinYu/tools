package com.wzy.tools.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * 替换文件中对应的字符串
 * @author: WuZY
 * @time: 2021/4/12 0012
 */
@Slf4j
public class ReplaceFileContent {
    private static final String utf8 = "UTF-8";

    /**
     * 替换单个文件中的内容
     * @param path 文件路径
     * @param express 用于找到需要替换的行
     * @param replaceExp 用于该行中需要替换的内容
     * @param replaceElement 替换之后的内容
     */
    public static void replaceFileContent(String path, String express
            , String replaceExp, String replaceElement) {
        if (!FileUtils.checkPath(path)) {
            return;
        }
        // 替换后内容的临时数据
        CharArrayWriter tempStream = new CharArrayWriter();
        // 读取文件内容并将替换后的内容
        try(
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), utf8))
         ) {
            String line;
            Pattern pattern = Pattern.compile(express);
            Matcher matcher;
            while ((line = reader.readLine()) != null) {
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    line = line.replaceAll(replaceExp,replaceElement);
                }
                tempStream.write(line);
                tempStream.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将替换的内容写回文件
        try(FileWriter out = new FileWriter(path)) {
            tempStream.writeTo(out);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        tempStream.close();
    }

    /**
     * 替换文件夹下的所有文件内容
     * @param dir 文件夹路径
     * @param express 用于找到需要替换的行
     * @param replaceExp 用于该行中需要替换的内容
     * @param replaceElement 替换之后的内容
     */
    public static void replaceFilesContent(String dir, String filename, String express
            , String replaceExp, String replaceElement) {
        List<String> paths = FileUtils.matchFilesByName(dir, filename);
        if (null != paths && !paths.isEmpty()) {
            paths.forEach(filePath->{
                System.out.println(filePath);
                replaceFileContent(filePath, express , replaceExp, replaceElement);
            });
        }
    }

    public static void main(String[] args) {
        String dir = "E:\\wzy\\";
        String filename = "pom.xml";
        String express = ".*<version>(.*\\$.*)</version>";
        String replaceExp = "\\$\\{.*\\}";
        String replaceElement = "\\$\\{common.version\\}";

        replaceFilesContent(dir, filename, express, replaceExp , replaceElement);
    }



}
