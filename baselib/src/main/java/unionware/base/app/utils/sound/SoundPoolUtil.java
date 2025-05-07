package unionware.base.app.utils.sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.blankj.utilcode.util.VibrateUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

import unionware.base.R;

/**
 * 播放声音的工具类
 * 采用SoundPool，比MediaPlayer更省内存
 */
public class SoundPoolUtil {
    @SuppressLint("StaticFieldLeak")
    private static volatile SoundPoolUtil instance = null;
    private SoundPool mSoundPool;
    private HashMap<String, Integer> listSoundId;

    public static SoundPoolUtil getInstance() {
        if (instance == null) {
            synchronized (SoundPoolUtil.class) {
                if (instance == null)
                    instance = new SoundPoolUtil();
            }
        }
        return instance;
    }

    public SoundPoolUtil() {
        listSoundId = new HashMap<>();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(3)//传入最多播放音频数量
                .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())//加载一个AudioAttributes
                .build();
    }

    /**
     * 释放播放池
     */
    public void clear() {
        if (mSoundPool != null) {
            mSoundPool.release();
            listSoundId.clear();
            mSoundPool = null;
        }
    }

    /**
     * 播放声音
     *
     * @param type        声音类型
     *                    0:扫描提示音 1:错误1 2:错误2 3:关箱提示 4:其他失败提示 5:超出数量提示 6:物料不匹配提示 7:重复扫描提示 8:提交失败提示 9:提交成功提示 10:其他成功提示
     * @param needVibrate 是否需要震动
     */
    private void playAudio(Context mContext,String type, boolean needVibrate) {
        if (mSoundPool == null)
            return;
        try {
            //声音ID 加载音频资源,第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
            // int voiceId = mSoundPool.load(mContext, getVolumeType(type), 1);
            int soundId = getVolumeType(type);
            int voiceId = 0;
            boolean flag = false;
            if (listSoundId.size() == 0) {
                voiceId = mSoundPool.load(mContext, soundId, 1);
            } else {
                //查找是否已经加载过
                for (String key : listSoundId.keySet()) {
                    if ((soundId + "").equals(key)) {
                        voiceId = listSoundId.get(key);
                        flag = true;
                        break;
                    }
                }
                //没加载过加载
                if (!flag)
                    voiceId = mSoundPool.load(mContext, soundId, 1);
            }
            int finalVoiceId = voiceId;
            if (flag) {
                mSoundPool.play(finalVoiceId, 1, 1, 1, 0, 1);
                if (needVibrate)
                    VibrateUtils.vibrate(200);
                return;
            }
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {  //异步需要等待加载完成，音频才能播放成功
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0) {
                        //第一个参数soundID
                        //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
                        //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
                        //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
                        //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
                        //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
                        //soundPool.play(voiceId, 1, 1, 1, 0, 1);
                        soundPool.play(finalVoiceId, 1, 1, 1, 0, 1);
                        listSoundId.put("" + soundId, finalVoiceId);
                        if (needVibrate)
                            VibrateUtils.vibrate(200);
                    }
                }
            });
        } catch (Resources.NotFoundException e) {//有可能找不到资源文件
            Log.e("SoundPoolUtil", e.getMessage());
        }
    }

    /**
     * 播放声音、没有震动
     */
    public void playAudio(Context context,String type) {
        playAudio(context,type, false);
    }

    /**
     * 播放声音+震动
     */
    public void playAudioVibrate(Context context,String type) {
        playAudio(context,type, true);
    }
    private int getVolumeType(String type) {
        int soundId;
        soundId = getDefault(type);
        return soundId;
    }
    private int getDefault(String type) {
        int soundId = -1;
        switch (type) {
            case "success":
                soundId = getResId("success", R.raw.class);
                break;
            case "error":
                soundId = getResId("error", R.raw.class);
                break;
            case "submit_success":
                soundId = getResId("submit_success", R.raw.class);
                break;
//            case "qr_sacn":
//                soundId = getResId("qr_sacn", R.raw.class);
//                //soundId = R.raw.qr_sacn;
//                break;
//            case "error":
//                soundId = getResId("error", R.raw.class);
//                // soundId = R.raw.error;
//                break;
//            case "error02":
//                soundId = getResId("error02", R.raw.class);
//                //soundId = R.raw.error02;
//                break;
//            case "closebox":
//                soundId = getResId("closebox", R.raw.class);
//                //soundId = R.raw.closebox;
//                break;
//            case "error03":
//                soundId = getResId("error03", R.raw.class);
//                // soundId = R.raw.error03;
//                break;
//            case "excesserror":
//                soundId = getResId("excesserror", R.raw.class);
//                //soundId = R.raw.excesserror;
//                break;
//            case "materialnotmatcherror":
//                soundId = getResId("materialnotmatcherror", R.raw.class);
//                //soundId = R.raw.materialnotmatcherror;
//                break;
//            case "repeatscanerror":
//                soundId = getResId("repeatscanerror", R.raw.class);
//                // soundId = R.raw.repeatscanerror;
//                break;
//            case "submiterror":
//                soundId = getResId("submiterror", R.raw.class);
//                //soundId = R.raw.submiterror;
//                break;
//            case "submitsuccess":
//                soundId = getResId("submitsuccess", R.raw.class);
//                //soundId = R.raw.submitsuccess;
//                break;
//            case "success":
//                soundId = getResId("success", R.raw.class);
//                //soundId = R.raw.success;
//                break;
//            case "repeated_reporting":
//                soundId = getResId("repeated_reporting", R.raw.class);
//                //soundId = R.raw.repeated_reporting;
//                break;
//            case "previous_operations_not_reported":
//                soundId = getResId("previous_operations_not_reported", R.raw.class);
//                //soundId = R.raw.previous_operations_not_reported;
//                break;
        }
        return soundId;
    }
    /**
     * 获取音频id
     * https://cloud.tencent.com/developer/ask/sof/103647763
     *
     * @param variableName 音频名字
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        Field field;
        int resId = -1;
        try {
            field = c.getField(variableName);
            try {
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SoundPoolUtil", e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SoundPoolUtil", e.getMessage());
        }
        return resId;
    }

}