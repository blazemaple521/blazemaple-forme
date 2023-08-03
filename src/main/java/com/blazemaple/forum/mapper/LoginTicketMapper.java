package com.blazemaple.forum.mapper;

import com.blazemaple.forum.domain.entity.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * @author BlazeMaple
 * @description 针对表【login_ticket】的数据库操作Mapper
 * @createDate 2023-06-18 16:51:20
 * @Entity com.blazemaple.forum.domain.entity.LoginTicket
 */
@Mapper
@Deprecated
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {

    @Insert({
        "insert into login_ticket(userId,ticket,status,expired) ",
        "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket LoginTicket);

    @Select({
        "select id,userId,ticket,status,expired ",
        "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
        "<script>",
        "update login_ticket set status=#{status} where ticket=#{ticket} ",
        "<if test=\"ticket!=null\"> ",
        "and 1=1 ",
        "</if>",
        "</script>"
    })
    int updateStatus(String ticket, int status);
}




