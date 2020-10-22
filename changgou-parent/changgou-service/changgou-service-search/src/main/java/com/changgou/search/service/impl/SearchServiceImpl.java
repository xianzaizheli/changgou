package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.GoodsFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.SearchResultMapperImpl;
import com.changgou.search.dao.SearchRepositories;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import io.swagger.models.auth.In;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchRepositories searchRepositories;
    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String,String> map) {
        //1.判断是否有值吧
        String keywords = "";
        if(null != map && !StringUtils.isEmpty(map.get("keywords"))){
            //1.获取到传来对象的关键字对象
            keywords = map.get("keywords");
        }else {
            //2.如果没有关键字，那么就把热门的品牌当成关键字给它
            keywords = "华为";
        }
        //3.新建一个查找条件语句
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //terms  指定分组的一个别名
        //field 指定要分组的字段名
        // size 指定查询结果的数量，你去重只用10条数据不太科学，一般用一千条或者是一万条会比较好，看具体需求，太多太少都不好
        // 对品牌名称进行分组查询
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName").size(1000));

        // 对分类名称进行分组查询
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("categoryGroup").field("categoryName").size(1000));

        //由于规格返回有重复的，那么要进行去重，使用Map<String,Set<String>>来存储
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("specGroup").field("spec.keyword").size(1000));

        //设置主关键字查询
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords , "name","brandName","categoryName"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //获取前端品牌信息,这里只会这些对应的内容过滤出来
        if(null != map && !StringUtils.isEmpty(map.get("brandName"))){
            boolQueryBuilder.filter().add(QueryBuilders.termQuery("brandName",map.get("brandName")));
        }

        if(null != map &&!StringUtils.isEmpty(map.get("categoryName"))){
            boolQueryBuilder.filter().add(QueryBuilders.termQuery("categoryName",map.get("categoryName")));
        }

        //规格过滤
        if(null != map){
            for (String key: map.keySet()){
                if(key.startsWith("spec_")){
                    boolQueryBuilder.filter().add(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",map.get(key)));
                }
            }
        }

        //价格过滤
        if(null != map && !StringUtils.isEmpty(map.get("price"))){
            String s = map.get("price").replaceAll("元", "").replaceAll("以上", "");
            String[] split = s.split("-");
            if(null != split){
                if(split.length == 2){
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(Long.parseLong(split[0]),true).to(Long.parseLong(split[1]),true));
                }else {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(Long.parseLong(split[0])));
                }
            }
        }
        //设置过滤查询
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

        //分页处理
        Integer pageNum = 1;
        if(null != map && !StringUtils.isEmpty(map.get("pageNum"))){
            try {
                pageNum = Integer.parseInt(map.get("pageNum"));
            }catch (NumberFormatException e){
                e.printStackTrace();
                pageNum =1;
            }
        }
        Integer pageSize = 20;
        if(null != map && !StringUtils.isEmpty(map.get("pageSize"))){
            try {
                pageNum = Integer.parseInt(map.get("pageSize"));
            }catch (NumberFormatException e){
                e.printStackTrace();
                pageNum =20;
            }
        }
        //设置分页查询
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1 ,pageSize));

        //设置排序
        if(null != map){
            if(!StringUtils.isEmpty(map.get("sortRule")) && !StringUtils.isEmpty("sortField")){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(map.get("sortField")).order(map.get("sortRule").equalsIgnoreCase("DESC")? SortOrder.DESC:SortOrder.ASC));
            }
        }

        //设置高亮属性
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        //设置高亮前缀和后缀
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));

        //构建真正的查询对象
        NativeSearchQuery build = nativeSearchQueryBuilder.build();
        //4.执行一个查询语句
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(build, SkuInfo.class , new SearchResultMapperImpl());

        //获取到对应品牌分组的内容
        StringTerms skuCategoryGroup = (StringTerms)skuInfos.getAggregation("skuBrandGroup");
        //获取到对应分类分组的内容
        StringTerms categoryGroup = (StringTerms)skuInfos.getAggregation("categoryGroup");

        //获取规格分组的内容
        StringTerms specGroup = (StringTerms)skuInfos.getAggregation("specGroup");

        Map<String, Set<String>> stringSetMap = getSpecMap(specGroup);

        ArrayList<String> list = getBrandNameGroup(skuCategoryGroup);
        ArrayList<String> categoryNameGroup = getCategoryNameGroup(categoryGroup);

        //5.新建一个对象用来存储返回的结果
        Map<Object, Object> objectObjectHashMap = new HashMap<>();

        objectObjectHashMap.put("specList",stringSetMap);
        objectObjectHashMap.put("brandList",list);
        objectObjectHashMap.put("categoryNameList",categoryNameGroup);
        //查询结果
        objectObjectHashMap.put("content",skuInfos.getContent());
        //总页数
        objectObjectHashMap.put("totalPage",skuInfos.getTotalPages());
        //总共有多少元素，这里指的是全部数据，不是查询出来的总数据
        objectObjectHashMap.put("totalElement",skuInfos.getTotalElements());

        objectObjectHashMap.put("pageNum",pageNum);
        objectObjectHashMap.put("pageSize",pageSize);
        return objectObjectHashMap;
    }

    /**
     * 获取规格分组的内容
     * @param specGroup
     * @return
     */
    private Map<String, Set<String>> getSpecMap(StringTerms specGroup) {
        Map<String, Set<String>> specMap = new HashMap<>();
        HashSet<String> specList = new HashSet<>();
        if(specGroup != null){
            for (StringTerms.Bucket bucket : specGroup.getBuckets()){
                specList.add(bucket.getKeyAsString());
            }
        }
        for(String specJson : specList){
            Map<String,String> map = JSON.parseObject(specJson, Map.class);
            for (Map.Entry<String,String> entry : map.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> specValues = specMap.get(key);
                if(specValues == null){
                    specValues = new HashSet<>();
                }
                specValues.add(value);
                specMap.put(key,specValues);
            }
        }
        return specMap;
    }

    /**
     * 获取品牌分组的内容
     * @param skuBrandGroup
     * @return
     */
    private ArrayList<String> getBrandNameGroup(StringTerms skuBrandGroup) {
        ArrayList<String> list = new ArrayList<>();
        if(null != skuBrandGroup){
            for (StringTerms.Bucket bucket :skuBrandGroup.getBuckets()){
                String keyAsString = bucket.getKeyAsString();//获取分组的值
                list.add(keyAsString);
            }
        }

        return list;
    }

    /**
     * 获取分类的分组
     * @param skuCategoryGroup
     * @return
     */
    private ArrayList<String> getCategoryNameGroup(StringTerms skuCategoryGroup){
        ArrayList<String> list = new ArrayList<>();
        if(null != skuCategoryGroup){
            for (StringTerms.Bucket bucket: skuCategoryGroup.getBuckets()){
                String keyAsString = bucket.getKeyAsString();
                list.add(keyAsString);
            }
        }
        return list;
    }

    @Override
    public void importSku() {
        Result<List<Sku>> byStatus = goodsFeign.findByStatus("1");
        List<Sku> data = byStatus.getData();
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(data), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos){
            String spec = skuInfo.getSpec();
            Map<String,Object> objects = JSON.parseObject(spec);
            skuInfo.setSpecMap(objects);
        }
        searchRepositories.saveAll(skuInfos);
    }
}
