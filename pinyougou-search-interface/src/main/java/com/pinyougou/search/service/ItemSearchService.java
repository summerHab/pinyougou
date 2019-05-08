package com.pinyougou.search.service;

import java.util.Map;

/**
 * 搜索
 */

public interface ItemSearchService {

    /**
     * 搜索查询
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);
}
