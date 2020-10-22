package com.changgou.order.service.impl;

import com.changgou.entity.IdWorker;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:shenkunlin
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private UserFeign userFeign;

    /**
     * 增加Order
     * @param order
     */
    @Override
    public void add(Order order){
        Map<String, String> map = new TokenDecode().dcodeToken();
        String username = map.get("username");

        List<OrderItem> orderItems = new ArrayList<>();
        for(String skuId : order.getSkuIds()){
            orderItems.add((OrderItem) redisTemplate.opsForHash().get("Cart_"+order.getUsername(),skuId));
        }

        //数量合计
        int totalNum = 0;
        //金额合计
        int totalMoney = 0;
        //实付金额
        int totalPayMoney = 0;

        //设置主键Id
        order.setId("No."+idWorker.nextId());

        for(OrderItem orderItem : orderItems){
            orderItem.setId("No."+idWorker.nextId());
            orderItem.setOrderId(order.getId());
            orderItem.setIsReturn("0");

            orderItemMapper.insertSelective(orderItem);
            skuFeign.decrCount(orderItem);
            totalNum+= orderItem.getNum();
            totalMoney+=orderItem.getMoney();
            totalPayMoney+=orderItem.getPayMoney();
        }

        order.setTotalMoney(totalMoney);
        order.setTotalNum(totalNum);
        order.setPayMoney(totalPayMoney);
        order.setPreMoney(totalMoney-totalPayMoney);

        //订单来源，1：WEB
        order.setSourceType("1");
        //创建时间
        order.setCreateTime(new Date());
        //修改时间
        order.setUpdateTime(order.getCreateTime());
        //是否评价，0：未评价，1：已评价
        order.setBuyerRate("0");
        //订单状态，0:未完成,1:已完成，2：已退货
        order.setOrderStatus("0");
        //支付状态，0:未发货，1：已发货，2：已收货
        order.setConsignStatus("0");

        Result result = userFeign.addUserPoints(10);

        for(String skuId : order.getSkuIds()){
            redisTemplate.opsForHash().delete("Cart_"+order.getUsername(),skuId);
        }

        orderMapper.insert(order);
        if(order.getPayType().equalsIgnoreCase("1")){
            redisTemplate.boundHashOps("Order").put(order.getId(),order);
        }
    }

    /**
     * 支付失败删除订单，回滚库存..
     * @param orderId
     */
    @Override
    public void deleteOrder(String orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        order.setPayStatus("2");
        order.setUpdateTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        //只要有支付，那么就要删除redis中的订单队列
        redisTemplate.boundHashOps("Order").delete(orderId);

        //回滚库存
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        List<OrderItem> orderItems = orderItemMapper.select(orderItem);
        for(OrderItem oi : orderItems){
            skuFeign.rollBackCount(oi);
        }
    }

    /**
     * 修改订单
     * @param orderId
     * @param datetime
     * @param transactionid
     */
    @Override
    public void updateStatus(String orderId, Date datetime, String transactionid) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        order.setPayTime(datetime);
        order.setUpdateTime(datetime);
        order.setOrderStatus("1");
        orderMapper.updateByPrimaryKeySelective(order);

        //支付成功
        redisTemplate.boundHashOps("Order").delete(orderId);
    }

    /**
     * Order条件+分页查询
     * @param order 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order){
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     * @param order
     * @return
     */
    public Example createExample(Order order){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(order!=null){
            // 订单id
            if(!StringUtils.isEmpty(order.getId())){
                    criteria.andEqualTo("id",order.getId());
            }
            // 数量合计
            if(!StringUtils.isEmpty(order.getTotalNum())){
                    criteria.andEqualTo("totalNum",order.getTotalNum());
            }
            // 金额合计
            if(!StringUtils.isEmpty(order.getTotalMoney())){
                    criteria.andEqualTo("totalMoney",order.getTotalMoney());
            }
            // 优惠金额
            if(!StringUtils.isEmpty(order.getPreMoney())){
                    criteria.andEqualTo("preMoney",order.getPreMoney());
            }
            // 邮费
            if(!StringUtils.isEmpty(order.getPostFee())){
                    criteria.andEqualTo("postFee",order.getPostFee());
            }
            // 实付金额
            if(!StringUtils.isEmpty(order.getPayMoney())){
                    criteria.andEqualTo("payMoney",order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if(!StringUtils.isEmpty(order.getPayType())){
                    criteria.andEqualTo("payType",order.getPayType());
            }
            // 订单创建时间
            if(!StringUtils.isEmpty(order.getCreateTime())){
                    criteria.andEqualTo("createTime",order.getCreateTime());
            }
            // 订单更新时间
            if(!StringUtils.isEmpty(order.getUpdateTime())){
                    criteria.andEqualTo("updateTime",order.getUpdateTime());
            }
            // 付款时间
            if(!StringUtils.isEmpty(order.getPayTime())){
                    criteria.andEqualTo("payTime",order.getPayTime());
            }
            // 发货时间
            if(!StringUtils.isEmpty(order.getConsignTime())){
                    criteria.andEqualTo("consignTime",order.getConsignTime());
            }
            // 交易完成时间
            if(!StringUtils.isEmpty(order.getEndTime())){
                    criteria.andEqualTo("endTime",order.getEndTime());
            }
            // 交易关闭时间
            if(!StringUtils.isEmpty(order.getCloseTime())){
                    criteria.andEqualTo("closeTime",order.getCloseTime());
            }
            // 物流名称
            if(!StringUtils.isEmpty(order.getShippingName())){
                    criteria.andEqualTo("shippingName",order.getShippingName());
            }
            // 物流单号
            if(!StringUtils.isEmpty(order.getShippingCode())){
                    criteria.andEqualTo("shippingCode",order.getShippingCode());
            }
            // 用户名称
            if(!StringUtils.isEmpty(order.getUsername())){
                    criteria.andLike("username","%"+order.getUsername()+"%");
            }
            // 买家留言
            if(!StringUtils.isEmpty(order.getBuyerMessage())){
                    criteria.andEqualTo("buyerMessage",order.getBuyerMessage());
            }
            // 是否评价
            if(!StringUtils.isEmpty(order.getBuyerRate())){
                    criteria.andEqualTo("buyerRate",order.getBuyerRate());
            }
            // 收货人
            if(!StringUtils.isEmpty(order.getReceiverContact())){
                    criteria.andEqualTo("receiverContact",order.getReceiverContact());
            }
            // 收货人手机
            if(!StringUtils.isEmpty(order.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",order.getReceiverMobile());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(order.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(!StringUtils.isEmpty(order.getSourceType())){
                    criteria.andEqualTo("sourceType",order.getSourceType());
            }
            // 交易流水号
            if(!StringUtils.isEmpty(order.getTransactionId())){
                    criteria.andEqualTo("transactionId",order.getTransactionId());
            }
            // 订单状态 
            if(!StringUtils.isEmpty(order.getOrderStatus())){
                    criteria.andEqualTo("orderStatus",order.getOrderStatus());
            }
            // 支付状态 0:未支付 1:已支付
            if(!StringUtils.isEmpty(order.getPayStatus())){
                    criteria.andEqualTo("payStatus",order.getPayStatus());
            }
            // 发货状态 0:未发货 1:已发货 2:已送达
            if(!StringUtils.isEmpty(order.getConsignStatus())){
                    criteria.andEqualTo("consignStatus",order.getConsignStatus());
            }
            // 是否删除
            if(!StringUtils.isEmpty(order.getIsDelete())){
                    criteria.andEqualTo("isDelete",order.getIsDelete());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }
}
