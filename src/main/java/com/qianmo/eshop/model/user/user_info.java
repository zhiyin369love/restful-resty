package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.orm.transaction.Transaction;
import cn.dreampie.security.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.qianmo.eshop.bean.user.UserInfo;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;
import com.qianmo.eshop.model.goods.goods_sku_price;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ccq on 16-2-20.
 */
@Table(name = "user_info", generatedKey = "id", cached = true)
public class user_info extends Model<user_info> {
    public final static user_info dao = new user_info();

    public boolean save() {
        boolean result;
        if (super.save()) {
            String roleId = sec_role.dao.findFirstBy("name = ?", "买家").get("id").toString();
            sec_user_role ur = new sec_user_role().set("user_id", this.get("id")).set("role_id", roleId);
            result = ur.save();
        } else {
            result = false;
        }
        return result;
    }

    public sec_user_role getRole() {
        sec_user_role role;
        if (this.get("role") == null && this.get("id") != null) {
            String sql = "SELECT role.id,role.name FROM sec_role role,sec_user_role user_role WHERE role.id=user_role.role_id AND user_role.user_id=?";
            role = sec_user_role.dao.findFirst(sql, this.get("id"));
            this.put("role", role);
        } else {
            role = this.get("role");
        }
        return role;
    }

    public List<sec_permission> getPermissions() {
        Long role_id;
        if (getRole() == null) {
            role_id = null;
        } else role_id = getRole().<Long>get("id");
        List<sec_permission> permissions;
        if (this.get("permissions") == null && role_id != null) {
            permissions = sec_permission.dao.findByRole(role_id);
            this.put("permissions", permissions);
        } else {
            permissions = this.get("permissions");
        }
        return permissions;
    }

    @JSONField(serialize = true)
    public Set<String> getPermissionValues() {
        List<sec_permission> permissions = getPermissions();
        Set<String> permissionValues = null;
        if (permissions != null) {
            permissionValues = new HashSet<String>();
            for (sec_permission permission : permissions) {
                permissionValues.add(permission.<String>get("value"));
            }
        }
        return permissionValues;
    }


    @JSONField(serialize = true)
    public Set<Long> getPermissionIds() {
        List<sec_permission> permissions = getPermissions();
        Set<Long> permissionIds = null;
        if (permissions != null) {
            permissionIds = new HashSet<Long>();
            for (sec_permission permission : permissions) {
                permissionIds.add(permission.<Long>get("id"));
            }
        }
        return permissionIds;
    }


    public user_info getUserInfo() {
        user_info model = user_info.dao.findById(SessionUtil.getUserId());
        if (model != null) {
            model.remove("password");
            model.remove("salt");
            return model;
        } else
            return null;
    }

