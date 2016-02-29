package com.qianmo.eshop.resource.z_common.geo;

import cn.dreampie.route.annotation.API;
import cn.dreampie.route.annotation.GET;
import com.qianmo.eshop.resource.z_common.ApiResource;
import com.qianmo.eshop.model.geo.geo_info;


import java.util.List;


/**
 * Created by ccq on 16-1-1.
 */
@API("/geo")
public class GeoResource extends ApiResource {

  @GET
  public List<geo_info> GetProvince() {
    return geo_info.dao.find("select geo_id,geo_name from geo_info where geo_type = 'PROVINCE'");
  }

  @GET("/name")
  public List<geo_info> GetGeo(String gname) {
    return geo_info.dao.find("select geo_id,geo_name from geo_info where geo_name like '%" + gname +"%' limit 10");
  }

  @GET("/get")
  public List<geo_info> GetGeoaa(String province, String city, String county, String gname) {
    return geo_info.dao.find("select geo_id,geo_name from geo_info where geo_name like '%" + gname +"%' and  limit 10");
  }

  @GET("/:gid")
  public List<geo_info> GetByGid(long gid) {
    return geo_info.dao.find("select geo_id,geo_name from geo_info where geo_id in (select geo_id_to from geo_assoc where geo_id = ?) ", gid);
  }
}