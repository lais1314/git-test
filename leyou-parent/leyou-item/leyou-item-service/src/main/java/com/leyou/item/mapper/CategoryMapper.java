package com.leyou.item.mapper;

import com.leyou.item.po.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-25 00:49
 **/
@org.apache.ibatis.annotations.Mapper
public interface CategoryMapper extends Mapper<Category> {

    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);

    @Select("SELECT name FROM tb_category WHERE id = #{id}")
    String queryNameById(Long id);
}
