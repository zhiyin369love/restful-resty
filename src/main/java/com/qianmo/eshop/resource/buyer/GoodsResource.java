package com.qianmo.eshop.resource.buyer;

import cn.dreampie.orm.page.FullPage;
import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.bean.goods.GoodsInfo;
import com.qianmo.eshop.bean.goods.GoodsSku;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_category;
import com.qianmo.eshop.model.goods.goods_info;
import com.qianmo.eshop.model.goods.goods_sku;
import com.qianmo.eshop.model.user.user_info;
import com.qianmo.eshop.resource.z_common.ApiResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fxg06 on 2016/3/6.
 */
@API("/goods")
public class GoodsResource extends ApiResource {
    /**
     * 获取商品列表
     * @param goods_name 商品名称
     * @param category_id 商品一级分类ID
     * @param sub_category_id 商品二级分类ID
     * @param page_start 第几页开始
     * @param page_step 返回多少条
     * @param sort 排序 1:新品
     * @param sort_style 排序方式
     * @param sort_type 排序类型
     * @return
     */
    @GET
    public HashMap goods(String goods_name,Integer category_id,Integer sub_category_id,
                         Integer page_start,Integer page_step,Integer sort,Integer sort_style,Integer sort_type){
        user_info userInfo = (user_info) Subject.getPrincipal().getModel();
        long buyer_id = 0;
        //判断登录用户是否为子账号，如果是则获取其父级id
        if(userInfo!=null){
            buyer_id = Long.parseLong(userInfo.get("id").toString());
        }
        /*
        判断是否有分页信息，如果没有，给定默认值
         */
        if (page_start==null){
            page_start = ConstantsUtils.DEFAULT_PAGE_START;
        }
        if (page_step==null){
            page_step = ConstantsUtils.DEFAULT_PAGE_STEP;
        }

        String sql = YamlRead.getSQL("findGoodsInfo","buyer/goods");
        /*
        判断是根据一级分类查商品还是二级分类查商品
         */
        if(sub_category_id!=null && sub_category_id>0){
            sql = sql + " AND a.category_id="+sub_category_id;
        }else{
            sql = sql + " AND a.category_id in (SELECT id from goods_category where pid="+category_id+")";
        }
        /*
        判断是否根据商品名称模糊搜索
         */
        if (goods_name!=null && !"".equals(goods_name)){
            sql = sql + " AND a.name like '%"+goods_name+"%'";
        }
        if(sort!=null){
            if (sort == ConstantsUtils.SORT_NEW){
                sql = sql + " ORDER BY b.release_date DESC";
            }else{
                sql = sql + " ORDER BY b.release_date DESC";
            }
            /**
             * 按价格排序
             */
            if(sort_style!=null && sort_style==ConstantsUtils.SORT_PRICE){
                sql = sql + ",c.price";
                if(sort_type!=null && sort_type==ConstantsUtils.SORT_ASC){
                    sql = sql + " ASC";//升序
                }else{
                    sql = sql + " DESC";//降序
                }

            }
        }else{
            /**
             * 按价格排序
             */
            if(sort_style!=null && sort_style==ConstantsUtils.SORT_PRICE){
                sql = sql + "c.price";
                if(sort_type!=null && sort_type==ConstantsUtils.SORT_ASC){
                    sql = sql + " ASC";//升序
                }else{
                    sql = sql + " DESC";//降序
                }

            }
        }
        HashMap resultMap = new HashMap();
        HashMap<Long,GoodsInfo> map = new HashMap<Long, GoodsInfo>();
        FullPage<goods_info> list = goods_info.dao.fullPaginate(page_start/page_step + 1,page_step,sql,buyer_id);
        if (list!=null && list.getTotalRow()>0){
            for(goods_info goodsInfo : list.getList()){
                GoodsInfo goods = map.get(Long.parseLong(goodsInfo.get("goods_id").toString()));
                if (goods == null){
                    goods = new GoodsInfo();
                    goods.setGoods_id(Long.parseLong(goodsInfo.get("goods_id").toString()));
                    goods.setGoods_name(goodsInfo.get("goods_name").toString());
                    goods.setGoods_num(Long.parseLong(goodsInfo.get("goods_id").toString()));
                    goods.setGeneric_name(goodsInfo.get("generic_name").toString());
                    //判断是否有主图
                    if(goodsInfo.get("main_pic_url")!=null){
                        goods.setMain_pic_url(goodsInfo.get("main_pic_url").toString());
                    }
                    goods.setProducer(goodsInfo.get("producer").toString());
                    goods.setSeller_name(goodsInfo.get("seller_name").toString());
                    goods.setIngredient(goodsInfo.get("ingredient").toString());


                    List<GoodsSku> skuList = new ArrayList<GoodsSku>();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(Integer.parseInt(goodsInfo.get("status").toString()));
                    goodsSku.setPrice(Double.parseDouble(goodsInfo.get("price").toString()));
                    goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                }else{
                    List<GoodsSku> skuList = (List)goods.getSkuList();
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setSku_id(Long.parseLong(goodsInfo.get("sku_id").toString()));
                    goodsSku.setSku_name(goodsInfo.get("sku_name").toString());
                    goodsSku.setStatus(Integer.parseInt(goodsInfo.get("status").toString()));
                    goodsSku.setPrice(Double.parseDouble(goodsInfo.get("price").toString()));
                    goodsSku.setRelease_date(goodsInfo.get("release_date").toString());
                    skuList.add(goodsSku);
                    goods.setSkuList(skuList);
                }
                map.put(goods.getGoods_id(),goods);
            }
        }
        List<GoodsInfo> goodsInfoList = new ArrayList<GoodsInfo>();
        if (map!=null && map.size()>0){
            for(Long goodsId:map.keySet()){
                goodsInfoList.add(map.get(goodsId));
            }
        }
        resultMap.put("goods_list",goodsInfoList);
        resultMap.put("total_count",goodsInfoList.size());
        return resultMap;
    }
    /**
     * 获取商品分类
     * @return
     */
    @GET("/type")
    public List goodsCategory(){
        return goods_category.dao.getList();
    }

    /**
     * 获取商品详情
     * @param id
     * @param buyer_id
     * @return
     */
    @GET("/:id")
    public HashMap goods(long id,long buyer_id){
        HashMap resultMap = new HashMap();
        goods_info goodsInfo = goods_info.dao.findFirst(YamlRead.getSQL("findGoods","buyer/goods"),id);
        resultMap.put("goods_info",goodsInfo);
        List<goods_sku> list = goods_sku.dao.find(YamlRead.getSQL("findGoodsSku","buyer/goods"),goodsInfo.get("goods_num"),buyer_id);
        resultMap.put("goods_sku_list",list);
        return resultMap;
    }
}
