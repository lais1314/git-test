package com.leyou.service;

import com.google.common.io.Files;
import com.leyou.item.po.Sku;
import com.leyou.item.po.Spu;
import com.leyou.item.po.SpuDetail;
import com.leyou.service.api.BrandService;
import com.leyou.service.api.CategoryService;
import com.leyou.service.api.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 15:46
 **/
@Service
public class GoodsPageService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Value("${ly.thymeleaf.dest-path}")
    private String destPath;

    public void createHtml(Long id) throws Exception {
        // 创建上下文，把查询到的数据加入上下文
        Context context = new Context();
        context.setVariables(this.getModels(id));

        // 创建输出流，关联到一个临时文件
        File temp = new File(id + ".html");
        // 目标页面文件
        File dest =  createPath(id);
        // 备份原页面文件
        File bak = new File(id + "_bak.html");
        try (PrintWriter writer = new PrintWriter(temp, "UTF-8")) {

            // 利用thymeleaf模板引擎生成 静态页面
            templateEngine.process("item", context, writer);

            if (dest.exists()) {
                // 如果目标文件已经存在，先备份
                dest.renameTo(bak);
            }
            // 将新页面覆盖旧页面
            Files.copy(temp, dest);
            // 成功后将备份页面删除
            bak.delete();
        } catch (IOException e) {
            // 失败后，将备份页面恢复
            bak.renameTo(dest);
            // 重新抛出异常，声明页面生成失败
            throw new Exception(e);
        } finally {
            // 删除临时页面
            temp.delete();
        }

    }

    private File createPath(Long id) {
        if (id == null) {
            return null;
        }
        String idStr = id.toString();
        String dir;
        String suffix = ".html";
        int len = idStr.length();
        if (len == 1) {
            dir = "/0/" + idStr + "/";
        } else {
            dir = "/" + idStr.charAt(len - 2) + "/" + idStr.charAt(len - 1) + "/";
        }
        File dest = new File(this.destPath, dir);
        if(!dest.exists()){
            dest.mkdirs();
        }
        return new File(dest, idStr + suffix);
    }

    public Map<String, Object> getModels(Long id) {
        Map<String, Object> model = new HashMap<>();
        // 查询spu信息并添加到模型
        ResponseEntity<Spu> spuResp = this.goodsService.querySpuById(id);
        ResponseEntity<SpuDetail> detailResp = this.goodsService.querySpuDetailById(id);
        ResponseEntity<List<Sku>> skuResp = this.goodsService.querySkuBySpuId(id);
        if (!spuResp.hasBody() || !detailResp.hasBody() || !skuResp.hasBody()) {
            // 没有商品，什么都不做
            return null;
        }
        Spu spu = spuResp.getBody();
        model.put("spu", spu);
        // 查询spuDetail信息并添加到模型
        model.put("spuDetail", detailResp.getBody());
        // 查询sku信息并添加到模型
        model.put("skus", skuResp.getBody());

        // 查询商品分类三级名称
        ResponseEntity<String> nameResp =
                this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (nameResp.hasBody()) {
            String[] names = nameResp.getBody().split("/");
            model.put("categories", names);
        }

        // 查询品牌名称
        model.put("brand", this.brandService.queryBrandById(spu.getBrandId()).getBody());

        return model;
    }

    public void deleteHtml(Long id) {
        File dest = createPath(id);
        dest.deleteOnExit();
    }
}
