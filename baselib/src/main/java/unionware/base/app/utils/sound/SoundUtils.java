package unionware.base.app.utils.sound;

import android.content.Context;

import com.unionware.base.lib_ui.utils.SoundType;

import unionware.base.app.utils.sound.SoundPoolUtil;

public class SoundUtils {
    public static void playVoice(Context context, boolean isSuccess) {
        //MediaPlayer.create(context, isSuccess ? R.raw.success : R.raw.error).start();
        if (isSuccess) {
            SoundPoolUtil.getInstance().playAudio(context, SoundType.Default.SUCCESS);
        } else {
            SoundPoolUtil.getInstance().playAudio(context, SoundType.Default.ERROR);
        }

    }

    public static void playVoice(Context context, String type) {
        SoundPoolUtil.getInstance().playAudio(context, type);
    }

    /**
     *  除非要求加振动默认，都只播放声音
     * @param context
     * @param type
     */
    public static void playVoiceVibrate(Context context, String type) {
        SoundPoolUtil.getInstance().playAudioVibrate(context, type);
    }
}
