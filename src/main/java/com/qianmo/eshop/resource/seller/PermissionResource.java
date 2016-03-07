package com.qianmo.eshop.resource.seller;


import cn.dreampie.common.http.result.HttpStatus;
import cn.dreampie.route.annotation.*;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.model.user.sec_role_permission;
import com.qianmo.eshop.model.user.user_info;

import java.util.HashMap;

/**
 * 卖家子账号权限api
 * Created by ccq on 16-1-1.
 */
@API("/account/permission")
public class PermissionResource extends SellerResource {

    /**
     * 获取卖家子账号权限列表
     * @param account_id 子账号ID
     * @return
     */
    @GET
    public HashMap List(long account_id) {
        HashMap result = new HashMap();
        //type = 1 表示为子账号 seller_id为查询此卖家名下的子账号
        result.put("permission_list", sec_role_permission.dao.getListById(account_id));
        return result;
    }

    /**
     * 编辑卖家子账号权限
     * @param id 子账号ID
     * @param model 权限实体信息
     * @return
     */
    @PUT("/:id")
    public HashMap Edit(long id,sec_role_permission model) {
        HashMap result = new HashMap();
        sec_role_permission RolePermission = sec_role_permission.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if(RolePermission != null){
            model.set("id",id);
            if (model.update()){
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }

    /**
     * 添加卖家子账号权限信息
     * @param model 权限实体信息
     * @return
     */
    @POST
    public HashMap Add(sec_role_permission model) {
        HashMap result = new HashMap();
        result = CommonUtils.AddreturnCodeMessage(false);
        if(sec_role_permission.dao.save(model)){
            result = CommonUtils.AddreturnCodeMessage(true);
        }
        return result;
    }

    /**
     * 删除卖家子账号权限信息
     * @param id 子账号权限ID
     * @return
     */
    @DELETE("/:id")
    public HashMap Delete(long id) {
        HashMap result = new HashMap();
        result = CommonUtils.DelreturnCodeMessage(false);
        if(sec_role_permission.dao.deleteById(id)){
            result = CommonUtils.DelreturnCodeMessage(true);
        }
        return result;
    }
}