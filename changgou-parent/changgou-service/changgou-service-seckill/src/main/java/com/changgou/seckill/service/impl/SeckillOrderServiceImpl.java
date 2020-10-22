package com.changgou.seckill.service.impl;

import com.changgou.entity.IdWorker;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Override
    public void closeOrder(String username) {
        //获取redis中订单消息
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        //获取用户排队消息
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundHashOps("UserQueueStatus").get(username);
        //验证一下这些消息是否存在
        if(seckillOrder != null && seckillStatus != null){
            //删除无效订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            //回滚库存
            //1、获取redis是否还有该商品
            SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps("Seckill_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
            //2、如果redis中没有，那么就从数据库中取数据
            if(seckillGoods == null){
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
            }
            //3、库存+1（递增数量+1，队列+1）
            // redis的库存判断
            redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGoods.getId()).leftPush(seckillGoods.getId());
            //redis与MySQL的精准同步判断
            Long count = redisTemplate.opsForValue().increment("SeckillGoodsCount_"+seckillStatus.getGoodsId(),1);
            //库存回滚一下
            seckillGoods.setStockCount(count.intValue());

            //4、将数据同步到redis中
            redisTemplate.boundHashOps("Seckill_"+seckillStatus.getTime()).put(seckillGoods.getGoodsId() , seckillGoods);

            //5、清理排队标志
            redisTemplate.boundHashOps("UserQueueCount").delete(username);
            //6、清理抢单标志
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
        }
    }

    @Override
    public void updatePayStatus(String outTradeNo, String transactionId, String username) {
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());
        seckillOrder.setTransactionId(transactionId);

        seckillOrderMapper.insertSelective(seckillOrder);
        //清空缓存
        redisTemplate.boundHashOps("SeckillOrder").delete(username);
        //删除排队消息
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        //删除抢购状态
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }

    @Override
    public SeckillStatus query(String username) {
        return (SeckillStatus)redisTemplate.boundHashOps("UserQueueStatus").get(username);
    }

    @Override
    public Boolean addRedisOrder(String id, String key, String username) {
        Long userQueueCount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        redisTemplate.expire("UserQueueCount",30, TimeUnit.HOURS);
        if(userQueueCount > 1){
            //判断是否重复抢单，如果是，那么就抛一个异常20005
            throw new RuntimeException(String.valueOf(StatusCode.REPERROR));
        }
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, Long.parseLong(id), key);
        //将秒杀商品信息存到redis中去，每一个秒杀都存进去，这里用于消费完就没了
        redisTemplate.opsForList().leftPush("SeckillOrderQueue",seckillStatus);

        //用于判断每一个用户后面的订单详情
        redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);
        //多线程操作
        multiThreadingCreateOrder.createOrder();
        return true;
    }

    /**
     * SeckillOrder条件+分页查询
     * @param seckillOrder 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder){
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder){
        Example example=new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(seckillOrder!=null){
            // 主键
            if(!StringUtils.isEmpty(seckillOrder.getId())){
                    criteria.andEqualTo("id",seckillOrder.getId());
            }
            // 秒杀商品ID
            if(!StringUtils.isEmpty(seckillOrder.getSeckillId())){
                    criteria.andEqualTo("seckillId",seckillOrder.getSeckillId());
            }
            // 支付金额
            if(!StringUtils.isEmpty(seckillOrder.getMoney())){
                    criteria.andEqualTo("money",seckillOrder.getMoney());
            }
            // 用户
            if(!StringUtils.isEmpty(seckillOrder.getUserId())){
                    criteria.andEqualTo("userId",seckillOrder.getUserId());
            }
            // 商家
            if(!StringUtils.isEmpty(seckillOrder.getSellerId())){
                    criteria.andEqualTo("sellerId",seckillOrder.getSellerId());
            }
            // 创建时间
            if(!StringUtils.isEmpty(seckillOrder.getCreateTime())){
                    criteria.andEqualTo("createTime",seckillOrder.getCreateTime());
            }
            // 支付时间
            if(!StringUtils.isEmpty(seckillOrder.getPayTime())){
                    criteria.andEqualTo("payTime",seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if(!StringUtils.isEmpty(seckillOrder.getStatus())){
                    criteria.andEqualTo("status",seckillOrder.getStatus());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(seckillOrder.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if(!StringUtils.isEmpty(seckillOrder.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",seckillOrder.getReceiverMobile());
            }
            // 收货人
            if(!StringUtils.isEmpty(seckillOrder.getReceiver())){
                    criteria.andEqualTo("receiver",seckillOrder.getReceiver());
            }
            // 交易流水
            if(!StringUtils.isEmpty(seckillOrder.getTransactionId())){
                    criteria.andEqualTo("transactionId",seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder){
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder){
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id){
        return  seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
