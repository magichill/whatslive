package com.letv.whatslive.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Title: 处理web相关功能的工具类
 * Desc: 处理web相关功能的工具类
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-11-21 下午5:36
 */
public class ModelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ModelUtils.class);

    public static void main(String args[]) {
        String line = readTxtFile("d:\\devmode.txt");
    }


    public static String readTxtFile(String filePath) {
        String lineTxt = null;
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);

                while ((lineTxt = bufferedReader.readLine()) != null) {
                    System.out.println(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return lineTxt;
    }


}
