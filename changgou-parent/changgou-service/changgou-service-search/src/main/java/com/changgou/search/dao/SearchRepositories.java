package com.changgou.search.dao;


import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchRepositories extends ElasticsearchRepository<SkuInfo ,String> {
}
