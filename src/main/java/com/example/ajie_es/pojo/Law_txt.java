package com.example.ajie_es.pojo;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(indexName = "law_txt")
public class Law_txt {

//    s1 标题
    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    String title;

//    s5 id
    @Field(type = FieldType.Keyword)
    String id;


//    s23 部分内容
    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    String part;

//  s47 相关法律,list数组，元素为一个json
    /*
    * [{
            "tkx": "第一百四十四条",
            "fgmc": "《中华人民共和国民事诉讼法》",
            "fgid": "3779249"
        }]
    * */
    JSONArray lawabout;

//    s41 发布日期
//    2021-08-24
    @Field(type = FieldType.Date)
    Date pushTime;

//    s31 判决日期
//    2021-08-24
    @Field(type = FieldType.Date)
    Date createTime;

//    s7案号
//    （2021）陕0831民初894号
    @Field(type = FieldType.Keyword)
    String txt_number;

//    s8案件类别
//    民事案件
    @Field(type = FieldType.Keyword)
    String txt_type;

//    s2判决法院
//    子洲县人民法院
    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    String txt_place;

//    s17 参与人,一个数组list
    JSONArray people;

//    s45 关键字 一个数组
    JSONArray Keyword;

//    s11 案由 一个数组
    JSONArray wenshuAu;



}
