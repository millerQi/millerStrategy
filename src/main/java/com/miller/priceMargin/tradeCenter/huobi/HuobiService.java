package com.miller.priceMargin.tradeCenter.huobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miller.priceMargin.strategy.moreCenterPriceMargin.AllocationSource;
import com.miller.priceMargin.util.StringUtil;
import com.miller.priceMargin.util.URLUtil;
import com.miller.priceMargin.weChat.WeChatSendMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Miller on 2016/12/23.
 */
@Component
public class HuobiService extends Base {
//    private Log log = LogFactory.getLog(HuobiService.class);

    public String buy(int coinType, String price, String amount, Integer tradeid, String method) {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("price", price);
        paraMap.put("amount", amount);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
//        if (StringUtils.isNotEmpty(tradePassword)) {
//            paraMap.put("trade_password", tradePassword);
//        }
        if (null != tradeid) {
            paraMap.put("trade_id", tradeid);
        }
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 提交市价单接口
     *
     * @param coinType
     * @param amount
     * @param tradeid
     * @param method
     * @return
     * @throws Exception
     */
    public String buyMarket(int coinType, String amount, Integer tradeid, String method)
            throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("amount", amount);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        if (null != tradeid) {
            paraMap.put("trade_id", tradeid);
        }
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 撤销订单
     *
     * @param coinType
     * @param id
     * @param method
     * @return
     * @throws Exception
     */
    public String cancelOrder(int coinType, long id, String method) throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("id", id);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 获取账号详情
     *
     * @param method
     * @return
     * @throws Exception
     */
    public String getAccountInfo() {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", "get_account_info");
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }


    /**
     * 查询个人最新10条成交订单
     *
     * @param coinType
     * @param method
     * @return
     * @throws Exception
     */
    public String getNewDealOrders(int coinType, String method) throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 根据trade_id查询oder_id
     *
     * @param coinType
     * @param tradeid
     * @param method
     * @return
     * @throws Exception
     */
    public String getOrderIdByTradeId(int coinType, long tradeid, String method) throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("trade_id", tradeid);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 获取所有正在进行的委托
     *
     * @param coinType
     * @param method
     * @return
     * @throws Exception
     */
    public String getOrders(int coinType, String method) throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 获取订单详情
     * <p>
     * 状态　0未成交　1部分成交　2已完成　3已取消 4废弃（该状态已不再使用） 5异常 6部分成交已取消 7队列中
     */
    public String getOrderInfo(int coinType, long id, String method) {
        TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("id", id);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 限价卖出
     *
     * @param coinType
     * @param price
     * @param amount
     * @param tradeid
     * @param method
     * @return
     * @throws Exception
     */
    public String sell(int coinType, String price, String amount, Integer tradeid, String method) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("price", price);
        paraMap.put("amount", amount);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
//        if (StringUtils.isNotEmpty(tradePassword)) {
//            paraMap.put("trade_password", tradePassword);
//        }
        if (null != tradeid) {
            paraMap.put("trade_id", tradeid);
        }
        return post(paraMap, HUOBI_API_URL);
    }

    /**
     * 市价卖出
     *
     * @param coinType
     * @param amount
     * @param tradeid
     * @param method
     * @return
     * @throws Exception
     */
    public String sellMarket(int coinType, String amount, Integer tradeid, String method)
            throws Exception {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("method", method);
        paraMap.put("created", getTimestamp());
        paraMap.put("access_key", HUOBI_ACCESS_KEY);
        paraMap.put("secret_key", HUOBI_SECRET_KEY);
        paraMap.put("coin_type", coinType);
        paraMap.put("amount", amount);
        String md5 = sign(paraMap);
        paraMap.remove("secret_key");
        paraMap.put("sign", md5);
//        if (StringUtils.isNotEmpty(tradePassword)) {
//            paraMap.put("trade_password", tradePassword);
//        }
        if (null != tradeid) {
            paraMap.put("trade_id", tradeid);
        }
        return post(paraMap, HUOBI_API_URL);
    }

    private String hbTickerUrl = "http://api.huobi.com/staticmarket/ticker_btc_json.js";
    private String depthUrl = "http://api.huobi.com/staticmarket/depth_" + AllocationSource.getCoinType() + "_" + AllocationSource.depthSize + ".js";
    private String okDepthUrl = "https://www.okcoin.cn/api/v1/depth.do?symbol=" + AllocationSource.getCoinType() + "_cny&size=" + AllocationSource.depthSize;

    public BigDecimal ticker() {
        String ret = URLUtil.doGet(hbTickerUrl, null);
        if (ret == null)
            return null;
        JSONObject object = JSON.parseObject(ret).getJSONObject("ticker");
        return object.getBigDecimal("last");
    }

    /**
     * 获取huobi/okcoin深度数据
     *
     * @return
     */
    private Log log = LogFactory.getLog(HuobiService.class);

    public Map<String, BigDecimal[]> depth() {
        Map<String, BigDecimal[]> map = new HashMap<>();
        String ret = URLUtil.doGet(depthUrl, null);
        String okRet = URLUtil.doGet(okDepthUrl, null);
        if (StringUtil.isEmpty(ret)) {
            log.error("huobi 's depth get error!");
            WeChatSendMessage.sendMsg("huobi 's depth get error!");
            return null;
        } else if (StringUtil.isEmpty(ret)) {
            log.error("okcoin 's depth get error!");
            WeChatSendMessage.sendMsg("okcoin 's depth get error!");
            return null;
        }
        JSONObject object = JSON.parseObject(ret);
        JSONArray asks = object.getJSONArray("asks");
        JSONArray bids = object.getJSONArray("bids");

        JSONObject okO = JSON.parseObject(okRet);
        JSONArray okAsks = okO.getJSONArray("asks");
        JSONArray okBids = okO.getJSONArray("bids");

        int size = asks.size();
        BigDecimal asksAvgPrice = BigDecimal.ZERO;
        BigDecimal bidsAvgPrice = BigDecimal.ZERO;
        BigDecimal asksAmount = BigDecimal.ZERO;
        BigDecimal bidsAmount = BigDecimal.ZERO;

        BigDecimal asksAvgPriceO = BigDecimal.ZERO;
        BigDecimal bidsAvgPriceO = BigDecimal.ZERO;
        BigDecimal asksAmountO = BigDecimal.ZERO;
        BigDecimal bidsAmountO = BigDecimal.ZERO;


        for (int i = 0; i < size; i++) {
            JSONArray askA = asks.getJSONArray(i);
            asksAvgPrice = asksAvgPrice.add(askA.getBigDecimal(0));
            asksAmount = asksAmount.add(askA.getBigDecimal(1));
            JSONArray bidA = bids.getJSONArray(i);
            bidsAvgPrice = bidsAvgPrice.add(bidA.getBigDecimal(0));
            bidsAmount = bidsAmount.add(bidA.getBigDecimal(1));
            /**okcoin**/
            JSONArray okAskA = okAsks.getJSONArray(i);
            asksAvgPriceO = asksAvgPriceO.add(okAskA.getBigDecimal(0));
            asksAmountO = asksAmountO.add(okAskA.getBigDecimal(1));
            JSONArray okBidA = okBids.getJSONArray(i);
            bidsAvgPriceO = bidsAvgPriceO.add(okBidA.getBigDecimal(0));
            bidsAmountO = bidsAmountO.add(okBidA.getBigDecimal(1));
        }
        BigDecimal[] hbAsk = new BigDecimal[2];
        BigDecimal[] hbBid = new BigDecimal[2];
        hbAsk[0] = asksAvgPrice.divide(BigDecimal.valueOf(AllocationSource.depthSize), 2);//深度均价
        hbAsk[1] = asksAmount;
        hbBid[0] = bidsAvgPrice.divide(BigDecimal.valueOf(AllocationSource.depthSize), 2);
        hbBid[1] = bidsAmount;
        BigDecimal[] okAsk = new BigDecimal[2];
        BigDecimal[] okBid = new BigDecimal[2];
        okAsk[0] = asksAvgPriceO.divide(BigDecimal.valueOf(AllocationSource.depthSize), 2);//深度均价
        okAsk[1] = asksAmountO;
        okBid[0] = bidsAvgPriceO.divide(BigDecimal.valueOf(AllocationSource.depthSize), 2);
        okBid[1] = bidsAmountO;
        map.put("hbAsks", hbAsk);
        map.put("hbBids", hbBid);
        map.put("okAsks", okAsk);
        map.put("okBids", okBid);
        return map;
    }

    public static void main(String[] args) {
        HuobiService service = new HuobiService();
        Map<String, BigDecimal[]> map = service.depth();
        System.out.println("hbAsks:" + Arrays.toString(map.get("hbAsks")));
        System.out.println("hbBids:" + Arrays.toString(map.get("hbBids")));
        System.out.println("okAsks:" + Arrays.toString(map.get("okAsks")));
        System.out.println("okBids:" + Arrays.toString(map.get("okBids")));
    }

}
