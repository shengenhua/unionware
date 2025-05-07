package com.unionware.basicui.setting;

import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.tencent.mmkv.MMKV;
import com.unionware.basicui.R;
import unionware.base.databinding.SettingNetworkActivtiyBinding;
import com.unionware.path.RouterPath;

import kotlin.jvm.JvmField;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;

@Route(path = RouterPath.Person.PATH_PERSON_NET_CONFIG)
public class ConfigNetActivity extends BaseBindActivity<SettingNetworkActivtiyBinding> {
    private MMKV kv;
    @JvmField
    @Autowired(name = "isSetting")
    boolean isSetting = false;

    @Override
    public int onBindLayout() {
        return R.layout.setting_network_activtiy;
    }

    @Override
    public void initView() {
        kv = MMKV.mmkvWithID("app");
        getMBind().layoutToolbar.tbTitle.setText("服务器配置");
        setSupportActionBar(getMBind().layoutToolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getMBind().layoutToolbar.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        getMBind().tvSettingNetSave.setOnClickListener(view -> {
            String url = getMBind().etSettingNetIp.getText().toString().trim();
            if (isURL(url)) {
                if (!url.endsWith("/")) {
                    url = url + "/";
                }
                kv.encode("url", url);
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                setResult(300, getIntent().putExtra("url", url));
                finish();
            } else {
                getMBind().etSettingNetIp.setFocusableInTouchMode(true);
                getMBind().etSettingNetIp.setEnabled(true);
                getMBind().etSettingNetIp.setSelection(0, url.length());
                Toast.makeText(mContext, "格式不正确，请重新输入！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initData() {
        getMBind().etSettingNetIp.setText(kv.decodeString("url"));
        getMBind().etSettingNetIp.postDelayed(() -> getMBind().etSettingNetIp.requestFocus(), 50);
        getMBind().etSettingNetIp.setSelection(0, getMBind().etSettingNetIp.getText().toString().length());
    }

    /**
     * 判断一个字符串是否为url
     *
     * @param str String 字符串
     * @return boolean 是否为url
     * @author peng1 chen
     **/
    public static boolean isURL(String str) {
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches(regex);
    }

}
