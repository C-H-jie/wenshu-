package com.example.ajie_es.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(indexName = "html_txt")
public class Law_HTML_txt {
    @Field(type = FieldType.Keyword)
    String id;

    //  qwContent HTML超文本，直接展示用
    @Field(type = FieldType.Text,index = false)
    String HTML_txt;

}
