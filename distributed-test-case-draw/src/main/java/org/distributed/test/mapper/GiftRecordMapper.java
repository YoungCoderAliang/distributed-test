package org.distributed.test.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.distributed.test.entity.GiftRecord;

public interface GiftRecordMapper {
    @Insert({
        "insert into gift_record (gift_id, user_id)",
        "values (#{giftId,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT})"
    })
    int insert(GiftRecord record);

    @InsertProvider(type=GiftRecordSqlProvider.class, method="insertSelective")
    int insertSelective(GiftRecord record);
}