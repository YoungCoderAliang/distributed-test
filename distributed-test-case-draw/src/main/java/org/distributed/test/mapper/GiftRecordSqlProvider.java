package org.distributed.test.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;

import org.distributed.test.entity.GiftRecord;

public class GiftRecordSqlProvider {

    public String insertSelective(GiftRecord record) {
        BEGIN();
        INSERT_INTO("gift_record");
        
        if (record.getGiftId() != null) {
            VALUES("gift_id", "#{giftId,jdbcType=BIGINT}");
        }
        
        if (record.getUserId() != null) {
            VALUES("user_id", "#{userId,jdbcType=BIGINT}");
        }
        
        return SQL();
    }
}