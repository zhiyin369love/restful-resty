package com.qianmo.eshop.model.buyer;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;
import com.qianmo.eshop.common.CommonUtils;
import com.qianmo.eshop.common.ConstantsUtils;
import com.qianmo.eshop.model.user.invite_verify_code;
import com.qianmo.eshop.model.user.user_info;

import java.util.List;

/**
 * Created by ccq on 16-1-1.
 */
@Table(name = "buyer_seller")
public class buyer_seller extends Model<buyer_seller> {
    public final static buyer_seller dao = new buyer_seller();

    public List<buyer_seller> getBuyerList(long seller_id, int status) {
        return buyer_seller.dao.findBy("seller_id = ? and status = ?", seller_id, status);
    }


    public boolean bindSeller(int bind_code, long buyer_id){
        //通过验证码找卖家id
        invite_verify_code code = getInviteByVerifyCode(bind_code);
        if (code != null && buyer_id != 0 && code.<Boolean>get("status") == true) {
            Long seller_Id = code.<Long>get("user_id");
            String phone = user_info.dao.findById(buyer_id).get("username");
            if(!CommonUtils.isEmpty(phone) && phone.equals(code.get("phone").toString())) {
            //查看是否已经绑定过
              buyer_seller buyerSeller = buyer_seller.dao.unCache().findFirstBy("buyer_id = ? and seller_id = ?", buyer_id, seller_Id);
              if (buyerSeller == null) {
                //如果没有绑定，则将买家卖家绑定起来
                  buyer_seller.dao.set("area_id", ConstantsUtils.ALL_AREA_ID).set("buyer_id", buyer_id).set("seller_id", seller_Id).set("status", ConstantsUtils.BUYER_SELLER_STATUS_BIDING).save();
                  //code.set("status", ConstantsUtils.INVITE_CODE_STATUS_EXPIRED).update();
                  code.delete();
                  user_info.dao.findById(buyer_id).set("isbuyer",ConstantsUtils.YES).update();
                //return new WebResult(HttpStatus.CREATED, "绑定成功");
               } else {
                  if(buyerSeller.<Integer>get("status") == ConstantsUtils.BUYER_SELLER_STATUS_BIDING_CANCEL) {
                      buyerSeller.set("status",ConstantsUtils.BUYER_SELLER_STATUS_BIDING).update();
                      code.delete();
                  }
              }
            //else {
                //buyerSeller.set("status",ConstantsUtils.BUYER_SELLER_STATUS_BIDING).update();
                //如果已经绑定过，提示已经绑定过
            //}
            //code.set("status",ConstantsUtils.INVITE_CODE_STATUS_SUCCESSED).update();
//            return new WebResult(HttpStatus.CREATED, "绑定成功");
                return true;
            } else {
                return false;
            }
        } else {
            //如果找不到，提示验证码错误
//            return new WebResult(HttpStatus.BAD_REQUEST, "验证码错误");
            return false;
        }
    }

    public invite_verify_code getInviteByVerifyCode(int bindCode) {
        return new invite_verify_code().getInviteByCode(bindCode, ConstantsUtils.INVITE_VERIFY_CODE_TYPE_INVITE);
    }

    public List<buyer_seller> getSellerList(long buyer_id, int status) {
        return buyer_seller.dao.findBy("buyer_id = ? and status = ? ", buyer_id, status);
    }
}
