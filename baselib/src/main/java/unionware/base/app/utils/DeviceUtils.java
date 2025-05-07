package unionware.base.app.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.UUID;

public class DeviceUtils {

    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !TextUtils.isEmpty(tm.getDeviceId())) {
                    deviceId = tm.getDeviceId();
                }
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && deviceId.isEmpty() && !TextUtils.isEmpty(tm.getImei())) {
                    deviceId = tm.getImei();
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            //最后防线，没有 id 通过uuid 随机一个
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = getUniqueUUID();
            }
        }
        return deviceId;
    }

    private static String getUniqueUUID() {
        MMKV kv = MMKV.mmkvWithID("app");
        String uuid = kv.decodeString("device_uuid", "");
        if (TextUtils.isEmpty(uuid)) {/*不卸载就不会更新uuid,这种情况是连android id 都拿不到的情况*/
            uuid = UUID.randomUUID().toString();
            kv.encode("device_uuid", uuid);
        }
        return uuid;
    }

}
