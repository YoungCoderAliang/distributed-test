package org.distributed.test.mapper;

import org.apache.ibatis.annotations.Update;
import org.distributed.test.entity.GiftV;

public interface GiftMapperEx extends GiftMapper {
    @Update({
        "update gift",
          "set num = #{num,jdbcType=INTEGER},",
          "v = #{v,jdbcType=BIGINT}",
        "where gift_id = #{giftId,jdbcType=BIGINT}",
        "and v = #{oldv,jdbcType=BIGINT}"
    })
    int updateByVersion(GiftV record);
}
