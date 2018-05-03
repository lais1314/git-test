package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.po.Brand;
import com.leyou.item.po.Sku;
import com.leyou.item.po.Spu;
import com.leyou.item.po.SpuDetail;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.api.BrandService;
import com.leyou.search.service.api.CategoryService;
import com.leyou.search.service.api.GoodsService;
import com.leyou.search.service.api.SpecificationService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-23 13:49
 **/
@Service
public class SearchService {


    @Autowired
    private ElasticsearchTemplate searchTemplate;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public SearchResult search(SearchRequest request) {
        // 如果没有关键词，则不进行搜索，避免查询所有数据
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }
        // 分页信息, ES 中分页是从0开始
        int page = request.getPage() - 1;
        int rows = request.getRows();
        // 排序信息
        String sortBy = request.getSortBy();
        boolean asc = request.getAsc();

        // 1、查询,创建组合查询构建器
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 1.1、查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 1.2、过滤条件
        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, String> entry : request.getFilter().entrySet()) {
            String key = entry.getKey();
            // 判断是否是数值类型
            String value = entry.getValue();
            String regex = "^(\\d+\\.?\\d+)-(\\d+\\.?\\d+)$";

            if (value.matches(regex)) {
                Double[] nums = NumberUtils.searchNumber(value, regex);
                // 数值类型进行范围查询
                filterBuilder.must(QueryBuilders.rangeQuery("specs." + key).gte(nums[0]).lt(nums[1]));
            } else {
                if (key != "cid3" && key != "brandId") {
                    key = "specs." + key + ".keyword";
                }
                filterBuilder.must(QueryBuilders.termQuery(key, value));
            }

        }
        queryBuilder.filter(filterBuilder);

        // 1.3、分页和排序
        Pageable pageable = null;
        if (StringUtils.isNotBlank(sortBy)) {
            pageable = PageRequest.of(page, rows, Sort.by(asc ? Direction.ASC : Direction.DESC, sortBy));
        } else {
            pageable = PageRequest.of(page, rows);
        }
        // 1.4、对商品分类聚合,按照三级分类嵌套聚合
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("cid1").field("cid1").size(20)
                .subAggregation(AggregationBuilders.terms("cid2").field("cid2").size(20)
                        .subAggregation(AggregationBuilders.terms("cid3").field("cid3").size(20)));
        // 1.5、对品牌进行聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand").field("brandId").size(18);


        // 1.6、整合所有条件
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .addAggregation(categoryAgg)
                .addAggregation(brandAgg)
                .build();

        // 1.7、执行查询，获取结果
        AggregatedPage<Goods> data = this.searchTemplate.queryForPage(query, Goods.class);

        // 2、解析结果
        // 2.1 计算总页数
        long totalPage = (data.getTotalElements() + rows - 1) / rows;

        List<Long> cids = new ArrayList<>();// 商品3级分类id集合
        List<String> categories = new ArrayList<>();// 商品id集合，多级分类会用“/”拼接为字符串
        // 2.2 获取商品分类聚合数据
        LongTerms agg1 = (LongTerms) data.getAggregation("cid1");
        for (LongTerms.Bucket b1 : agg1.getBuckets()) {
            LongTerms agg2 = (LongTerms) b1.getAggregations().asMap().get("cid2");
            for (LongTerms.Bucket b2 : agg2.getBuckets()) {
                LongTerms agg3 = (LongTerms) b2.getAggregations().asMap().get("cid3");
                for (LongTerms.Bucket b3 : agg3.getBuckets()) {
                    categories.add(b1.getKeyAsString() + "/" + b2.getKeyAsString() + "/" + b3.getKeyAsString());
                    cids.add(b3.getKeyAsNumber().longValue());
                }
            }
        }

        // 2.3 获取品牌聚合数据
        List<Long> brandIds = new ArrayList<>();
        LongTerms brandTerms = (LongTerms) data.getAggregation("brand");
        for (LongTerms.Bucket brandBucket : brandTerms.getBuckets()) {
            brandIds.add(brandBucket.getKeyAsNumber().longValue());
        }
        List<Brand> brands = null;
        ResponseEntity<List<Brand>> resp = this.brandService.queryBrandByIds(brandIds);
        if (resp.getStatusCode().value() == 200) {
            brands = resp.getBody();
        }

        // 3、根据分类查询规格参数
        List<Map<String, Object>> specs = new ArrayList<>(0);
        if (cids.size() == 1) {
            specs = getSpecs(queryBuilder, cids.get(0));
        }
        return new SearchResult(data.getTotalElements(), totalPage, data.getContent(), categories, brands, specs);
    }

    private List<Map<String, Object>> getSpecs(QueryBuilder query, Long cid) {
        String unitsKey = "search:spec:units:" + cid;
        String numKey = "search:spec.num:" + cid;
        String strKey = "search:spec.str:" + cid;

        Map<Object, Object> units = null;// 保存数值规格的单位
        Map<String, Double> numericalSpec = new HashMap<>();// 保存数值类型规格,及分段间隔
        Set<String> stringSpec = null;// 保存非数值类型规格

        units = this.redisTemplate.opsForHash().entries(unitsKey);
        stringSpec = this.redisTemplate.opsForSet().members(strKey);
        Map<Object, Object> numSpecCache = this.redisTemplate.opsForHash().entries(numKey);
        for (Map.Entry<Object, Object> entry : numSpecCache.entrySet()) {
            numericalSpec.put(entry.getKey().toString(), Double.parseDouble(entry.getValue().toString()));
        }

        if (CollectionUtils.isEmpty(units) ||
                CollectionUtils.isEmpty(numericalSpec) ||
                CollectionUtils.isEmpty(stringSpec)) {

            // 1 根据分类查询规格
            ResponseEntity<String> resp = this.specificationService.querySpecificationByCategoryId(cid);
            if (resp.getStatusCode().value() >= 300 || !resp.hasBody()) {
                return null;
            }

            // 2 解析可以被搜索的规格
            List<Map<String, Object>> list = JsonUtils.nativeRead(resp.getBody(), new TypeReference<List<Map<String, Object>>>() {
            });
            if (list != null) {
                for (Map<String, Object> map : list) {
                    List<Map<String, Object>> params = (List<Map<String, Object>>) map.get("params");
                    for (Map<String, Object> param : params) {
                        if ((boolean) param.get("searchable")) {
                            // 判断是否是数值类型
                            if (param.containsKey("numerical") && (boolean) param.get("numerical")) {
                                numericalSpec.put(param.get("k").toString(), 0d);
                                units.put(param.get("k").toString(), param.get("unit").toString());
                            } else {
                                stringSpec.add(param.get("k").toString());
                            }
                        }
                    }
                }
            }

            // 3 对 数值类型规格数据进行聚合,计算分段间隔大小

            // 3.1 编写聚合,计算每个数值类型规格属性 最大和最小值
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withIndices("goods").withTypes("docs").withPageable(PageRequest.of(0, 1));
            for (String key : numericalSpec.keySet()) {
                queryBuilder.addAggregation(AggregationBuilders.stats(key).field("specs." + key));
            }
            // 3.2 查询
            Map<String, Aggregation> aggs = this.searchTemplate.query(
                    queryBuilder.build(), SearchResponse::getAggregations).asMap();

            // 3.4 解析数值类型
            for (String key : numericalSpec.keySet()) {
                InternalStats stats = (InternalStats) aggs.get(key);
                // 计算得出数值适当间隔
                double interval = getInterval(stats.getMin(), stats.getMax(), stats.getSum());
                numericalSpec.put(key, interval);
            }

            try {
                // 将得到的结果缓存到Redis中
                this.redisTemplate.opsForHash().putAll(unitsKey, units);
                this.redisTemplate.opsForSet().add(strKey, stringSpec.toArray(new String[stringSpec.size()]));
                Map<String, String> map = new HashMap<>();
                for (Map.Entry<String, Double> entry : numericalSpec.entrySet()) {
                    map.put(entry.getKey(), entry.getValue().toString());
                }
                this.redisTemplate.opsForHash().putAll(numKey, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 4、再次对数值类型进行聚合
        NativeSearchQueryBuilder queryBuilder2 = new NativeSearchQueryBuilder()
                .withIndices("goods").withTypes("docs")
                .withPageable(PageRequest.of(0, 1))
                .withQuery(query);
        for (Map.Entry<String, Double> entry : numericalSpec.entrySet()) {
            queryBuilder2.addAggregation(
                    AggregationBuilders
                            .histogram(entry.getKey())// 聚合名称
                            .field("specs." + entry.getKey())// 查询字段，例如：specs.cpu频率
                            .interval(entry.getValue())// 分段间隔
                            .minDocCount(1));// 只显示最少有一个文档的结果
        }
        // 5、对非数值类型进行聚合
        for (String key : stringSpec) {
            queryBuilder2.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));
        }

        // 6、查询结果
        Map<String, Aggregation> aggregationMap = this.searchTemplate.query(
                queryBuilder2.build(), SearchResponse::getAggregations).asMap();

        // 7、解析结果，将所有规格及选项组封装
        List<Map<String, Object>> result = new ArrayList<>();
        // 7.1、封装数值类型
        for (String key : numericalSpec.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("k", key);
            // 获取单位
            map.put("unit", units.get(key));
            List<String> options = new ArrayList<>();
            InternalHistogram histogram = (InternalHistogram) aggregationMap.get(key);
            for (InternalHistogram.Bucket bucket : histogram.getBuckets()) {
                // 计算分段区间并拼接成字符串
                Double begin = (double) bucket.getKey();
                Double end = begin + numericalSpec.get(key);
                // 对begin和end取整
                if (NumberUtils.isInt(begin) && NumberUtils.isInt(end)) {
                    // 确实是整数，需要取整
                    options.add(begin.intValue() + "-" + end.intValue());
                } else {
                    // 小数，取2位小数
                    begin = NumberUtils.scale(begin, 2);
                    end = NumberUtils.scale(end, 2);
                    options.add(begin + "-" + end);
                }
            }
            map.put("options", options);
            result.add(map);
        }
        // 7.2、封装非数值类型
        for (String key : stringSpec) {
            Map<String, Object> map = new HashMap<>();
            map.put("k", key);
            List<Object> options = new ArrayList<>();
            StringTerms terms = (StringTerms) aggregationMap.get(key);
            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                options.add(bucket.getKey().toString());
            }
            map.put("options", options);
            result.add(map);
        }
        return result;
    }

    private double getInterval(double min, double max, Double sum) {
        double interval = (max - min) / 6.0d;
        // 判断是否是小数
        if (sum.intValue() == sum) {
            // 不是小数，要取整十、整百这样
            // 根据interval的整数位长度来判断位数
            int length = StringUtils.substringBefore(String.valueOf(interval), ".").length();
            double factor = Math.pow(10.0, length - 1);
            return Math.round(interval / factor) * factor;
        } else {
            // 是小数,我们只保留一位小数
            return NumberUtils.scale(interval, 1);
        }
    }

    public void createIndex(Long id) throws IOException {
        // 查询spu
        ResponseEntity<Spu> spuResp = this.goodsService.querySpuById(id);
        // 查询sku信息
        ResponseEntity<List<Sku>> skuResp = this.goodsService.querySkuBySpuId(id);
        // 查询详情
        ResponseEntity<SpuDetail> detailResp = this.goodsService.querySpuDetailById(id);
        if (!spuResp.hasBody() || !skuResp.hasBody() || !detailResp.hasBody()) {
            return;
        }
        Spu spu = spuResp.getBody();
        List<Sku> skus = skuResp.getBody();
        SpuDetail detail = detailResp.getBody();
        // 查询商品分类名称
        ResponseEntity<String> nameResp = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        String names = "";
        if (nameResp.hasBody()) {
            names = nameResp.getBody();
        }

        // 获取商品详情中的规格模板
        List<Map<String, Object>> specTemplate = JsonUtils.mapper.readValue(detail.getSpecifications(), new TypeReference<List<Map<String, Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();
        // 过滤规格模板，把所有可搜索的信息保存到Map中
        specTemplate.forEach(m -> {
            List<Map<String, Object>> params = (List<Map<String, Object>>) m.get("params");
            params.forEach(p -> {
                if ((boolean) p.get("searchable")) {
                    if (p.get("v") != null) {
                        if (!"以官网信息为准".equals(p.get("v"))) {
                            specs.put(p.get("k").toString(), p.get("v"));
                        }
                    } else if (p.get("options") != null) {
                        specs.put(p.get("k").toString(), p.get("options"));
                    }
                }
            });
        });
        // 组装sku信息
        List<Map<String, Object>> skuList = new ArrayList<>();
        Set<Long> prices = new HashSet<>();
        for (Sku sku : skus) {
            // 保存价格
            prices.add(sku.getPrice());
            // 其它信息
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("image", StringUtils.isBlank(sku.getImages()) ?
                    "" : StringUtils.split(sku.getImages(), ",")[0]);
            map.put("price", sku.getPrice());
            map.put("title", sku.getTitle());
            skuList.add(map);
        }

        Goods goods = new Goods();
        // 基本属性
        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        // sku相关信息
        goods.setPrice(new ArrayList<>(prices));// 价格
        goods.setSkus(JsonUtils.serialize(skuList));// sku的json数组结构
        // 所有分词搜索的数据,包含：标题、分类名称、品牌名称等
        goods.setAll(skus.get(0).getTitle() + " " + names);

        // 写索引
        this.searchTemplate.index(new IndexQueryBuilder()
                .withIndexName("goods").withType("docs").withObject(goods).build());
    }

    public void deleteIndex(Long id) {
        this.searchTemplate.delete("good", "docs", id.toString());
    }
}
