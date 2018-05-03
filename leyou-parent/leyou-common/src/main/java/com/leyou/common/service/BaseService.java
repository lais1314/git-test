package com.leyou.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-25 17:54
 **/
public class BaseService<T,ID> {

    @Autowired
    protected Mapper<T> mapper;

    private Class<T> clazz;

    {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    public T queryById(ID id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    public List<T> queryAll() {
        return this.mapper.selectAll();
    }

    public T queryByWhere(T t) {
        return this.mapper.selectOne(t);
    }

    public List<T> queryListByWhere(T t) {
        return this.mapper.select(t);
    }

    public int insert(T t) {
        return this.mapper.insert(t);
    }

    public int insertSelective(T t) {
        return this.mapper.insertSelective(t);
    }

    public int update(T t) {
        return this.mapper.updateByPrimaryKey(t);
    }

    public int updateSelective(T t) {
        return this.mapper.updateByPrimaryKeySelective(t);
    }

    public int deleteById(long id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    public int deleteByWhere(T t) {
        return this.mapper.delete(t);
    }

    public int deleteByIds(String property, List<ID> ids) {
        Example example = new Example(this.clazz);
        example.createCriteria().andIn(property, ids);
        return this.mapper.deleteByExample(example);
    }

    public List<T> queryByIds(String property, List<ID> ids) {
        Example example = new Example(this.clazz);
        example.createCriteria().andIn(property, ids);
        return this.mapper.selectByExample(example);
    }
}
