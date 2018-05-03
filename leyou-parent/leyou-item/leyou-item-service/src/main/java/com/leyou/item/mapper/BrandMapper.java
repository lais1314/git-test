package com.leyou.item.mapper;

import com.leyou.item.po.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-26 17:15
 **/
@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id = #{bid}")
    int deleteCategoryBrandByBrandId(Long bid);

    @Select("SELECT b.* FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(Long cid);
}
