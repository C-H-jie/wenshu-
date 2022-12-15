package com.example.ajie_es.pojo;
import com.alibaba.fastjson.JSONArray;
import java.util.Date;

public class SuperSearch {

    private int SearchSize;

    private int SearchPage;

    //    s1 标题
    private String title;

    //    s5 id
    private String id;


    //    s23 部分内容
    private String part;

    //  s47 相关法律,list数组，元素为一个json
    /*
    * [{
            "tkx": "第一百四十四条",
            "fgmc": "《中华人民共和国民事诉讼法》",
            "fgid": "3779249"
        }]
    * */
    private JSONArray lawabout;

    //    s41 发布日期
//    2021-08-24
    private Date startTime;

    //    s31 判决日期
//    2021-08-24
    private Date endTime;

    //    s7案号
//    （2021）陕0831民初894号
    private String txt_number;

    //    s8案件类别
//    民事案件
    private String txt_type;

    //    s2判决法院
//    子洲县人民法院
    private String txt_place;

    //    s17 参与人,姓名
    private String people;

    //    s45 关键字 一个数组
    private JSONArray Keyword;

    //    s11 案由 一个数组
    private String wenshuAu;

    public SuperSearch() {
    }

    public SuperSearch(int searchSize, int searchPage, String title, String id, String part, JSONArray lawabout, Date startTime, Date endTime, String txt_number, String txt_type, String txt_place, String people, JSONArray keyword, String wenshuAu) {
        this.SearchSize = searchSize;
        this.SearchPage = searchPage;
        this.title = title;
        this.id = id;
        this.part = part;
        this.lawabout = lawabout;
        this.startTime = startTime;
        this.endTime = endTime;
        this.txt_number = txt_number;
        this.txt_type = txt_type;
        this.txt_place = txt_place;
        this.people = people;
        this.Keyword = keyword;
        this.wenshuAu = wenshuAu;
    }

    public int getSearchSize() {
        return SearchSize;
    }

    public void setSearchSize(int searchSize) {
        SearchSize = searchSize;
    }

    public int getSearchPage() {
        return SearchPage;
    }

    public void setSearchPage(int searchPage) {
        SearchPage = searchPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public JSONArray getLawabout() {
        return lawabout;
    }

    public void setLawabout(JSONArray lawabout) {
        this.lawabout = lawabout;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTxt_number() {
        return txt_number;
    }

    public void setTxt_number(String txt_number) {
        this.txt_number = txt_number;
    }

    public String getTxt_type() {
        return txt_type;
    }

    public void setTxt_type(String txt_type) {
        this.txt_type = txt_type;
    }

    public String getTxt_place() {
        return txt_place;
    }

    public void setTxt_place(String txt_place) {
        this.txt_place = txt_place;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public JSONArray getKeyword() {
        return Keyword;
    }

    public void setKeyword(JSONArray keyword) {
        Keyword = keyword;
    }

    public String getWenshuAu() {
        return wenshuAu;
    }

    public void setWenshuAu(String wenshuAu) {
        this.wenshuAu = wenshuAu;
    }
}
