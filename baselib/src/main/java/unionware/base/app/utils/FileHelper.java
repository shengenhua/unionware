package unionware.base.app.utils;

import static com.alibaba.android.arouter.utils.Consts.TAG;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;

public class FileHelper {
    public static String combinePath(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    /**
     * 读取文件
     *
     * @param filepath 文件路径
     */
    public static String readFile(String filepath) {
        String encoding = "UTF-8";
        return readFile(filepath, encoding);
    }

    /**
     * 读取文件，指定编码
     *
     * @param filepath 文件路径
     */
    public static String readFile(String filepath, String encoding) {
        try {
            filepath = URLDecoder.decode(filepath, "utf-8");
            File file = new File(filepath);
            Long filelength = file.length();
            byte[] filecontent = new byte[filelength.intValue()];
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return new String(filecontent, encoding);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveToFile(String path, String content) {

        BufferedWriter out = null;
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            //Toast.makeText(context, "请检查SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        //取得SD卡根目录
        File file = Environment.getExternalStorageDirectory();
        try {
            Log.e(TAG, "======SD卡根目录：" + file.getCanonicalPath());
            if (file.exists()) {
                Log.e(TAG, "file.getCanonicalPath() == " + file.getCanonicalPath());
            }
      /*
      输出流的构造参数1：可以是File对象 也可以是文件路径
      输出流的构造参数2：默认为False=>覆盖内容； true=>追加内容
       */
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false)));
            out.write(content);
            //Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将字符串写入文件
     *
     * @param filepath
     * @param text
     * @param isAppend
     */
    public static void writeFile(String filepath, String text, boolean isAppend) {

        try {
            filepath = URLDecoder.decode(filepath, "utf-8");
            File file = new File(filepath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream f = new FileOutputStream(filepath, isAppend);
            f.write(text.getBytes());
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filepath, String text, String encodeing) {

        try {
            filepath = URLDecoder.decode(filepath, "utf-8");
            File file = new File(filepath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            // FileOutputStream f = new FileOutputStream(filepath, "GBK");
            // f.write(text.getBytes());
            // f.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filepath), encodeing);
            writer.append(text);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
