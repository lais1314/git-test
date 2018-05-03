package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.dto.Pagination;
import com.leyou.common.service.BaseService;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.po.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-01 20:38
 **/
@Service
public class BrandService extends BaseService<Brand, Long> {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Pagination pagination, String key) {
        // 开始分页
        PageHelper.startPage(pagination.getPage(), pagination.getRows());
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }
        // 排序
        example.setOrderByClause(pagination.getOrderByClause() == null ? "id ASC" : pagination.getOrderByClause());
        // 查询
        Page<Brand> page = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page);
    }

    @Transactional
    public void saveBrand(Brand brand) {
        // 保存品牌信息
        this.brandMapper.insert(brand);
        // 保存品牌与分类的中间表
        brand.getCategories().forEach(cid -> this.brandMapper.insertCategoryBrand(cid, brand.getId()));
    }

    @Transactional
    public void updateBrand(Brand brand) {
        // 更新品牌信息
        this.brandMapper.updateByPrimaryKey(brand);
        // 更新品牌与分类的中间表
        // 1)删除以前的数据
        this.brandMapper.deleteCategoryBrandByBrandId(brand.getId());
        // 2）重新插入数据
        brand.getCategories().forEach(cid -> this.brandMapper.insertCategoryBrand(cid, brand.getId()));
    }

    @Transactional
    public void deleteBrand(Long id) {
        // 删除品牌信息
        this.brandMapper.deleteByPrimaryKey(id);
        // 删除中间表数据
        this.brandMapper.deleteCategoryBrandByBrandId(id);
    }

    public List<Brand> queryBrandByCategory(Long cid) {

        return this.brandMapper.queryByCategoryId(cid);
    }

}
