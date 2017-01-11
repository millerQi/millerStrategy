package com.miller.priceMargin.weChat;

import com.alibaba.fastjson.JSON;
import com.miller.priceMargin.util.URLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tonyqi on 17-1-11.
 */
public class WeChatSendMessage {

    private static Log log = LogFactory.getLog(WeChatSendMessage.class);

    private static ExecutorService pool = Executors.newSingleThreadExecutor();

    public static void sendMsg(String msg) {
        pool.execute(() -> {
            String url = WeChatData.sendMsgUrl + AccessToken.getAccessToken();
            Map<String, Object> map = new HashMap<>();
            Map<String, String> content = new HashMap<>();
//        map.put("touser", "@all");
            map.put("toparty", "1");
//        map.put("totag", "1");
            map.put("msgtype", "text");
            map.put("agentid", 1);
            content.put("content", msg);
            map.put("text", content);
//        map.put("safe", 0);
            String ret = URLUtil.doPostString(url, JSON.toJSONString(map));
            if (!ret.contains("\"errcode\":0,\"")) {
                log.error("send msg error ! msg:" + msg + " ! errorRet :" + ret);
            }
        });
    }
}
