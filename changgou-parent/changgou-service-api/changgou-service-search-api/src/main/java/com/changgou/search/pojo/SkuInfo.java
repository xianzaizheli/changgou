package com.changgou.search.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Document(indexName = "usertest",type = "skuinfo")
public class SkuInfo implements Serializable {
    @Id
    private String id;//商品id

    @Field(type = FieldType.Text ,analyzer = "ik_smart")
    private String sn;//商品条码

    /**
     *  type 指定数据类型
     *  type = FieldType.Text,Text支持分词
     *  analyzer = "ik_smart"；创建索引的分词器
     *  index = true；添加数据的时候，是否分词
     *  store = false;是否存储
     *  searchAnalyzer = "ik_smart"；搜索时候使用的分词器
     */
    @Field(type = FieldType.Text , analyzer = "ik_smart" ,searchAnalyzer = "is_smart" ,index = true ,store = false)
    private String name;//SKU名称

    @Field(type = FieldType.Double)
    private Integer price;//价格（分）

    private Integer num;//库存数量

    private Integer alertNum;//库存预警数量

    private String image;//商品图片

    private String images;//商品图片列表

    private Integer weight;//重量（克）

    private Date createTime;//创建时间

    private Date updateTime;//更新时间

    private String spuId;//SPUID

    private Integer categoryId;//类目ID

    @Field(type = FieldType.Keyword)
    private String categoryName;//类目名称

    @Field(type = FieldType.Keyword)
    private String brandName;//品牌名称

    private String spec;//规格

    private Integer saleNum;//销量

    private Integer commentNum;//评论数

    private String status;//商品状态 1-正常，2-下架，3-删除

    private Integer version;//

    //规格参数
    private Map<String,Object> specMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getAlertNum() {
        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, Object> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, Object> specMap) {
        this.specMap = specMap;
    }
}
