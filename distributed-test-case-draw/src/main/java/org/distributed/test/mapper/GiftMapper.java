package org.distributed.test.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.distributed.test.entity.Gift;

public interface GiftMapper {
    @Delete({
        "delete from gift",
        "where gift_id = #{giftId,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long giftId);

    @Insert({
        "insert into gift (gift_id, gift_name, ",
        "num, v)",
        "values (#{giftId,jdbcType=BIGINT}, #{giftName,jdbcType=VARCHAR}, ",
        "#{num,jdbcType=INTEGER}, #{v,jdbcType=BIGINT})"
    })
    int insert(Gift record);

    @InsertProvider(type=GiftSqlProvider.class, method="insertSelective")
    int insertSelective(Gift record);

    @Select({
        "select",
        "gift_id, gift_name, num, v",
        "from gift",
        "where gift_id = #{giftId,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="gift_id", property="giftId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gift_name", property="giftName", jdbcType=JdbcType.VARCHAR),
        @Result(column="num", property="num", jdbcType=JdbcType.INTEGER),
        @Result(column="v", property="v", jdbcType=JdbcType.BIGINT)
    })
    Gift selectByPrimaryKey(Long giftId);

    @UpdateProvider(type=GiftSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Gift record);

    @Update({
        "update gift",
        "set gift_name = #{giftName,jdbcType=VARCHAR},",
          "num = #{num,jdbcType=INTEGER},",
          "v = #{v,jdbcType=BIGINT}",
        "where gift_id = #{giftId,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Gift record);
}