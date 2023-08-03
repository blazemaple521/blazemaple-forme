package com.blazemaple.forum.service;

import java.util.Date;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/10 14:27
 */
public interface DataService {

    public void recordUV(String ip);

    public long calculateUV(Date start, Date end);

    public void recordDAU(int userId);

    public long calculateDAU(Date start, Date end);
}
