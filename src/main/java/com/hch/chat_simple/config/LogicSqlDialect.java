package com.hch.chat_simple.config;

import java.util.Properties;

import com.github.pagehelper.dialect.helper.MySqlDialect;
import com.github.pagehelper.parser.CountJSqlParser45;
import com.github.pagehelper.parser.CountSqlParser;
import com.github.pagehelper.parser.OrderByJSqlParser45;
import com.github.pagehelper.parser.OrderBySqlParser;
import com.github.pagehelper.util.ClassUtil;

public class LogicSqlDialect extends MySqlDialect {
    
    @Override
    public void setProperties(Properties properties) {
        this.countSqlParser = ClassUtil.newInstance(properties.getProperty("countSqlParser"), CountSqlParser.class, properties, CountJSqlParser45::new);
        this.orderBySqlParser = ClassUtil.newInstance(properties.getProperty("orderBySqlParser"), OrderBySqlParser.class, properties, OrderByJSqlParser45::new);

    }
}
