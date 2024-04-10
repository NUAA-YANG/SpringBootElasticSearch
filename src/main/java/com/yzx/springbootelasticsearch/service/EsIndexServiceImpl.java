package com.yzx.springbootelasticsearch.service;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class EsIndexServiceImpl implements EsIndexService{

    private static String INDEX_NAME="jd_goods";

    @Autowired
    @Qualifier("restClient")
    private RestHighLevelClient client;

    //返回是否创建成功
    @Transactional
    @Override
    public boolean createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    //返回是否存在
    @Transactional
    @Override
    public boolean existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDEX_NAME);
        boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);
        return isExists;
    }

    //获取索引
    @Transactional
    @Override
    public IndexRequest getIndex() {
        IndexRequest request = new IndexRequest(INDEX_NAME);
        return request;
    }

    //删除索引
    @Transactional
    @Override
    public boolean deleteIndex() throws IOException {
        //如果存在则删除
        if (existIndex()){
            DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
            AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
            return delete.isAcknowledged();
        }
        return false;
    }
}
