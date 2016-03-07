package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.YamlRead;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "sec_role_permission", cached = true)
public class sec_role_permission extends Model<sec_role_permission> {
    public static final sec_role_permission dao = new sec_role_permission();


    //根据卖家ID获取子账号权限列表
    public List getListById(long account_id) {
        List result = new ArrayList();

        //根据卖家ID查询子账号权限列表
        dao.find(YamlRead.getSQL("getAccountPermissionById","seller/seller"),account_id);

        return result;
    }
}
