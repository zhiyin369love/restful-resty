package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import cn.dreampie.security.Subject;
import com.qianmo.eshop.common.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_receive_address")
public class buyer_receive_address extends Model<buyer_receive_address> {
    public final static buyer_receive_address dao = new buyer_receive_address();

    //获取收货地址列表
    public List<buyer_receive_address> List(Object BuyerId){
        List<buyer_receive_address> receiveAddressList = dao.findBy("buyer_id = ?",BuyerId);
        return receiveAddressList;
    }

    //获取单个收货地址
    public buyer_receive_address Details(long id){
        buyer_receive_address receiveAddress = dao.findById(id);
        return receiveAddress;
    }

    //添加收货地址
    public boolean Add(buyer_receive_address model){
        boolean result;
        model.set("area_id", ConstantsUtils.ALL_AREA_ID);
        model.set("buyer_id", Subject.getPrincipal().getModel().get("id"));
        if (model.save()){
            result = true;
        }
        else{
            result = false;
        }
        return result;
    }

    //编辑收货地址
    public boolean Edit(long id, buyer_receive_address model){
        boolean result;
        buyer_receive_address receiveAddress = dao.findById(id);
        if(receiveAddress != null){
            model.set("id",id);
            if (model.update()){
                result = true;
            }
            else{
                result = false;
            }
        }
        else{
            result = false;
        }
        return result;
    }

    //删除收货地址
    public boolean Delete(long id){
        boolean result;
        if (dao.deleteById(id)){
            result = true;
        }
        else{
            result = false;
        }
        return result;
    }
}