package com.example.ajie_es.service;


import com.example.ajie_es.pojo.SuperSearch;
import lombok.SneakyThrows;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //    普通分页搜索
    public List<Map<String,Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        //条件搜索
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("lawmas");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);
        //精确值查询
//        FuzzyQueryBuilder describe = QueryBuilders.fuzzyQuery("describe", keyword);
//        TermQueryBuilder describe = QueryBuilders.termQuery("tittle",keyword);
//        QueryStringQueryBuilder describe = new QueryStringQueryBuilder("\""+keyword+"\"");
//        SimpleQueryStringBuilder describe = new SimpleQueryStringBuilder(keyword);
        MatchQueryBuilder describe = QueryBuilders.matchQuery("title",keyword);
        boolBuilder.must(describe);
        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

//        System.out.println("searchResponse = " + searchResponse);

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
//            System.out.println("documentFields = " + documentFields);
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    //    标题高亮搜索
    public List<Map<String,Object>> searchPage2(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("lawmas");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);
        MatchQueryBuilder describe = QueryBuilders.matchQuery("title",keyword);
        boolBuilder.must(describe);
        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        sourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits())
        {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field= highlightFields.get("title");
            if(field!= null){
                Text[] fragments = field.fragments();
                String n_field = "";
                for (Text fragment : fragments) {
                    n_field += fragment;
                }
                //高亮标题覆盖原标题
                sourceAsMap.put("title",n_field);
            }
            list.add(hit.getSourceAsMap());
        }

        return list;

    }

    //    文本搜索---已废弃
    public List<Map<String,Object>> lawtxt_search(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        //条件搜索
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("lawtext");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);
        //精确值查询
//        FuzzyQueryBuilder describe = QueryBuilders.fuzzyQuery("describe", keyword);
//        TermQueryBuilder describe = QueryBuilders.termQuery("txt",keyword);
//        QueryStringQueryBuilder describe = new QueryStringQueryBuilder("\""+keyword+"\"");
//        SimpleQueryStringBuilder describe = new SimpleQueryStringBuilder(keyword);
        MatchQueryBuilder describe = QueryBuilders.matchQuery("txt",keyword);

        boolBuilder.must(describe);
        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("searchResponse.getHits().getHits() = " + searchResponse.getHits().getHits());

//        System.out.println("searchResponse = " + searchResponse);

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
//            System.out.println("documentFields = " + documentFields);
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }


    //    文本+标题高亮搜索---添加权重排序
//    public Map lawtxt_search_HLight(String keyword, int pageNo, int pageSize) throws IOException
    public Map lawtxt_search_HLight(String keyword, int pageNo, int pageSize) throws IOException
    {
        if (pageNo<=1){
            pageNo = 1;
        }


        //建立request，并确定将request指向“lawmas”索引
        SearchRequest searchRequest = new SearchRequest("law_txt");



        //建立sourcebuilder 用来储存页码参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);


        //建立boolbuilder
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        // MatchQueryBuilder 进行关键字搜索，分别对txt和tittle进行匹配搜索
        MatchQueryBuilder describe2 = QueryBuilders.matchQuery("part",keyword);
        MatchQueryBuilder describe = QueryBuilders.matchQuery("title",keyword);

        //boolBuilder进行判断，此处设置为 txt文本必须包含keyword,title可以不包含keyword
        boolBuilder.must(describe2)
                .should(describe);

        //加入sourceBuilder准备
        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置txt和title高亮
        highlightBuilder.field("txt");
        highlightBuilder.field("title");

//        System.out.println("highlightBuilder = " + highlightBuilder);

        //        highlightBuilder.field("title");
//        highlightBuilder.requireFieldMatch(false);
        //高亮的HTML标签
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
//        highlightBuilder.fragmentSize(1); //最大高亮分片数
//        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段

        //同样加入sourceBuilder准备发送
        sourceBuilder.highlighter(highlightBuilder);


//        解除搜索限制，允许返回真实hit
        sourceBuilder.trackTotalHits(true);

        //用Request进行发送
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

//        System.out.println("response = " + response);
//        System.out.println("response = " + response);
//        System.out.println("response.getHits().getHits() = " + response.getHits().getHits());



        //获取response中的数据
        TotalHits totalHits = response.getHits().getTotalHits();
////        System.out.println("totalHits = " + totalHits);
//
        List<Map<String, Object>> list = new ArrayList<>();
//      //遍历response中的数据
        for (SearchHit hit : response.getHits().getHits())
        {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //将高亮字段打入检索结果中
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field= highlightFields.get("part");
            if(field!= null){
                Text[] fragments = field.fragments();
                String n_field = "";
                for (Text fragment : fragments) {
                    n_field += fragment;
                }
                //高亮标题覆盖原标题
//                n_field = n_field.substring(0,50);
//                System.out.println(n_field);
                sourceAsMap.put("part",n_field);
            }
//
            HighlightField htitle = highlightFields.get("title");
            if(htitle!= null)
            {
                Text[] fragments = htitle.fragments();
                String htt = "";
                for (Text fragment : fragments)
                {
                    htt += fragment;

                }
                //高亮标题覆盖原标题
//                n_field = n_field.substring(0,50);
//                System.out.println(htt);
                sourceAsMap.put("title",htt);
            }
//
            list.add(hit.getSourceAsMap());
        }
//      将数据整合为一个list写入map中
        Map dict = new HashMap();
        dict.put("number",totalHits.value);
        dict.put("mas",list);



        return dict;
    }

    //    类型搜索---keyword
    public Map  keywords_search(String keyword) throws IOException {

        //建立request，并确定将request指向“lawmas”索引
        SearchRequest searchRequest = new SearchRequest("law_txt");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        if (keyword.length() == 0){
            System.out.println("Ture");
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            sourceBuilder.query(matchAllQueryBuilder);
            sourceBuilder.size(0);

        }

        else {
            System.out.println("Flase");

            //建立boolbuilder
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

            // MatchQueryBuilder 进行关键字搜索，分别对txt和tittle进行匹配搜索
            MatchQueryBuilder describe2 = QueryBuilders.matchQuery("part",keyword);
            MatchQueryBuilder describe = QueryBuilders.matchQuery("title",keyword);

            //boolBuilder进行判断，此处设置为 txt文本必须包含keyword,title可以不包含keyword
            boolBuilder.must(describe2)
                    .should(describe);

            //加入sourceBuilder准备
            sourceBuilder.query(boolBuilder);
            sourceBuilder.size(0);

        }



        //        聚合搜索，查询关键字数量
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("Keyword").field("Keyword");
        aggregationBuilder.size(30);
        sourceBuilder.aggregation(aggregationBuilder);

        //        解除搜索限制，允许返回真实hit
        sourceBuilder.trackTotalHits(true);


        //        发送请求
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //        聚合搜索数据处理
        Aggregations aggregations = response.getAggregations();
        ParsedStringTerms parsedStringTerms = aggregations.get("Keyword");
        List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
        Map abuoutWords = new HashMap();
        for (Terms.Bucket bucket : buckets) {
            //key的数据
            String key = bucket.getKey().toString();
            long docCount = bucket.getDocCount();
//            System.out.println(key + ":" + docCount );
            abuoutWords.put(key,docCount);
        }

        return abuoutWords;

    }


