package com.yzx.springbootelasticsearch.controller;

import com.yzx.springbootelasticsearch.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;


    @GetMapping("/parse/{keyword}")
    public boolean parseContent(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{from}/{size}")
    public List<Map<String, Object>> searchContent(@PathVariable("keyword") String keyword,
                                                   @PathVariable("from") Integer from,
                                                   @PathVariable("size") Integer size) throws IOException {
        return contentService.searchContent(keyword,from,size);
    }

}
