package com.yzx.springbootelasticsearch;

import com.alibaba.fastjson.JSON;
import com.yzx.springbootelasticsearch.pojo.User;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
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
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
class SpringBootElasticSearchApplicationTests {

    @Autowired
    @Qualifier("restClient")
    private RestHighLevelClient client;

    //=========================================索引操作==============================================
    //创建索引
    @Test
    void createIndex() throws IOException {
        //1、创建索引请求(这里千万要注意，创建的索引都要求小写)
        CreateIndexRequest index = new CreateIndexRequest("test_index");
        //2、客户端执行请求(使用默认的请求参数)，获得请求后的响应
        CreateIndexResponse response = client.indices().create(index, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    //判断索引是否存在
    @Test
    void existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("yzx_index");
        boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(isExists);
    }

    //测试获取索引
    @Test
    void getIndex(){
        IndexRequest request = new IndexRequest("user_index");
        System.out.println(request);
    }

    //测试删除索引
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("jd_goods");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        //判断是否删除成功
        System.out.println(delete.isAcknowledged());
    }


    //=========================================文档操作==============================================
    //添加文档，这里就算多次添加文档，也只会保留最后一个
    @Test
    void addDoc() throws IOException {
        //1. 创建对象
        User user = new User("无情", 20, "外号亮堂堂，有三个绝活");
        //2. 创建请求
        IndexRequest request = new IndexRequest("user_index");
        //3. 指定规则，类似于 PUT /yzx_index/_doc/1
        request.id("8");
        request.timeout("1s");//超时
        //4. 将数据放入请求，XContentType.JSON表示以json数据放入
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //5. 客户端发送请求，获得响应
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        //6. 查看结果
        System.out.println(response);
        System.out.println(response.status());
        System.out.println(response.isFragment());
    }

    //批量插入数据
    @Test
    void bulkAddDoc() throws IOException {
        //1. 创建批量请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("张三", 18, "学学学"));
        userList.add(new User("刘亮", 32, "喜爱阅读"));
        userList.add(new User("氧气", 9, "不折不扣的直男"));
        //2. 批量请求数据
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("user_index")
                            .id(""+(i+5))
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
        }
        //3. 执行请求
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.status());//查看状态

    }

    //判断文档是否存在
    @Test
    void existDoc() throws IOException {
        //1. 获取文档
        GetRequest request = new GetRequest("user_index", "1");
        //2. 不获取返回的 _source的上下文（固定写法）
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        //3. 判断
        boolean isExists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(isExists);
    }

    //获得文档信息
    @Test
    void getDoc() throws IOException {
        //1. 获取文档
        GetRequest request = new GetRequest("user_index", "1");
        //2. 获取信息
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());//字符串形式返回
        System.out.println(response.getSource());//Map形式返回对象
        System.out.println(response);//Map形式返回所有信息
    }

    //更新文档信息
    //返回结果是OK
    @Test
    void updateDoc() throws IOException {
        //1. 获取文档
        UpdateRequest request = new UpdateRequest("user_index", "1");
        //2. 重新创建对象
        User user = new User("张三", 18, "最近变胖了");
        //3. 重新存储
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        //4. 更新，获取响应
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());//查看状态
    }

    //删除文档
    @Test
    void deleteDoc() throws IOException {
        //1. 获取文档
        DeleteRequest request = new DeleteRequest("user_index", "1");
        request.timeout("1s");
        //2. 发送请求
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());// OK
    }

    //文案查询
    // SearchRequest 搜索请求
    // SearchSourceBuilder 条件构造
    // HighlightBuilder 高亮
    // TermQueryBuilder 精确查询
    @Test
    void searchDoc() throws IOException {
        //1. 构建搜索类
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();

        //2. 构建查询条件，使用工具类 QueryBuilders 创建
            //2.1 精准查询
        //TermQueryBuilder termQuery = QueryBuilders.termQuery("name", "刘亮");
            //2.2 匹配查询
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("title", "数据");
            //2.3 高亮---可参考 searchBuilder 的字段部分
        searchBuilder.highlighter(new HighlightBuilder());
            //2.4 分页---可参考 searchBuilder 的字段部分
        searchBuilder.from(0);
        searchBuilder.size(10);
            //2.5 设置超时(毫秒单位)
        searchBuilder.timeout(new TimeValue(60000));

        //3. 封装条件到搜索类中
        searchBuilder.query(matchQuery);

        //4. 创建查询请求，并将搜索类放入
        SearchRequest request = new SearchRequest("jd_goods").source(searchBuilder);

        //5. 客户端查询请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //6. 查看返回结果
        SearchHits hits = response.getHits();
            //6.1 返回的全部数据，冗余
        System.out.println(JSON.toJSONString(hits));
        System.out.println("========================");
            //6.2 获取返回的对象
        for (SearchHit hit:hits.getHits()){
            System.out.println(hit.getSourceAsMap());
        }

    }


}