//    HTML正文文本获取，根据id查询
    @SneakyThrows
    public Map HTML_searchByid(String id){
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        //建立request，并确定将request指向“lawmas”索引
        SearchRequest searchRequest = new SearchRequest("html_txt");
        GetRequest getRequest = new GetRequest("html_txt",id);
        GetResponse documentFields = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
        return documentFields.getSource();
    }


    public Map type_search(String keyword, int pageNo, int pageSize) throws IOException {

        SearchRequest searchRequest = new SearchRequest("law_txt");
        //建立sourcebuilder 用来储存页码参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("txt_type", keyword);

        boolBuilder.must(termQueryBuilder);


        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        sourceBuilder.trackTotalHits(true);

        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        TotalHits totalHits = response.getHits().getTotalHits();

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits().getHits()) {
//            System.out.println("documentFields = " + documentFields);
            list.add(documentFields.getSourceAsMap());
        }

        Map dict = new HashMap();
        dict.put("number",totalHits.value);
        dict.put("mas",list);

        return dict;
    }

    public  Map showSearch(SuperSearch superSearch) throws IOException {

        System.out.println(superSearch.getSearchSize());


        if (superSearch.getSearchPage()<=1){
            superSearch.setSearchPage(1);
        }

        if (superSearch.getSearchSize()<=1){
            superSearch.setSearchSize(1);
        }


        //建立boolbuilder
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        //建立request，并确定将request指向“lawmas”索引
        SearchRequest searchRequest = new SearchRequest("law_txt");

        //建立sourcebuilder 用来储存页码参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(superSearch.getSearchPage()-1);
        sourceBuilder.size(superSearch.getSearchSize());


        // MatchQueryBuilder 进行关键字搜索。
        if (superSearch.getPart()!=null){
            MatchQueryBuilder describe1 = QueryBuilders.matchQuery("part",superSearch.getPart());
            boolBuilder.must(describe1);
        }
        if (superSearch.getTitle()!=null){
            MatchQueryBuilder describe2 = QueryBuilders.matchQuery("title",superSearch.getTitle());
            boolBuilder.must(describe2);
        }

        if (superSearch.getKeyword()!=null){
            MatchQueryBuilder keyword = QueryBuilders.matchQuery("Keyword",superSearch.getKeyword());
            boolBuilder.must(keyword);
        }

        if (superSearch.getTxt_number()!=null){
            TermQueryBuilder describe3 = QueryBuilders.termQuery("txt_number",superSearch.getTxt_number());
            boolBuilder.must(describe3);
        }

        if (superSearch.getStartTime()!=null && superSearch.getEndTime()!=null){
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("pushTime")
                    .from(superSearch.getStartTime()).to(superSearch.getEndTime());
            boolBuilder.must(rangequerybuilder);
        }

        if (superSearch.getTxt_place()!=null){
            MatchQueryBuilder describe4 = QueryBuilders.matchQuery("txt_place",superSearch.getTxt_place());
            boolBuilder.must(describe4);
        }

        if (superSearch.getPeople()!=null){
            MatchQueryBuilder describe5 = QueryBuilders.matchQuery("people",superSearch.getPeople());
            boolBuilder.must(describe5);
        }

        if (superSearch.getWenshuAu()!=null){
            TermQueryBuilder describe6 = QueryBuilders.termQuery("wenshuAu",superSearch.getWenshuAu());
            boolBuilder.must(describe6);
        }



        //boolBuilder进行判断，此处设置为 txt文本必须包含keyword,title可以不包含keyword


        //加入sourceBuilder准备
        sourceBuilder.query(boolBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置txt和title高亮
        highlightBuilder.field("part");
        highlightBuilder.field("title");

//        System.out.println("highlightBuilder = " + highlightBuilder);

        //        highlightBuilder.field("title");
//        highlightBuilder.requireFieldMatch(false);
        //高亮的HTML标签
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
//        highlightBuilder.fragmentSize(1); //最大高亮分片数
//        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段

        //同样加入sourceBuilder准备发送
        sourceBuilder.highlighter(highlightBuilder);


//        解除搜索限制，允许返回真实hit
        sourceBuilder.trackTotalHits(true);

        //用Request进行发送
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

//        System.out.println("response = " + response);
//        System.out.println("response = " + response);
//        System.out.println("response.getHits().getHits() = " + response.getHits().getHits());



        //获取response中的数据
        TotalHits totalHits = response.getHits().getTotalHits();
////        System.out.println("totalHits = " + totalHits);
//
        List<Map<String, Object>> list = new ArrayList<>();
//      //遍历response中的数据
        for (SearchHit hit : response.getHits().getHits())
        {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //将高亮字段打入检索结果中
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field= highlightFields.get("part");
            if(field!= null){
                Text[] fragments = field.fragments();
                String n_field = "";
                for (Text fragment : fragments) {
                    n_field += fragment;
                }
                //高亮标题覆盖原标题
//                n_field = n_field.substring(0,50);
//                System.out.println(n_field);
                sourceAsMap.put("part",n_field);
            }
//
            HighlightField htitle = highlightFields.get("title");
            if(htitle!= null)
            {
                Text[] fragments = htitle.fragments();
                String htt = "";
                for (Text fragment : fragments)
                {
                    htt += fragment;

                }
                //高亮标题覆盖原标题
//                n_field = n_field.substring(0,50);
//                System.out.println(htt);
                sourceAsMap.put("title",htt);
            }
//
            list.add(hit.getSourceAsMap());
        }
//      将数据整合为一个list写入map中
        Map dict = new HashMap();
        dict.put("number",totalHits.value);
        dict.put("mas",list);



        return dict;

    }

    //    类型搜索---keyword
    public Map  type_total(String keyword) throws IOException {

        //建立request，并确定将request指向“lawmas”索引
        SearchRequest searchRequest = new SearchRequest("law_txt");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        if (keyword.length() == 0){
            System.out.println("Ture");
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            sourceBuilder.size(0);
            sourceBuilder.query(matchAllQueryBuilder);
        }

        else {
            System.out.println("Flase");

            //建立boolbuilder
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

            // MatchQueryBuilder 进行关键字搜索，分别对txt和tittle进行匹配搜索
            MatchQueryBuilder describe2 = QueryBuilders.matchQuery("txt_type",keyword);

            //boolBuilder进行判断，此处设置为 txt文本必须包含keyword,title可以不包含keyword
            boolBuilder.must(describe2);

            //加入sourceBuilder准备
            sourceBuilder.query(boolBuilder);
            sourceBuilder.size(0);

        }



        //        聚合搜索，查询关键字数量
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("txt_type").field("txt_type");
        aggregationBuilder.size(30);
        sourceBuilder.aggregation(aggregationBuilder);

        //        解除搜索限制，允许返回真实hit
        sourceBuilder.trackTotalHits(true);


        //        发送请求
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //        聚合搜索数据处理
        Aggregations aggregations = response.getAggregations();
        ParsedStringTerms parsedStringTerms = aggregations.get("txt_type");
        List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
        Map abuoutWords = new HashMap();
        for (Terms.Bucket bucket : buckets) {
            //key的数据
            String key = bucket.getKey().toString();
            long docCount = bucket.getDocCount();
//            System.out.println(key + ":" + docCount );
            abuoutWords.put(key,docCount);
        }

        return abuoutWords;

    }


}

