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

    public Integer updateHasCoin(int hasCoin) {
        String sql = "update initialization_data set has_coin = " + hasCoin;
        return executeUpdate(getConnection(), sql);
    }

    public Integer addGains(BigDecimal gain) {
        String sql = "update initialization_data set gains = gains+" + gain;
        return executeUpdate(getConnection(), sql);
    }

    public Integer getHasCoin() {
        String sql = "select has_coin from initialization_data";
        String ret = getFirRow(getConnection(), sql);
        if (ret == null)
            throw new RuntimeException("get has_coin error!!!");
        return Integer.valueOf(ret);
    }

    public BigDecimal getGains() {
        String sql = "select gains from initialization_data";
        String ret = getFirRow(getConnection(), sql);
        if (ret == null)
            throw new RuntimeException("get has_coin error!!!");
        return BigDecimal.valueOf(Double.valueOf(ret));
    }

    private String getFirRow(Connection conn, String sql) {
        if (conn == null) {
            log.error("get connection error!");
            return null;
        }
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString("has_coin");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Integer executeUpdate(Connection conn, String sql) {
        if (conn == null) {
            log.error("get connection error!");
            return null;
        }
        Statement statement = null;
        int ret = 0;
        try {
            statement = conn.createStatement();
            ret = statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    private Long getIdAndExecute(Connection conn, String sql) {
        if (conn == null) {
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
        } finally {
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
        StringBuilder sql = new StringBuilder("insert into `order_gain` (sell_order_id,buy_order_id,gains) values(")
                .append("'").append(orderGain.getSellOrderId() == null ? -1L : orderGain.getSellOrderId()).append("',")
                .append("'").append(orderGain.getBuyOrderId() == null ? -1L : orderGain.getBuyOrderId()).append("',")
                .append("'").append(orderGain.getGains() == null ? BigDecimal.ZERO : orderGain.getGains()).append("')");
        return getIdAndExecute(getConnection(), sql.toString());
    }

    public void updateFreePrice(BigDecimal okNetAsset, BigDecimal hbNetAsset) {
        String sql = "update initialization_data set ok_free_price = " + okNetAsset + ", hb_free_price = " + hbNetAsset;
        executeUpdate(getConnection(), sql);
    }


    public void updateLastPrice(BigDecimal okNetAsset, BigDecimal hbNetAsset) {
        String sql = "update initialization_data set ok_last_price = " + okNetAsset + ", hb_last_price = " + hbNetAsset;
        executeUpdate(getConnection(), sql);
    }
}
