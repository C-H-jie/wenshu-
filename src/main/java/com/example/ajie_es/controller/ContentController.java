package com.example.ajie_es.controller;


import com.example.ajie_es.pojo.Sentence;
import com.example.ajie_es.service.ContentService;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.config.EnableReactiveElasticsearchAuditing;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    //id搜索，一般只做调试使用
    @GetMapping("/apiq={keyword}/{No}/{pageSize}")
    public List<Map<String,Object>> search (@PathVariable("keyword") String keyword,
                                            @PathVariable("No") int No,
                                            @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.searchPage(keyword,No,pageSize);

    }
//    //类别搜索，用于首页构建
//    @CrossOrigin
//    @GetMapping("/type_search={keyword}&No={No}&pageSize={pageSize}")
//    public List<Map<String,Object>> search5 (@PathVariable("keyword") String keyword,
//                                             @PathVariable("No") int No,
//                                             @PathVariable("pageSize") int pageSize) throws IOException {
//
//        return contentService.type_search_datesort(keyword,No,pageSize);
//    }
//

    //  关键字搜索，只搜索标题，不具有高亮功能，已经基本废弃
    @CrossOrigin
    @GetMapping("/search={keyword}&No={No}&pageSize={pageSize}")
    public List<Map<String,Object>> search2 (@PathVariable("keyword") String keyword,
                                             @PathVariable("No") int No,
                                             @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.searchPage2(keyword,No,pageSize);
    }


    // 文本搜索，不具有高亮功能
    @GetMapping("/txtsearch={keyword}&No={No}&pageSize={pageSize}")
    public List<Map<String,Object>> search3 (@PathVariable("keyword") String keyword,
                                             @PathVariable("No") int No,
                                             @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.lawtxt_search(keyword,No,pageSize);
    }


    //  高亮文本搜索，目前的主要接口之一
    @CrossOrigin
    @GetMapping("/htxtsearch={keyword}&No={No}&pageSize={pageSize}")
    public Map search4 (@PathVariable("keyword") String keyword,
                        @PathVariable("No") int No,
                        @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.lawtxt_search_HLight(keyword,No,pageSize);
    }

    @PostMapping("/htxtsearch")
    public Map post_search4 (@RequestParam("keyword") String keyword,
                             @RequestParam("No") int No,
                             @RequestParam("pageSize") int pageSize) throws IOException {

        return contentService.lawtxt_search_HLight(keyword,No,pageSize);
    }


    @PostMapping("/html_txt")
    public Map post_Html_txt(@RequestParam("id") String id)
    {
        return contentService.HTML_searchByid(id);
    }




}
