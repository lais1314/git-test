package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.dto.Pagination;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.po.Sku;
import com.leyou.item.po.Spu;
import com.leyou.item.po.SpuDetail;
import com.leyou.item.po.Stock;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-13 19:14
 **/
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    public PageResult<Spu> querySpuList(Pagination pagination, String key, Boolean saleable) {
        // 分页
        PageHelper.startPage(pagination.getPage(), pagination.getRows());
        // 创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 是否过滤上下架
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        // 是否模糊查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        // 默认按照时间排序
        example.setOrderByClause("last_update_time DESC");
        Page<Spu> page = (Page<Spu>) this.spuMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page);
    }

    public Spu querySpuById(Long id) {
        // 查询spu
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        // 查询商品描述
        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(id);
        spu.setSpuDetail(spuDetail);
        // 查询sku
        spu.setSkus(this.querySkuBySpuId(id));
        return spu;
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        for (Sku sku : skus) {
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()));
        }
        return skus;
    }

    @Transactional
    public void save(Spu spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        // 保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item.insert", spu.getId());
        } catch (AmqpException e) {
            logger.error("新增商品消息发送异常，商品id：{}", spu.getId(), e);
        }
    }

    @Transactional
    public void update(Spu spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        // 如果以前存在，则删除
        if(CollectionUtils.isNotEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item.update", spu.getId());
        } catch (AmqpException e) {
            logger.error("修改商品消息发送异常，商品id：{}", spu.getId(), e);
        }
    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(spuId);
            // 默认不参与任何促销
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock().getStock());
            this.stockMapper.insert(stock);
        }
    }

    @Transactional
    public int updateStatus(Long id, Boolean saleable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
        spu.setLastUpdateTime(new Date());
        int count = this.spuMapper.updateByPrimaryKeySelective(spu);
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item.update", spu.getId());
        } catch (AmqpException e) {
            logger.error("下架商品消息发送异常，商品id：{}", spu.getId(), e);
        }
        return count;
    }

    public SpuDetail querySpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }
}
