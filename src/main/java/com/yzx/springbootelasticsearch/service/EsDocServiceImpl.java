package com.yzx.springbootelasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.yzx.springbootelasticsearch.pojo.Content;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EsDocServiceImpl implements EsDocService{

    private static String INDEX_NAME="jd_goods";

    @Autowired
    @Qualifier("restClient")
    private RestHighLevelClient client;

    @Autowired
    private EsIndexService indexService;

    @Transactional
    @Override
    public boolean addDoc(Content content) throws IOException {
        //先判断是否存在
        if (indexService.existIndex()){
            IndexRequest request = new IndexRequest(INDEX_NAME);
            //设置超时，不指定id，随机生成
            request.timeout("1s");
            //json放入数据
            request.source(JSON.toJSONString(content), XContentType.JSON);
            //获得响应
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            //判断是否成功创建
            return response.getResult() == IndexResponse.Result.CREATED;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean bulkAddDoc(List<Content> listContent) throws IOException {
        //判断是否存在索引
        if (indexService.existIndex()){
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("10s");
            //存入数据
            for (Content content : listContent) {
                bulkRequest.add(new IndexRequest(INDEX_NAME)
                        .source(JSON.toJSONString(content), XContentType.JSON)
                );
            }
            //执行请求
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            //不失败表示插入成功
            return !bulkResponse.hasFailures();
        }
        return false;
    }


    @Transactional
    @Override
    public boolean deleteDoc(Integer id) throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, ""+id);
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.getResult() == IndexResponse.Result.DELETED;
    }

    @Transactional
    @Override
    public GetResponse getDoc(Integer id) throws IOException {
        GetRequest request = new GetRequest("user_index", ""+id);
        //2. 获取信息
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        return response;
    }

    @Transactional
    @Override
    public boolean updateDoc(Integer id,Content content) throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX_NAME, "" + id);
        request.doc(JSON.toJSONString(content),XContentType.JSON);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response.getResult() == IndexResponse.Result.UPDATED;
    }

    /**
     * @Description: 匹配搜索
     * @Param: [param搜索字段名称, searchWord搜索关键字]
     * @return: org.elasticsearch.search.SearchHits
     * @Author: yzx
     * @Date: 2024/4/10
     */
    @Transactional
    @Override
    public List<Map<String, Object>> searchDoc(String param, String searchWord, Integer from, Integer size) throws IOException {
        //构建搜索类
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();

        //匹配查询
        TermQueryBuilder termQuery = QueryBuilders.termQuery(param, searchWord);

        //设置超时
        searchBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮的字段
        highlightBuilder.field(param);
        //多个高亮显示
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchBuilder.highlighter(highlightBuilder);

        //分页
        if (from <= 0){
            from = 0;
        }
        if (size != 0){
            searchBuilder.from(from);
            searchBuilder.size(size);
        }

        //封装搜索
        searchBuilder.query(termQuery);
        //创建查询请求，并将搜索类放入
        SearchRequest request = new SearchRequest(INDEX_NAME).source(searchBuilder);
        //客户端查询请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //解析结果，第一次getHits()返回了全部的数据，包括版本信息等
        //第二次getHits()返回了数据中的查询对象
        //getSourceAsMap()表示将查询对象转化为map集合，其中键就是属性，值就是属性对应的值
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(SearchHit hit:response.getHits().getHits()){
            //使用新的高亮字段，覆盖旧字段
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //获取全部的高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get(param);
            //开始替换，将旧的title值替换为高亮的值
            if (title != null){
                Text[] fragments = title.getFragments();
                StringBuilder newTitle = new StringBuilder();
                for (Text text:fragments){
                    newTitle.append(text);
                }
                sourceAsMap.put(param,newTitle);
            }
            //再次添加进去
            list.add(sourceAsMap);
        }
        return list;
    }

    @Transactional
    @Override
    public boolean existDoc(Integer id) throws IOException {
        GetRequest request = new GetRequest(INDEX_NAME, "" + id);
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean isExists = client.exists(request, RequestOptions.DEFAULT);
        return isExists;
    }
}
