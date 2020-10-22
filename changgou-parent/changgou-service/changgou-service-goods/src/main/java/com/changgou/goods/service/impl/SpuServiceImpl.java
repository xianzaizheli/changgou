package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.IdWorker;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.jackson.EurekaXmlJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 批量下架
     * @param supIds
     */
    @Override
    public void pullMany(Long[] supIds) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(supIds));
        //已经上架
        criteria.andEqualTo("isMarketable","1");
        //未删除
        criteria.andEqualTo("isDelete","0");
        //已审核
        criteria.andEqualTo("status","1");

        spuMapper.updateByExampleSelective(spu , example);
    }

    @Override
    public void putMany(Long[] spuIds) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        //批量修改
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(spuIds));
        //不为删除
        criteria.andEqualTo("isDelete","0");
        //为审核
        criteria.andEqualTo("status","1");
        //已经下架的
        criteria.andEqualTo("isMarketable","0");
        spuMapper.updateByExampleSelective(spu ,example);
    }

    @Override
    public void put(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if("1".equalsIgnoreCase(spu.getIsDelete())){
            throw new RuntimeException("商品已删除");
        }
        if("0".equalsIgnoreCase(spu.getIsMarketable())){
            throw new RuntimeException("商品未审核通过");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);

    }

    @Override
    public void pull(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if("1".equalsIgnoreCase(spu.getIsDelete())){
            throw new RuntimeException("该商品已删除");
        }
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品审核
     * @param spuId
     */
    @Override
    public void audit(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("该商品已经删除");
        }
        spu.setIsMarketable("1");
        spu.setStatus("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 查找商品
     * @param spuId
     * @return
     */
    @Override
    public Goods findGoodsById(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> select = skuMapper.select(sku);
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkus(select);
        return goods;
    }

    /**
     * 新增/修改商品
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        //增加spu
        Spu spu = goods.getSpu();

        //这里做一个判断，新增的话一般都是没有id的，而修改的话会有，所以可以做一个判定
        if(StringUtils.isEmpty(spu.getId())){
            spu.setId(String.valueOf(new IdWorker(0,0).nextId()));
            spuMapper.insertSelective(spu);
        }else {
            spuMapper.updateByPrimaryKeySelective(spu);
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.deleteByPrimaryKey(sku);
        }



        //增加sku
        Date date = new Date();
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //获取Sku集合
        List<Sku> skus = goods.getSkus();
        //循环将数据加入到数据库
        for (Sku sku : skus){
            //构建SKU名称，采用SPU+规格值组装
            //做一个前置判断
            if(StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }
            //获取sku代表的名称
            String name = spu.getName();
            //获取规格
            Map<String , String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (Map.Entry<String ,String> map : specMap.entrySet()) {
                name = name + map.getValue();
            }
            sku.setName(name);
            //Id
            sku.setId(String.valueOf(new IdWorker(0,0).nextId()));
            //spuId
            sku.setSpuId(spu.getId());
            //类目id
            sku.setCategoryId(spu.getCategory3Id());
            //类目名称
            sku.setCategoryName(category.getName());
            //品牌名称
            sku.setBrandName(brand.getName());
            //创建时间
            sku.setCreateTime(date);
            //修改时间
            sku.setUpdateTime(date);

            skuMapper.insertSelective(sku);
        }
    }

    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
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
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
