package com.changgou.search.service;

import java.util.Map;

public interface SearchService {

    Map search(Map<String,String> map);

    void importSku();
}
