package com.leyou.item.service;

import com.leyou.common.service.BaseService;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.po.Category;
import com.leyou.item.vo.TreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-25 01:13
 **/
@Service
public class CategoryService extends BaseService<Category,Long> {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    public List<TreeResult> queryAllAsTree() {
        // 查询所有
        List<Category> list = this.categoryMapper.selectAll();
        // 转为一个map,key是父节点id，值是该父节点下所有子节点集合
        Map<Long,List<Category>> map = new HashMap<>();
        for (Category c : list) {
            if(!map.containsKey(c.getParentId())){
                map.put(c.getParentId(),new ArrayList<>());
            }
            map.get(c.getParentId()).add(c);
        }
        // 封装结果
        List<TreeResult> results = new ArrayList<>();
        // 获取1级分类
        List<Category> level1 = map.get(0L);
        for (Category c1 : level1) {
            // 封装结果
            TreeResult tr1 = new TreeResult();
            tr1.setId(c1.getId());
            tr1.setName(c1.getName());
            tr1.setParentId(c1.getParentId());
            tr1.setChildren(new ArrayList<>());
            // 将tr1放入1级类目集合
            results.add(tr1);
            // 获取2级分类
            List<Category> level2 = map.get(c1.getId());
            for (Category c2 : level2) {
                TreeResult tr2 = new TreeResult();
                tr2.setId(c2.getId());
                tr2.setName(c2.getName());
                tr2.setParentId(c2.getParentId());
                tr2.setChildren(new ArrayList<>());
                // 将tr2放入2级类目集合
                tr1.getChildren().add(tr2);
                // 获取3级类目
                List<Category> level3 = map.get(c2.getId());
                for (Category c3 : level3) {
                    TreeResult tr3 = new TreeResult();
                    tr3.setId(c3.getId());
                    tr3.setName(c3.getName());
                    tr3.setParentId(c3.getParentId());
                    // 将tr3放入3级类目集合
                    tr2.getChildren().add(tr3);
                }
            }
        }
        return results;
    }

    public String queryNameByIds(List<Long> ids) {
        if(ids == null || ids.size() == 0){
            return null;
        }
        String name = "";
        for (Long id : ids) {
            name += ("/" + this.categoryMapper.queryNameById(id));
        }
        return name.substring(1);
    }
}
