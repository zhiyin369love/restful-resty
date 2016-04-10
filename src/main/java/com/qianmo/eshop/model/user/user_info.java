package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import cn.dreampie.security.*;
import com.alibaba.fastjson.annotation.JSONField;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.common.SessionUtil;
import com.qianmo.eshop.common.YamlRead;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public boolean resetPwd(String confirm_pwd, String pwd, String token) {
        try {
            //判断新旧密码是否一致
            if (confirm_pwd != pwd) {
                return false;
            }
            //判断用户的token是否正确
            invite_verify_code inviteVerifyCode = invite_verify_code.dao.findFirstBy("token = ?", token);
            if (inviteVerifyCode.get("phone") != null) {
                //根据id获取用户信息,判断是否存在此用户
                user_info UserInfo = user_info.dao.findFirstBy("username", inviteVerifyCode.get("phone"));
                if (UserInfo == null) {
                    return false;
                } else {
                    //正确则更新密码
                    UserInfo.set("password", DefaultPasswordService.instance().crypto(pwd)).save();
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
        long id;
        String randCode = null;
        try {
            id = invite_verify_code.dao.queryFirst("select id from invite_verify_code where code=? and phone = ? and expire_time > NOW()", code, phone);
            //判断是否成功匹配到了验证码 zero表示没有验证成功
            if (id != ConstantsUtils.ZERO) {
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

}
