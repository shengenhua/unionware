package unionware.base.app.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import unionware.base.app.ex.DeviceException;
import unionware.base.app.model.AuthBean;

/**
 * Author: sheng
 * Date:2024/8/30
 */
public class DeviceAuthUtils {

    /**
     * 手动设置是否需要授权
     */
    private static boolean TEST = false;

    public static void setDebug(boolean debug) {
        TEST = debug;
    }

    /**
     * 解密私钥
     */
    private static final String BEGIN_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPXFnhKPbyCqeecH\n" +
            "D+9u+7tPp4/C52fV82BkjsO2DrwUota8t7raIHdq1Y012HPUZWqqHuAHjZCHQiL6\n" +
            "IdUhCsbMqbNKigloU0TUmh6BNuhCXZqPF83R70bSJ2g3NIjSC3hb3uvpar7pXRjG\n" +
            "1jSantiWtaNU8M02GHqe/VRwyZ/RAgMBAAECgYB2w0DobSFhIp/3kMUViYbhc9Nf\n" +
            "t+pElfGecdk/H5mtLzelFtqd00v/CMQbEZsAFfHQAlcbIOs6nPHLnx18NRRUHB2o\n" +
            "fVUx770YkrQ2n7D367TGjoJN/LtOj7OTHKyovBBwzi6nGq09rfqTzHVr5tBEHvty\n" +
            "/pu3++5eM2kyiyC6RQJBAP6Ib4Pim33l79lvg3fltquF4zS1+Pqj18zB0BfvXHPw\n" +
            "txsAQwJd+2ti3JmUJt6jwV9CpUTUDg+u+lCRS95a0jMCQQD3MEFFKDUOXdda826q\n" +
            "jFQXvQOQJbvar/mt4//M8tJ89VAJjtcAL+Z1xUHBEr7UBzmHj7CADGWWH/SVNP4V\n" +
            "vanrAkARVFyzUARiJ/uShEMhMKSlPPJhLlqLRgh1SmTOnF9hDWyKDBSD3r41a3+1\n" +
            "fR7AXcnWoIT8EKv8aV9liN7vfqN9AkEAo5C0v/RZF0aVSEOoyQXOZ17VSr8NTSoA\n" +
            "x0YFcDKFgdY5vflc32zSzL9YQVAMIfUd5kv4eOK7HdnLWCgWsYAtPQJBANEQCetN\n" +
            "+TuglPnA24Xxd+wgHlYkA5cwKOHmYZdh+fCMZaQb11Eyg5CoiT5aHUkaMeSiF2r2\n" +
            "pZCzT6LQ/vdNZjQ=";

    private static String filePath = null;

