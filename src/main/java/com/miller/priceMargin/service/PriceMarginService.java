package com.miller.priceMargin.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.miller.priceMargin.model.Order;
import com.miller.priceMargin.model.OrderGain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

/**
 * Created by Miller on 2017/1/1.
 */
@Component
public class PriceMarginService {
    @Autowired
    private DruidDataSource druidDataSource;

    private static Log log = LogFactory.getLog(PriceMarginService.class);

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = druidDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return conn;
    }

    //return id
    public Long saveOrder(Order order) {
        StringBuilder sql = new StringBuilder("insert into `order` (trade_center,trade_direction,ticker_price," +
                "amount,deal_amount,deal_price,create_time) VALUES (")
                .append("'").append(order.getTradeCenter() == null ? "" : order.getTradeCenter()).append("',")
                .append("'").append(order.getTradeDirection() == null ? "" : order.getTradeDirection()).append("',")
                .append("'").append(order.getTickerPrice() == null ? BigDecimal.ZERO : order.getTickerPrice()).append("',")
                .append("'").append(order.getAmount() == null ? BigDecimal.ZERO : order.getAmount()).append("',")
                .append("'").append(order.getDealAmount() == null ? BigDecimal.ZERO : order.getDealAmount()).append("',")
                .append("'").append(order.getDealPrice() == null ? BigDecimal.ZERO : order.getDealPrice()).append("',")
                .append("'").append(order.getCreateTime() == null ? new Timestamp(new Date().getTime()) : order.getCreateTime()).append("')");
        return getIdAndExecute(getConnection(), sql.toString());
    }


    private Long getIdAndExecute(Connection conn, String sql) {
        if (conn == null){
            log.error("get connection error!");
            return null;
        }
        Statement statement = null;
        ResultSet rs;
        Long id;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = statement.getGeneratedKeys();
            rs.next();
            id = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("order save failed ! SQLException");
        }finally{
            try {
                if (statement != null)
                    statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return id;
    }

    public Long saveOrderGain(OrderGain orderGain) {
        StringBuilder sql = new StringBuilder("insert into `order_gain` (sell_order_id,buy_order_id,gains,ok_free_price,hb_free_price) values(")
                .append("'").append(orderGain.getSellOrderId() == null ? -1L : orderGain.getSellOrderId()).append("',")
                .append("'").append(orderGain.getBuyOrderId() == null ? -1L : orderGain.getBuyOrderId()).append("',")
                .append("'").append(orderGain.getGains() == null ? BigDecimal.ZERO : orderGain.getGains()).append("',")
                .append("'").append(orderGain.getOkFreePrice() == null ? BigDecimal.ZERO : orderGain.getOkFreePrice()).append("',")
                .append("'").append(orderGain.getHbFreePrice() == null ? BigDecimal.ZERO : orderGain.getHbFreePrice()).append("')");
        return getIdAndExecute(getConnection(), sql.toString());
    }
}
