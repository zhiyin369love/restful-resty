package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.DateUtils;
import com.qianmo.eshop.model.user.user_info;

import java.util.Date;
import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class buyer_receive_address extends Model<buyer_receive_address> {
    public final static buyer_receive_address dao = new buyer_receive_address();

    //获取收货地址列表
    public List<buyer_receive_address> list(Object BuyerId) {
        List<buyer_receive_address> receiveAddressList = dao.findBy("buyer_id = ? and deleted_at is null", BuyerId);
        return receiveAddressList;
    }

    //获取单个收货地址
    public buyer_receive_address details(long id) {
        buyer_receive_address receiveAddress = dao.findById(id);
        return receiveAddress;
    }

    //添加收货地址
    public boolean add(buyer_receive_address model) {
        boolean result;
        model.set("area_id", ConstantsUtils.ALL_AREA_ID);
        model.set("buyer_id", Subject.getPrincipal().getModel().get("id"));
        if (model.save()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    //编辑收货地址
    public boolean edit(long id, buyer_receive_address model) {
        boolean result;
        buyer_receive_address receiveAddress = dao.findById(id);
        if (receiveAddress != null) {
            model.set("id", id);
            if (model.update()) {
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    //删除收货地址
    public  boolean delete(long id) {
        //boolean result;
        boolean isUpdate = buyer_receive_address.dao.update("update buyer_receive_address set deleted_at = CURRENT_TIMESTAMP  where id = ?",id);
      /*  if (dao.deleteById(id)) {
            result = true;
        } else {
            result = false;
        }*/
        return isUpdate;
    }

    //将user_info转成buyer_receive_address model
    public buyer_receive_address toModel(user_info model) {
        buyer_receive_address result = new buyer_receive_address();
        result.set("name", model.get("name"))
                .set("phone", model.get("phone"))
                .set("province_id", model.get("province_id"))
                .set("province_name", model.get("province_name"))
                .set("city_id", model.get("city_id"))
                .set("city_name", model.get("city_name"))
                .set("county_id", model.get("county_id"))
                .set("county_name", model.get("county_name"))
                .set("town_id", model.get("town_id"))
                .set("town_name", model.get("town_name"))
                .set("address", model.get("address"))
                .set("buyer_id", model.get("id"));
        return result;
    }
}