package com.miller.priceMargin.weChat;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.fastjson.JSON;
import com.miller.priceMargin.util.URLUtil;

/**
 * Created by tonyqi on 17-1-11.
 */
public class AccessToken {
    private static Log log = LogFactory.getLog(AccessToken.class);

    private AccessToken() {
    }

    private static String accessToken;

    //最后更新token的时间戳
    private static Long lastTime;

    static String getAccessToken() {
        if (checkTimeout()) {//超时调用获取最新token接口
            String content = URLUtil.doGet("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + WeChatData.corpId + "&corpsecret=" +
                    WeChatData.secretId, null);
            if (content != null && content.contains("access_token")) {//接口调用成功
                accessToken = JSON.parseObject(content).getString("access_token");
                lastTime = System.currentTimeMillis();
            } else {//调用失败
                log.error("failed to getToken , error msg :" + content);
                initAll();
            }
        }
        return accessToken;
    }

    private static void initAll() {
        lastTime = null;
        accessToken = null;
    }

    /**
     * 检测token是否超时
     *
     * @return true 超时或未获取token / false 未超时
     */
    private static boolean checkTimeout() {
        if (lastTime == null)//第一次获取token
            return true;
        else if (System.currentTimeMillis() - lastTime > 7200000)//token超时时间为1.5小时
            return true;
        return false;
    }
}
