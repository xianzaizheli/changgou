package com.changgou.seckill.timer;

import com.changgou.entity.DateUtil;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "* 0/1 * * * *")
    public void loadGoodsPushRedis(){
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date startTime : dateMenus){
            String timeName = DateUtil.data2str(startTime, DateUtil.PATTERN_YYYY_MM_DDHHMM);
//            System.out.println(timeName);
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //审核要通过
            criteria.andEqualTo("status","1");
            //库存要大于0，这里是有问题的
            criteria.andGreaterThan("num",0);
            //确定上架时间
            criteria.andGreaterThanOrEqualTo("startTime",startTime);
            criteria.andLessThan("endTime",DateUtil.addDateHour(startTime,2));

            Set keys = redisTemplate.boundHashOps("Seckill_"+timeName).keys();
            if(null != keys && keys.size() > 0){
                criteria.andNotIn("id",keys);
                //这里不能直接return，因为如果你数据没有一次性到位呢，后面还要加商品就加不进去了
            }
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            for(SeckillGoods goods : seckillGoods){
                redisTemplate.boundHashOps("Seckill_"+timeName).put(goods.getId()+"",goods);
                redisTemplate.expireAt("Seckill_"+timeName,DateUtil.addDateHour(startTime,2));

                //为了更好判断redis中是否还有库存可以被消费，防止超卖，不然的话，可能会多卖
                Long[] goodsCountList = createGoodsCountList(goods.getStockCount(), goods.getId());
                if(null != goodsCountList){
                    redisTemplate.opsForList().leftPushAll("SeckillGoodsCountList_"+goods.getId(), goodsCountList);
                }
                //为了更精准的同步数据到数据库中，防止出现少买的情况
                redisTemplate.opsForValue().increment("SeckillGoodsCount_"+goods.getId(),goods.getStockCount());
            }

        }
    }

    /**
     * 生成一个数组，内部数量为
     * @param num
     * @param id
     * @return
     */
    public Long[] createGoodsCountList(int num,long id){
        Long[] count = new Long[num];
        for (int i = 0; i < count.length ; i++) {
            count[i] = id;
        }
        return count;
    }
}
