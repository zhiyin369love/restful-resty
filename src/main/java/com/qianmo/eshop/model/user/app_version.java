package com.qianmo.eshop.model.user;

import cn.dreampie.orm.Model;
import cn.dreampie.orm.annotation.Table;

/**
 * Title:
 * Description
 * Copyringt :
 * Company : 安徽阡陌网络科技有限公司
 *
 * @author fxg on 16-4-11
 * @version 1.0
 */
@Table(name="app_version")
public class app_version extends Model<app_version> {
    public final static app_version dao = new app_version();
}