    public static String getFilePath(Context context) {
        if (filePath == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                // 如果SD卡不存在，就保存到本应用的目录下
                filePath = context.getFilesDir().getAbsolutePath();
            }
        }
//        //创建文件夹
//        String authDir = filePath + "/auth/";
//        File dirFile = new File(authDir);
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
//        return authDir + "auth"+Product.PRODUCT_ID+".txt";//使用lic文件格式
        return filePath + "/auth" + Product.PRODUCT_ID + ".lic";
    }

    public static String getOldFilePath(Context context) {
        if (filePath == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                // 如果SD卡不存在，就保存到本应用的目录下
                filePath = context.getFilesDir().getAbsolutePath();
            }
        }
        return filePath + "/auth.txt";
    }

    /**
     * 检查校验时间 ，判断是否今天已经校验通过，防止重复反复校验获取数据
     * 通过返回 true 否则 false ,重新校验检查
     */
    public static boolean checkInspect() {
        if (TEST) {
            //写死许可
            return true;
        }
        MMKV mmkv = MMKV.defaultMMKV();
        long time = mmkv.decodeLong("inspect_time");
        if (time <= 0) {
            return false;
        }
        //当前时间戳是否 大于 保存的 时间 24小时
        long difference = System.currentTimeMillis() - time;
        // 检查差值是否小于24小时
        return difference < 86400000;
    }

    public static void saveFile(Context context, String content) {
        FileHelper.saveToFile(getFilePath(context), content);
    }


    /**
     *
     * @param data
     * @param productId 当前产品id
     * @return
     * @throws DeviceException
     */
    public static AuthBean getAuth(String data, String productId) throws DeviceException {
        //多个产品
        String[] licenseFile = data.split("\n");

        Map<String, String> plains = new HashMap<>();
        for (String s : licenseFile) {
            if (s.length() > 14) {
                //前 14 位为授权产品 id
                plains.put(s.substring(0, 14), s.substring(14));
            } else {
                plains.put(s, "");
            }
        }
        if (!plains.containsKey(productId)) {
            throw new DeviceException(ErrorCode.not_product_id);
        }

        if (TextUtils.isEmpty(plains.get(productId))) {
            throw new DeviceException(ErrorCode.license_data_null);
        }
        String plainText = null;
        try {
            plainText = RSAUtil.decryptPivate(plains.get(productId), BEGIN_PRIVATE_KEY);
        } catch (Exception e) {
            throw new DeviceException(ErrorCode.res_error);
        }
        try {
            return new Gson().fromJson(plainText, AuthBean.class);
        } catch (Exception e) {
            throw new DeviceException(ErrorCode.json_error);
        }
    }

    public static String error(DeviceException e) {
        String show = "设备授权异常";
        if (e.getError().equals(ErrorCode.license_null) || e.getError().equals(ErrorCode.license_data_null)) {
            show = "设备未授权";
        } else if (e.getError().equals(ErrorCode.time_expired)) {
            show = "设备已过有效期";
        }
        return String.format("%s(%s)", show, e.getError());
    }

    /**
     * 检验 是否通过
     * @return
     */
    public static boolean inspect(Context context, String productId) throws DeviceException {
        if (checkInspect()) {
            return true;
        }
        //兼容老的授权
        //先判断新授权路径，没有再判断老授权路径
        String fileData = FileHelper.readFile(getFilePath(context));
        if (TextUtils.isEmpty(fileData)) {
            fileData = FileHelper.readFile(getOldFilePath(context));
            if (TextUtils.isEmpty(fileData)) {
                throw new DeviceException(ErrorCode.license_null);
            }
        }
        AuthBean authBean = getAuth(fileData, productId);
        return inspectBean(context, fileData, authBean, productId, DeviceUtils.getDeviceId(context));
    }

    /**
     * 检验 是否通过
     * @return
     */
    public static boolean inspectByDateTips(Context context, String productId) throws DeviceException {
        if (checkInspect()) {
            return true;
        }
        //兼容老的授权
        //先判断新授权路径，没有再判断老授权路径
        String fileData = FileHelper.readFile(getFilePath(context));
        if (TextUtils.isEmpty(fileData)) {
            fileData = FileHelper.readFile(getOldFilePath(context));
            if (TextUtils.isEmpty(fileData)) {
                throw new DeviceException(ErrorCode.license_null);
            }
        }
        AuthBean authBean = getAuth(fileData, productId);
        return inspectBeanByDateTips(context, fileData, authBean, productId, DeviceUtils.getDeviceId(context));
    }

    /**
     * 检验 是否通过
     * @return
     */
    public static boolean inspect(Context context, String data, String productId) throws DeviceException, Exception {
        AuthBean authBean = getAuth(data, productId);
        return inspectBean(context, data, authBean, productId, DeviceUtils.getDeviceId(context));
    }

    /**
     * 检验 是否通过
     * @return
     */
    private static boolean inspectBean(Context context, String data, AuthBean authBean, String productId, String deviceId) throws DeviceException {
        if (TEST) {
            //写死许可
            return true;
        }
        try {
            if (authBean.getMachines() == null) {
                throw new DeviceException(ErrorCode.machines_null);
            }
            if (!authBean.getMachines().contains(deviceId)) {
                throw new DeviceException(ErrorCode.machines_not_id);
            }
            if (authBean.getProduct() == null) {
                throw new DeviceException(ErrorCode.product_null);
            }
            if (!productId.equals(authBean.getProduct().getId())) {
                throw new DeviceException(ErrorCode.proudct_not_id);
            }

            /*//取得资源对象
            URL url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            Date date2 = new Date(uc.getDate());*/

            //网络获取时间有问题，先用当前系统时间
            Date date2 = new Date(System.currentTimeMillis());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            if (authBean.getProduct().getTactics() != null && !authBean.getProduct().getTactics().isEmpty()) {// 永久时候，时间为空
                Date date1 = sdf.parse(authBean.getProduct().getTactics().get(0).getParam());
                if ((date1 != null ? date1.compareTo(date2) : 0) < 0) {
                    throw new DeviceException(ErrorCode.time_expired);
                }
            }
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("inspect_time", System.currentTimeMillis());
            saveFile(context, data);
        } catch (DeviceException e) {
            throw e;
        } catch (Exception e) {
            throw new DeviceException(ErrorCode.unknown);
        }
        return true;
    }

    /**
     * 检验 是否通过
     * @return
     */
    private static boolean inspectBeanByDateTips(Context context, String data, AuthBean authBean, String productId, String deviceId) throws DeviceException {
        if (TEST) {
            //写死许可
            return true;
        }
        try {
            if (authBean.getMachines() == null) {
                throw new DeviceException(ErrorCode.machines_null);
            }
            if (!authBean.getMachines().contains(deviceId)) {
                throw new DeviceException(ErrorCode.machines_not_id);
            }
            if (authBean.getProduct() == null) {
                throw new DeviceException(ErrorCode.product_null);
            }
            if (!productId.equals(authBean.getProduct().getId())) {
                throw new DeviceException(ErrorCode.proudct_not_id);
            }

            /*//取得资源对象
            URL url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            Date date2 = new Date(uc.getDate());*/

            //网络获取时间有问题，先用当前系统时间
            Date date2 = new Date(System.currentTimeMillis());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            if (authBean.getProduct().getTactics() != null && !authBean.getProduct().getTactics().isEmpty()) {// 永久时候，时间为空
                Date date1 = sdf.parse(authBean.getProduct().getTactics().get(0).getParam());
                if ((date1 != null ? date1.compareTo(date2) : 0) < 0) {
                    throw new DeviceException(ErrorCode.time_expired);
                } else {
                    long diffInMillies = date1.getTime() - date2.getTime();
                    long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
                    //Log.e("日期","还有多少天到期="+diffInMillies);
                    if (diffInDays <= 15) {
                        DeviceException exception = new DeviceException(ErrorCode.time_expired_tip);
                        //加msg
                        //WMS条码软件程序使用许可即将到期，有效期至2024-10-26，请联系供应商及时续订许可。
                        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
                        String msg = "软件程序使用设备授权即将到期，有效期至" + d.format(date1) + "，请到系统管理参数设置设备授权续订。";
                        exception.setMsg(msg);
                        throw exception;
                    }
                }
            }
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("inspect_time", System.currentTimeMillis());
            saveFile(context, data);
        } catch (DeviceException e) {
            throw e;
        } catch (Exception e) {
            throw new DeviceException(ErrorCode.unknown);
        }
        return true;
    }

    /**
     * 需要的权限
     * @return
     */
    public static String[] requiresPermission() {
        List<String> permission = new ArrayList<>(
                Arrays.asList(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE));
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permission.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        return permission.toArray(new String[0]);
    }

    public static class Product {
        //产品id
        public static final String PRODUCT_ID = "6649c619bd06af"; // 6649c619bd06afd6

    }

    private static class ErrorCode {
        /**
         * 未知错误
         */
        private static final String unknown = "0x0000";
        /**
         * 许可信息为空
         */
        private static final String license_null = "0x0001";
        /**
         * 没有匹配到与当前产品Id一致的许可信息
         */
        private static final String not_product_id = "0x0002";
        /**
         * 能匹配到产品Id，但加密信息为空
         */
        private static final String license_data_null = "0x0003";
        /**
         * 使用RSA模块解密失败
         */
        private static final String res_error = "0x0004";
        /**
         * 对许可信息Json反序列化失败
         */
        private static final String json_error = "0x0005";
        /**
         * Machines 属性为 Null，即许可信息中不包含 Machines 属性节点（许可信息确实是json，但并非约定的json结构）
         */
        private static final String machines_null = "0x0011";
        /**
         * Machines 属性不包含当前设备特征码
         */
        private static final String machines_not_id = "0x0012";
        /**
         * Product  属性为 Null，即许可信息中不包含 Product 属性节点（许可信息确实是json，但并非约定的json结构）
         */
        private static final String product_null = "0x0013";
        /**
         * Product.Id 属性不是当前产品Id
         */
        private static final String proudct_not_id = "0x0014";
        /**
         * 许可过期检查
         */
        private static final String time_expired = "0x0015";
        /**
         * 许可15天过期 自定义
         */
        private static final String time_expired_tip = "xxxxxx15";
    }
}
