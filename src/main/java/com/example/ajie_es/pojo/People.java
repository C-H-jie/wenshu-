package com.example.ajie_es.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(indexName = "people")
public class People {
    private  String id_;

    @Field(type = FieldType.Keyword,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private  String describe;

}



