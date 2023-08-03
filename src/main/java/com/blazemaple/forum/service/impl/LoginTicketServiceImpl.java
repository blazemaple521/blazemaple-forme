package com.blazemaple.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blazemaple.forum.domain.entity.LoginTicket;
import com.blazemaple.forum.service.LoginTicketService;
import com.blazemaple.forum.mapper.LoginTicketMapper;
import org.springframework.stereotype.Service;

/**
* @author BlazeMaple
* @description 针对表【login_ticket】的数据库操作Service实现
* @createDate 2023-06-18 16:51:20
*/
@Service
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketMapper, LoginTicket>
    implements LoginTicketService{

}




