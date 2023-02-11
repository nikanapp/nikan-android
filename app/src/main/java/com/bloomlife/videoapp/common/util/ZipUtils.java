package com.bloomlife.videoapp.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/20.
 */
public class ZipUtils{

    public static final int BUFFER_LENGHT = 10 * 1024;

    public static void tarZipFile(String zipPath, String tarPath){
        InputStream is = null;
        try {
            is = new FileInputStream(zipPath);
            tarZipFile(is, tarPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void tarZipFile(InputStream is, String tarPath){
        File tarFilePath = new File(tarPath);
        if (!tarFilePath.exists()){
            tarFilePath.mkdirs();
        }
        ZipInputStream zis = null;
        FileOutputStream os = null;
        BufferedInputStream bis = null;
        try {
            byte[] buffer = new byte[BUFFER_LENGHT];
            ZipEntry zEntry = null;
            bis = new BufferedInputStream(is);
            zis = new ZipInputStream(bis);
            while ((zEntry = zis.getNextEntry()) != null) {
                File file = new File(tarFilePath.getPath(), zEntry.getName());
                os = new FileOutputStream(file);// 得到数据库文件的写入流
                int count = 0;
                if (zEntry.isDirectory()) {
                    file.mkdirs();
                }
                while ((count = zis.read(buffer, 0, BUFFER_LENGHT)) > 0) {
                    os.write(buffer, 0, count);
                    os.flush();
                }
                zis.closeEntry();
                os.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                if (zis != null)
                    zis.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
