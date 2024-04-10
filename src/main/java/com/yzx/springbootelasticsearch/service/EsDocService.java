package com.yzx.springbootelasticsearch.service;

import com.yzx.springbootelasticsearch.pojo.Content;
import org.elasticsearch.action.get.GetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface EsDocService {
    //添加单个文档
    boolean addDoc(Content content) throws IOException;
    //批量添加文档
    boolean bulkAddDoc(List<Content> listContent) throws IOException;
    //删除单个文档
    boolean deleteDoc(Integer id) throws IOException;
    //获取文档
    GetResponse getDoc(Integer id) throws IOException;
    //更新文档
    boolean updateDoc(Integer id, Content content) throws IOException;
    //搜索文档
    List<Map<String, Object>> searchDoc(String param, String searchWord, Integer from, Integer size) throws IOException;
    //判断文档是否存在
    boolean existDoc(Integer id) throws IOException;

}
