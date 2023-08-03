package com.blazemaple.forum.mapper;

import com.blazemaple.forum.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author BlazeMaple
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-06-18 16:51:29
* @Entity com.blazemaple.forum.domain.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




