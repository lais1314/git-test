package com.leyou.item.po;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-27 13:22
 **/
@Table(name = "tb_specification")
public class Specification {

    @Id
    private Long categoryId;
    private String specifications;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
}
