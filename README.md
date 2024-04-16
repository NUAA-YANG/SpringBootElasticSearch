# ElasticSearch7.6.2

---

# 一、ElasticSearch概述

---

1. **Elaticsearch**，简称为es，es是一个开源的**高扩展**的**分布式全文检索引擎**
2. 可以近乎**实时的存储**、**检索数据**，其本身扩展性很好，可以扩展到上百台服务器，处理PB级别(大数据时代）的数据
3. `es`也使用`java`开发并使用`Lucene`作为其核心来实现所有索引和搜索的功能
4. 目的**是通过简单的**`RESTful API`来隐藏`Lucene`的复杂性，从而让全文搜索变得简单
5. **使用者**：维基百科，百度百科，全文检索，高亮，搜索推荐



# 二、ElasticSearch安装

---

## 2.1 后端启动

要和`jdk`对应，最低`jdk1.8`起步，我们在`windows`下面搭建

> 目录

![image-20240403164151860](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404031642043.png)

1. `bin` ：启动文件目录
2. `config`：配置文件目录
   - `1og4j2`：日志配置文件
   - `jvm.options`：`java` 虚拟机相关的配置(默认启动占`1g`内存，内容不够需要自己调整)
   - `elasticsearch.yml`：配置文件! 默认`9200`端口，通信`9300`端口
3. `1ib`：相关`jar`包
4. `modules`：功能模块目录
5. `plugins`：插件目录，如`ik`分词器



> 启动

进入`bin`目录，启动`elasticsearch.bat`文件

![image-20240403164958247](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404031650196.png)

访问电脑端`http://localhost:9200/`，能看到如下界面表示启动成功

![image-20240403171451939](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404031714998.png)



## 2.2 可视化界面

> `Elasticsearch-head`安装

1. 安装可视化界面之前需要安装`NodeJS`，参考链接：[NodeJS安装配置](https://blog.csdn.net/qq_39038178/article/details/125403896?ops_request_misc=&request_id=&biz_id=102&utm_term=nodejs%E5%AE%89%E8%A3%85%E9%85%8D%E7%BD%AE&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduweb~default-6-125403896.142^v100^control&spm=1018.2226.3001.4187)
2. 安装`Elasticsearch-head`插件，默认端口`9100`，安装包下载：[GitHub](https://github.com/mobz/elasticsearch-head/archive/master.zip)

```bash
cd E:\Environment\Elasticsearch-head-master
# 安装依赖，这里最好用管理员身份运行下载，可能会存在很多警告，不需要管
npm install
# 启动
npm run start
# 访问
http://localhost:9100/
```

当`Elasticsearch-head-master`文件夹下存在`node_modules`目录时，说明安装成功

![image-20240407113013972](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071130305.png)

3. 访问可能会存在跨域问题(即不同端口，主机，协议)，通过修改`E:\Environment\Elasticsearch7.6.2\config\elasticsearch.yml`文件的方法解决，但先需要关闭`Elasticsearch`和`Elasticsearch-head`

```bash
# 开启跨域
http.cors.enabled: true
# 所有人访问
http.cors.allow-origin: "*"
```

4. 再次启动`Elasticsearch`和`Elasticsearch-head`，进行连接，显示绿色的健康表示连接成功

![image-20240407143932356](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071439740.png)



> `Elasticsearch-head`认识

- 索引 可以看做 “数据库”

- 类型 可以看做 “表”
- 文档 可以看做 “表中的行”

- `Elasticsearch-head`我们只是把它当做可视化数据展示工具，之后所有的查询都在`kibana`中进行，因为不支持`json`格式化，不方便



## 2.3 kibana

`Kibana`是一个针对`ElasticSearch`的开源分析及可视化平台，用来搜索、查看交互存储在`Elasticsearch`索引中的数据。使用`Kibana` ,可以通过各种图表进行高级数据分析及展示

`Kibana`的版本要和`elasticsearch`版本对应，可查看官方链接：[elastic支持一览表](https://www.elastic.co/cn/support/matrix#matrix_compatibility)

例如我下载的`elasticsearch7.6.2`，那么对应下载`kibana7.6.2`，`kibana`下载链接：[kibana下载](https://mirrors.huaweicloud.com/kibana/?C=N&O=D)



> 汉化并启动

下载解压后，进入`config`目录，编辑`kibana.yml`，添加如下命令

```bash
i18n.locale: "zh-CN"
```

其中`kibana.yml`中设置了默认连接本地`elasticsearch`

![image-20240407162607517](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071626891.png)

进入`bin`目录，启动`kibana.bat`文件，可能会出现警告`Plugin "case" is disabled.`，不用理会

![image-20240407152000229](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071520363.png)

访问电脑端`http://localhost:5601/`，能看到如下界面表示启动成功，其中左侧扳手可用来发送请求

![image-20240407152149018](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071521998.png)



# 三、ElasticSearch核心概念

---

| Relational DB      | ElasticSearch          |
| ------------------ | ---------------------- |
| 数据库（database） | 索引  indices          |
| 表（tables）       | types \<慢慢会被弃用!> |
| 行（rows）         | 文档 documents         |
| 字段（columns）    | fields                 |

1. `ElasticSearch`是面向文档搜索，一切都是`json`
2. 索引和搜索数据的最小单位是文档
3. 索引（库）存储了映射类型的字段和其他设置，然后它们被存储到了各个分片上，每个分片可看作==倒排索引==

![image-20240407153825418](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071538031.png)

4. 所谓==倒排索引==是根据内容（标签）查找源（博客`ID`），常规情况下，我们根据源（博客`ID`）查找内容（标签）

![img](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071535072.png)



# 四、IK分词器

---

## 4.1 分词器概述

即把一段中文或者别的划分成一个个的关键字，在搜索时候会把自己的信息进行分词，会把数据库中或者索引库中的数据进行分词，然后进行一一个匹配操作



## 4.2 下载安装

这里也要注意版本的对应，下载链接：[ik分词器GitHub地址](https://github.com/medcl/elasticsearch-analysis-ik/releases)，其中`7.6.2`地址：[7.6.2下载](https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.6.2/elasticsearch-analysis-ik-7.6.2.zip)

在`E:\Environment\ElasticSearch\Elasticsearch7.6.2\plugins`目录下新建`ik`目录，将压缩包解压

![image-20240407160747118](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071608848.png)

重启`elasticsearch`和`kibana`，可以看到`ik`分词器已经被加载

![image-20240407161230990](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071612927.png)



## 4.3 Kibana测试

`ik_smart`：粗糙的拆分

```bash
GET _analyze
{
  "analyzer": "ik_smart",
  "text": "计算机科学与技术学院"
}
```

![image-20240407162805653](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071628681.png)

`ik_max_word`：最细致划分

```bash
GET _analyze
{
  "analyzer": "ik_max_word",
  "text": "计算机科学与技术学院"
}
```

![image-20240407162904850](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071629860.png)



## 4.4 自定义词语

有的时候，拆分出来的词语并不是我们想要的，此时便可以自定义词语

第一步，首先在`E:\Environment\ElasticSearch\Elasticsearch7.6.2\plugins\ik\config`目录下新建一个文本文件，命名为`名称.dic`，例如我命名为`myWord.dic`

第二步，打开自定义的`dic`文件，在其中编写你需要的词语（**千万注意，这里要将文件的编码选择为`UTF-8`，否则无法正确识别**）

![image-20240407165357629](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071655079.png)

第三步，打开目录下的`IKAnalyzer.cfg.xml`文件，将自定义的`dic`文件写入

![image-20240407163925715](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071639078.png)

第四步，重启`es`和`kibana`，可以看到`es`已经重新加载了我们自定义的`dic`文件

![image-20240407164439394](C:\Users\YZX\AppData\Roaming\Typora\typora-user-images\image-20240407164439394.png)

打开`kibana`测试，能够识别我们自定义的词语

![image-20240407165719750](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071657957.png)



# 五、Rest索引操作

---

## 5.1 基本操作

基本操作说明

|      method      |                     url地址                     |          描述          |
| :--------------: | :---------------------------------------------: | :--------------------: |
| PUT（创建,修改） |     localhost:9200/索引名称/类型名称/文档id     | 创建文档（指定文档id） |
|   POST（创建）   |        localhost:9200/索引名称/类型名称         | 创建文档（随机文档id） |
|   POST（修改）   | localhost:9200/索引名称/类型名称/文档id/_update |        修改文档        |
|  DELETE（删除）  |     localhost:9200/索引名称/类型名称/文档id     |        删除文档        |
|   GET（查询）    |     localhost:9200/索引名称/类型名称/文档id     |   查询文档通过文档ID   |
|   POST（查询）   | localhost:9200/索引名称/类型名称/文档id/_search |      查询所有数据      |



## 5.2 数据类型

常见的数据类型

1. 字符串：
   - `text`：==支持分词，全文检索==，==支持模糊、精确查询==，不支持聚合,排序操作，且最大支持的字符长度无限制，适合大字段存储；
   - `keyword`：==不进行分词，直接索引==、==支持模糊、支持精确匹配==，支持聚合、排序操作，最大支持的长度为`32766`个`UTF-8`类型的字符
2. 数值：`long`、`Integer`、`short`、`byte`、`double`、`float`、`half float`、`scaled float`
3. 日期：`date`
4. 布尔：`boolean`
5. 二进制：`binary`



## 5.3 增删改查

> 1. `put`-创建库并且指定字段类型

创建名称为`test2`的库

```bash
PUT /test2
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "age":{
        "type": "long"
      },
      "birthday":{
        "type": "date"
      }
    }
  }
}
```

查看可视化界面，已添加一个库，但没有数据

![image-20240407190542697](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071905827.png)



> 2. `put`-创建库且添加字段

创建名称为`test1`的库，类型为`type1`，`id`为`1`，不写数据类型会默认生成

```bash
PUT /test1/type1/1
{
  "name" : "yzx",
  "sex" : "male",
  "age" : 18
}
```

查看可视化界面，已添加一条数据

![image-20240407172848933](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404071728137.png)

这里要注意，在`es8.x`后，已经不需要填写`type`，默认写`_doc`，所以添加内容还可以写成

```bash
PUT /test3/_doc/1
{
  "name" : "yzx",
  "sex" : "male",
  "birth" : "2000-0306",
  "age" : 24
}
```



> 3. `get`-获得信息

获得名称为`test2`的库信息

```bash
GET test2
```



> 4. `get`-获取系统信息

通过`get _cat/`可查看系统的信息

```bash
GET _cat/indices
GET _cat/aliases
GET _cat/allocation
GET _cat/count
GET _cat/fielddata
GET _cat/health
GET _cat/indices
GET _cat/master
GET _cat/nodeattrs
GET _cat/nodes
GET _cat/pending_tasks
GET _cat/plugins
GET _cat/recovery
GET _cat/repositories
GET _cat/segments
GET _cat/shards
GET _cat/snapshots
GET _cat/tasks
GET _cat/templates
GET _cat/thread_pool
```



> 5. `post`-修改值

可以通过**库名称**和**`id`**直接进行字段修改，其中`"doc"`是固定格式

```bash
POST /test3/_doc/1/_update
{
  "doc":{
    "name":"哔哩哔哩",
    "age":100
  }
}
```



> 6. `delete`-删除索引

```bash
# 删除库
DELETE test1
# 删除数据
DELETE /test3/_doc/2
```



# 六、Rest文档操作

---

在进行查询匹配前，先存储若干条数据

```bash
PUT /party/user/1
{
  "name" : "yzx",
  "sex" : "male",
  "age" : 18,
  "desc": "我热爱学习，我每天学习24小时，好好学习，天天向上",
  "tags": ["java","python","mysql","linux"]
}
```

## 6.1 简单匹配

> 根据`id`获取数据

```bash
GET /party/user/1
```

> 精准匹配查询

比如查询`party`中的`name`为`yzx`的用户

```bash
GET /party/user/_search?q=name:yzx
```



## 6.2 查询匹配

- `match`：匹配（会使用分词器解析（先分析文档，然后进行查询））
- `_source`：过滤字段
- `sort`：排序
- `form`、`size` 分页



> 匹配查询

`match`中的字段为存储的字段，例如查询`desc`包含 锻炼 的数据

```bash
GET /party/user/_search
{
  "query": {
    "match": {
      "desc": "锻炼"
    }
  }
}
```

表示查询到两条数据，且拥有得分（得分越高，表示该条数据更匹配搜索）

![image-20240407200459632](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404072005722.png)

> 字段过滤

还可以利用`"_source"`字段对结果进行过滤，例如我只想展示`"name","desc"`两个字段

```bash
GET /party/user/_search
{
  "query": {
    "match": {
      "desc": "锻炼"
    }
  },
  "_source": ["name","desc"]
}
```

![image-20240407200528676](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404072005008.png)

> 排序

还可以利用`"sort"`字段对结果进行过滤，排序规则有`asc`和`desc`

```bash
GET /party/user/_search
{
  "query": {
    "match": {
      "desc": "锻炼"
    }
  },
  "sort": [
    {
      "age": {
        "order": "desc"
      }
    }
  ]
}
```

> 分页

通过`  "from"`和`"size"`两个字段决定，前者表示从第几个数据开始，后者表示返回多少条数据。数据下标从`0`开始

```bash
GET /party/user/_search
{
  "query": {
    "match": {
      "desc": "锻炼"
    }
  },
  "from": 0,
  "size": 2
}
```



## 6.3 精准查询

正如上文所说

- `match`会使用分词器进行解析，可实现模糊匹配

- `term`为直接查询精确文本，必须查询的文本全部包含，而且不可拆分



举例说明，当我们使用`match`模糊匹配时，可以查到相关拥有该词语的数据

![image-20240408161233362](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404081612647.png)

但我们使用`term`精准匹配时，就无法查询到相关数据，因为【锻炼】为一个整体，不可拆分

![image-20240408161208212](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404081612733.png)





## 6.4 多条件查询bool

- `must` 相当于 `and`
- `should` 相当于 `or`
- `must_not` 相当于 `not`
- `filter` 过滤

> `must`多条件

匹配的条件全部要满足

```bash
GET /party/user/_search
{
  "query": {
  "bool": {
    "must": [
      {
        "match": {
          "sex": "male"
        }
      },
      {
        "match": {
          "tags": "java"
        }
      }
    ]
  }
  }
}
```



> `filter`过滤器

用来限制数据的大小范围，比如限制年龄在`15-23`

```bash
GET /party/user/_search
{
  "query": {
  "bool": {
    "must": [
      {
        "match": {
          "sex": "male"
        }
      }
    ],
    "filter": {
      "range":{
        "age":{
          "gt": 15,
          "lt": 23
        }
      }
    }
  }
  }
}
```



## 6.5 数组匹配

这里要注意几点：

1. 不能和其他字段一起使用
2. 关键字之间利用空格分开
3. 会使用分词器查询

==例如，下面的关键字会被拆分为【年、龄、开、心、大】==

```bash
GET /party/user/_search
{
  "query":{
    "match":{
      "desc":"年龄 开心 大"
    }
  }
}
```



## 6.6 高亮查询

高亮查询利用`"highlight"`字段实现，可自定义前缀或后缀

当不写`"pre_tags"`和`"post_tags"`时，为默认样式

```bash
GET /party/user/_search
{
  "query": {
    "match": {
      "desc":"热爱"
    }
  }
  ,
  "highlight": {
    "pre_tags": "<p class='key' style='color:red'>",
    "post_tags": "</p>", 
    "fields": {
      "desc": {}
    }
  }
}
```

![image-20240408162431501](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404081624552.png)





# 七、整合SpringBoot

---

## 7.1 基本配置

1. 创建项目，在`NoSQL`中选择`es`依赖

![image-20240408164137377](https://cdn.jsdelivr.net/gh/NUAA-YANG/TyporaPicture@main//img/202404081641963.png)

2. 在`pom.xml`导入`elasticsearch`依赖，这里千万要注意，得保证和本地安装的版本一致

```xml
<!--填写和自己本地版本匹配的elasticsearch-->
<properties>
    <java.version>1.8</java.version>
    <elasticsearch.version>7.6.2</elasticsearch.version>
    <spring-boot.version>2.6.13</spring-boot.version>
</properties>

<!--用于将对象转化为json存储-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.70</version>
</dependency>
```

3. 创建配置类，新建`config\ESClientConfig.java`，用来实现操作

```java
@Configuration
public class ESClientConfig {
    @Bean
    public RestHighLevelClient restClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1",9200,"http"))
        );
        return client;
    }
}
```





## 7.2 索引操作

测试类导入即可使用，其中`@Qualifier`中写的是我们自己的配置类

```java
@Autowired
@Qualifier("restClient")
private RestHighLevelClient client;
```

索引的操作，都是以`_IndexRequest`而命名

> 创建索引

```java
@Test
void createIndex() throws IOException {
    //1、创建索引请求(这里千万要注意，创建的索引都要求小写)
    CreateIndexRequest index = new CreateIndexRequest("yzx_index");
    //2、客户端执行请求(使用默认的请求参数)，获得请求后的响应
    CreateIndexResponse response = client.indices().create(index, RequestOptions.DEFAULT);
    System.out.println(response);
}
```

> 判断索引是否存在

```java
@Test
void existIndex() throws IOException {
    GetIndexRequest request = new GetIndexRequest("yzx_index");
    boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);
    System.out.println(isExists);
}
```

> 获取索引

```java
@Test
void getIndex() throws IOException {
    IndexRequest request = new IndexRequest("yxx_index");
    System.out.println(request);
}
```

> 删除索引

```java
@Test
void deleteIndex() throws IOException {
    DeleteIndexRequest request = new DeleteIndexRequest("yzx_index");
    AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
    //判断是否删除成功
    System.out.println(delete.isAcknowledged());
}
```



## 7.3 文档的操作

### 7.3.1 添加单个文档

---

单个文档的添加，千万要注意，是`JSON.toJSONString()`，而不是`JSON.toJSON()`

```java
//添加文档
@Test
void addDoc() throws IOException {
    //1. 创建对象
    User user = new User("张三", 18, "热爱跑步，喜欢打球");
    //2. 创建请求
    IndexRequest request = new IndexRequest("user_index");
    //3. 指定规则，类似于 PUT /yzx_index/_doc/1
    request.id("1");
    request.timeout("1s");//超时
    //4. 将数据放入请求
    request.source(JSON.toJSONString(user), XContentType.JSON);
    //5. 客户端发送请求，获得响应
    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
    //6. 查看结果
    System.out.println(response);
    System.out.println(response.status());
}
```

### 7.3.2 批量添加文档

---

当数据未指定`id`时，系统会随机生成一个`id`

```java
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
            .id(""+(i+1))
            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
        );
    }
    //3. 执行请求
    BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    System.out.println(bulkResponse.status());//查看状态

}
```

### 7.3.3 判断文档是否存在

---

```java
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
```

### 7.3.4 获得文档

---

```java
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
```

### 7.3.5 更新文档

---

```java
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
```

### 7.3.6 删除文档

---

```java
@Test
void deleteDoc() throws IOException {
    //1. 获取文档
    DeleteRequest request = new DeleteRequest("user_index", "1");
    request.timeout("1s");
    //2. 发送请求
    DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
    System.out.println(response.status());// OK
}
```

### 7.3.7 文档查询

---

```java
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
    MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("desc", "亮男");
    //2.3 高亮---可参考 searchBuilder 的字段部分
    searchBuilder.highlighter(new HighlightBuilder());
    //2.4 分页---可参考 searchBuilder 的字段部分
    searchBuilder.from(0);
    searchBuilder.size(5);
    //2.5 设置超时(毫秒单位)
    searchBuilder.timeout(new TimeValue(60000));

    //3. 封装条件到搜索类中
    searchBuilder.query(termQuery);

    //4. 创建查询请求，并将搜索类放入
    SearchRequest request = new SearchRequest().source(searchBuilder);

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
```

