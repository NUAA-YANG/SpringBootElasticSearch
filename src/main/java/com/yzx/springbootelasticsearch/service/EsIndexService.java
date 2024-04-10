package com.yzx.springbootelasticsearch.service;

import org.elasticsearch.action.index.IndexRequest;

import java.io.IOException;

public interface EsIndexService {
    //创建索引
    boolean createIndex() throws IOException;

    //判断索引是否存在
    boolean existIndex() throws IOException;

    //获取索引
    IndexRequest getIndex();

    //删除索引
    boolean deleteIndex() throws IOException;

}
