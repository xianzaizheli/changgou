package com.changgou.search;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.Map;


public class SearchResultMapperImpl implements SearchResultMapper {
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
        ArrayList<T> content = new ArrayList<>();
        //如果没有结果则返回为空
        if (searchResponse.getHits() == null || searchResponse.getHits().getTotalHits() <= 0){
            return new AggregatedPageImpl<T>(content);
        }
        for (SearchHit searchHit:searchResponse.getHits()){
            String sourceAsString = searchHit.getSourceAsString();
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            if(null != name){
                StringBuffer stringBuffer = new StringBuffer();
                for(Text text:name.getFragments()){
                    stringBuffer.append(text);
                }
                skuInfo.setName(stringBuffer.toString());
            }
            content.add((T)skuInfo);
        }
        long totalHits = searchResponse.getHits().getTotalHits();
        //4.获取所有聚合函数的结果
        Aggregations aggregations = searchResponse.getAggregations();
        //5.深度分页的ID
        String scrollId = searchResponse.getScrollId();
        return new AggregatedPageImpl<T>(content ,pageable,totalHits ,aggregations,scrollId);
    }
}