    //修改密码
    public boolean updatePwd(long id, String confirm_pwd, String new_pwd, String old_pwd) {
        try {
            //判断新旧密码是否一致
            if (!confirm_pwd.equals(new_pwd)) {
                return false;
            }
            //根据id获取用户信息,判断是否存在此用户
            user_info UserInfo = user_info.dao.findById(id);
            if (UserInfo == null) {
                return false;
            } else {
                //判断用户填写的旧密码是否正确
                if (UserInfo.get("password").toString().equals(DefaultPasswordService.instance().crypto(old_pwd))) {
                    //正确则更新密码
                    UserInfo.set("password", DefaultPasswordService.instance().crypto(new_pwd)).update();
                    Subject.refresh();
                    Subject.logout();
                } else {
                    return false;
                }
            }

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    //重置密码
    @Transaction
    public boolean resetPwd(String confirm_pwd, String pwd, String token) {
        try {
            //判断新旧密码是否一致
            if (!confirm_pwd.equals(pwd)) {
                return false;
            }
            //判断用户的token是否正确
            invite_verify_code inviteVerifyCode = invite_verify_code.dao.findFirstBy("token = ?", token);
            if (inviteVerifyCode.get("phone") != null) {
                //根据id获取用户信息,判断是否存在此用户
                user_info UserInfo = user_info.dao.findFirstBy("username = ?", inviteVerifyCode.get("phone"));
                if (UserInfo == null) {
                    return false;
                } else {
                    //正确则更新密码
                    if(UserInfo.set("password", DefaultPasswordService.instance().crypto(pwd)).update())
                    {
                        Subject.refresh();
                        inviteVerifyCode.delete();
                    }
                }
            } else {
                return false;
            }

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    //检查验证码是否正确
    public String checkCode(String code, String phone) {
        Long id;
        String randCode = null;
        try {
            id = invite_verify_code.dao.queryFirst("select id from invite_verify_code where code=? and phone = ? and expire_time > NOW()", code, phone);
            //判断是否成功匹配到了验证码 zero表示没有验证成功
            if (!id.equals(ConstantsUtils.ZERO_LONG)) {
                randCode = CommonUtils.getRandNum(ConstantsUtils.SIX); //生成6位作为token返回
                invite_verify_code.dao.findById(id).set("token", randCode).update();
            }
        } catch (Exception ex) {
            return null;
        }
        return randCode;
    }

    public HashMap edit(user_info model) {
        HashMap result;
        Long id = SessionUtil.getUserId();
        user_info UserInfo = user_info.dao.findById(id);
        result = CommonUtils.EditreturnCodeMessage(false);
        if (UserInfo != null) {
            model.set("id", id);
            if (model.update()) {
                result = CommonUtils.EditreturnCodeMessage(true);
            }
        }
        return result;
    }


    public user_info getUserInfoById(Long id) {
        String getUserInfoSql = YamlRead.getSQL("findUserInfoById", "buyer/order");
        user_info userTemp = user_info.dao.findFirst(getUserInfoSql, id);
        return userTemp == null ? new user_info() : userTemp;
    }

    /**
     * 获取卖家下买家信息
     * @param name 零售商公司名称或者账号
     * @param pageStart  从第几条开始
     * @param pageStep   返回多少条
     * @param sellerId 卖家ID
     * @return
     */
    public FullPage<user_info> userList(String name, Integer pageStart, Integer pageStep,Long sellerId) {
        String sql = YamlRead.getSQL("findBuyer","seller/seller");
        //判断是否根据账号，公司名称及姓名模糊查询
        if (name != null && !"".equals(name)) {
            sql = sql + " AND (a.username like '%" + name + "%' or a.nickname like '%"
                    + name + "%' or a.name like '%" + name + "%')";
        }
        FullPage<user_info> userList = fullPaginate(pageStart/pageStep + 1, pageStep,sql,sellerId);
        return userList;
    }

    /**
     * 获取商品规格及规格对应的买家价格信息
     * @param skuId   商品规格ID
     * @param skuPriceStatus 商品价格状态
     * @param name 名称
     * @param pageStart  从第几条开始
     * @param pageStep   返回多少条
     * @param sellerId   卖家ID
     * @return
     */
    public List<UserInfo> userInfoList(Long skuId, Integer skuPriceStatus, String name, Integer pageStart, Integer pageStep, Long sellerId){
        FullPage<user_info> userList = userList(name,pageStart,pageStep,sellerId);
        List<UserInfo> list = new ArrayList<UserInfo>();
        String skuSql = YamlRead.getSQL("findGoodsSkuAndPrice","seller/goods");
        if(userList!=null && userList.getList().size()>0){
            for(user_info user:userList.getList()){
                UserInfo userInfo = new UserInfo();
                userInfo.setBuyer_id(user.<Long>get("buyer_id"));
                userInfo.setNickname(user.<String>get("nickname"));
                userInfo.setBuyer_address(user.<String>get("buyer_address"));
                goods_sku_price goodsSkuPrice = goods_sku_price.dao.findFirst(skuSql,user.<Long>get("buyer_id"),skuId);
                if(Long.valueOf(skuPriceStatus).equals(goodsSkuPrice.get("status"))){
                    userInfo.setSku_id(skuId);
                    userInfo.setSku_name(goodsSkuPrice.<String>get("sku_name"));
                    userInfo.setGoods_num(goodsSkuPrice.<Long>get("goods_num"));
                    if(goodsSkuPrice.get("sku_price_id")!=null){
                        userInfo.setSku_price_id(goodsSkuPrice.<Long>get("sku_price_id"));
                    }
                    userInfo.setSku_price_status(skuPriceStatus);
                    userInfo.setPrice(goodsSkuPrice.<BigDecimal>get("price"));
                    list.add(userInfo);
                }
            }
        }
        return list;
    }

    /**
     * 根据账号获取用户信息
     * @param username 用户账号拼接的字符串
     * @return
     */
    public List getUserInfo(String username){
        List<user_info> list = user_info.dao.findBy("username in ("+username+")");
        List<user_info> userList = new ArrayList<user_info>();
        if(list!=null && list.size()>0) {
            for (user_info user : list) {
                user_info userInfo = new user_info();
                userInfo.put("username", user.get("username"));
                if (user.get("name")!=null && !"".equals(user.get("name"))){
                    userInfo.put("name", user.get("name"));
                } else {
                    userInfo.put("name", "");
                }
                if (user.get("nickname")!=null && !"".equals(user.get("nickname"))){
                    userInfo.put("nickname", user.get("nickname"));
                } else {
                    userInfo.put("nickname", "");
                }
                userList.add(userInfo);
            }
        }
        return userList;
    }
}
