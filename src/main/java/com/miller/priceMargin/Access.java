package com.miller.priceMargin;

import com.miller.priceMargin.strategy.moreCenterPriceMargin.MoreCenterPriceMargin;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Miller on 2017/1/1.
 */
public class Access {

    public static void main(String[] args) throws InterruptedException {
        loadProperties();
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/Application-context.xml");
        MoreCenterPriceMargin moreCenter = context.getBean(MoreCenterPriceMargin.class);
        moreCenter.startStrategy();
    }

    private static void loadProperties() {
        Properties pps = new Properties();
        try {
            pps.load(Access.class.getClassLoader().getResourceAsStream("config/logging.properties"));
        } catch (IOException e) {
            System.out.println("配置文件读取失败,运行结束");
            System.exit(0);
        }
        PropertyConfigurator.configure(pps);
    }
}
