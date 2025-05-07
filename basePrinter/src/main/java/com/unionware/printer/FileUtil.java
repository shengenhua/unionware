package com.unionware.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;


/**
 * 文件工具类
 */
public class FileUtil {
    private static String TAG = "FileUtil";
    private static final int BUFFERSIZE = 1024;


    /**
     * @Author: hyg
     * @Date: 2019/8/8 17:21
     * @Description:保存为pdf文件
     */
    public static void SaveToPDF(String msg, Context mContext) {
        try {
            FileOutputStream fos = mContext.openFileOutput("pdf_android.zip",
                    Context.MODE_PRIVATE);
            fos.write(Base64.decode(msg.getBytes(), Base64.DEFAULT));
            fos.close();
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void createZipAndAddEntry(byte[] data, String zipFilePath, String entryName) throws IOException {
        // 创建文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
        // 创建ZipOutputStream
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        // 创建ZIP条目
        ZipEntry zipEntry = new ZipEntry(entryName);
        // 开始写入新的ZIP条目
        zipOutputStream.putNextEntry(zipEntry);
        // 将字节流写入ZIP文件
        zipOutputStream.write(data);
        // 关闭流
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        fileOutputStream.close();
    }


    public static void DeZip(Context mContext) {
        try {
            UnApacheZipFile(mContext.getFilesDir() + "/pdf_android.zip", mContext.getFilesDir().toString() + "/pdf_android");
            Log.d("打印", mContext.getFilesDir().toString() + "/pdf_android");
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        //先将bitmap转为byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //将byte[]转为base64
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }



    /**
     * 　　* 使用 org.apache.tools.zip.ZipFile 解压文件，它与 java 类库中的
     * 　　* java.util.zip.ZipFile 使用方式是一新的，只不过多了设置编码方式的
     * 　　* 接口。
     * 　　*
     * 　　* 注，apache 没有提供 ZipInputStream 类，所以只能使用它提供的ZipFile
     * 　　* 来读取压缩文件。
     * 　　* @param archive 压缩包路径
     * 　　* @param decompressDir 解压路径
     * 　　* @throws IOException
     * 　　* @throws FileNotFoundException
     * 　　* @throws ZipException
     */
    public static void UnApacheZipFile(String archive, String decompressDir) throws IOException, FileNotFoundException, ZipException {
        BufferedInputStream bi = null;
        ZipFile zf = new ZipFile(archive, "gb2312");//支持中文
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory()) {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
                bi.close();
            }
        }
        zf.close();
    }

}
