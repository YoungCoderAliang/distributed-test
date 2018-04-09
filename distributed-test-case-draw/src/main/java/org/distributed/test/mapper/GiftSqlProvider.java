package org.distributed.test.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import org.distributed.test.entity.Gift;

public class GiftSqlProvider {

    public String insertSelective(Gift record) {
        BEGIN();
        INSERT_INTO("gift");
        
        if (record.getGiftId() != null) {
            VALUES("gift_id", "#{giftId,jdbcType=BIGINT}");
        }
        
        if (record.getGiftName() != null) {
            VALUES("gift_name", "#{giftName,jdbcType=VARCHAR}");
        }
        
        if (record.getNum() != null) {
            VALUES("num", "#{num,jdbcType=INTEGER}");
        }
        
        if (record.getV() != null) {
            VALUES("v", "#{v,jdbcType=BIGINT}");
        }
        
        return SQL();
    }

    public String updateByPrimaryKeySelective(Gift record) {
        BEGIN();
        UPDATE("gift");
        
        if (record.getGiftName() != null) {
            SET("gift_name = #{giftName,jdbcType=VARCHAR}");
        }
        
        if (record.getNum() != null) {
            SET("num = #{num,jdbcType=INTEGER}");
        }
        
        if (record.getV() != null) {
            SET("v = #{v,jdbcType=BIGINT}");
        }
        
        WHERE("gift_id = #{giftId,jdbcType=BIGINT}");
        
        return SQL();
    }
}