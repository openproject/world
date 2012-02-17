package com.tianxia.app.floworld.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    //读取文件
    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);;
        } finally {
            if(is != null) {
                is.close();
            }
        }
        return text;
    }

    //从流中读取文件
    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            while((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }

    //将文本内容写入文件
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }
}
