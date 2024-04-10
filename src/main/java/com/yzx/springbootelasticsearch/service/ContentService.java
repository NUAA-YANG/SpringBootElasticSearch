package com.yzx.springbootelasticsearch.service;


import com.yzx.springbootelasticsearch.pojo.Content;
import com.yzx.springbootelasticsearch.utils.HtmlParseUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {


    @Autowired
    private EsIndexService indexService;

    @Autowired
    private EsDocService docService;

    //调用解析网页方法，将数据存入es
    public boolean parseContent(String keyword) throws IOException {
        //1. 查询数据
        List<Content> list = new HtmlParseUtil().parseJD(keyword);
        //2. 判断索引是否存在，并且插入数据
        if (!indexService.existIndex()){
            indexService.createIndex();
        }
        //3. 插入数据
        return docService.bulkAddDoc(list);
    }

    public List<Map<String, Object>> searchContent(String keyword,Integer from,Integer size) throws IOException {
        return docService.searchDoc("title",keyword,from,size);
    }
}
