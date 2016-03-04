package cn.dreampie.example;

import cn.dreampie.client.Client;
import cn.dreampie.client.ClientRequest;
import cn.dreampie.common.util.json.Jsoner;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * 买家购物车测试类
 * Created by zhangyang on 16-3-4.
 */
@FixMethodOrder(MethodSorters.JVM)
public class SellerRetailerTest {

  private Client client;
  private ClientRequest request;

  @Before
  public void setUp() throws Exception {
    client = new Client("http://localhost:8081/api/v1.0/seller");
  }


  /**
   * 发送邀请码的测试方法
   */
  @Test
  public void testAddSendCode() {
    request = new ClientRequest("/sendcode");
    String json = Jsoner.toJSON(
            new ArrayList() {
              {
                add(new HashMap<String, Object>() {{
                  put("phone", "123456");
                  put("remark", "ok");
                }});

                add(new HashMap<String, Object>() {{
                  put("phone", "1234567");
                  put("remark", "ok");
                }});
              }
            });
    HashMap params = new HashMap();
    params.put("seller_id","2");
    params.put("accounts",json);
    request.setParams(params);
    client.build(request).put();
  }

  /**
   * 编辑购物车的测试方法
   */
  @Test
  public void testPut() {
    request = new ClientRequest("/cooperation");
    request.addParam("id",null);
    request.addParam("op","1");
    request.addParam("seller_id","2");
    System.out.println(client.build(request).put());
  }


  /**
   *  测试获取经销商下的零售商信息
   */
  @Test
  public void testSellerGet() {
    ClientRequest request = new ClientRequest("/retailerList");
    request.addParam("seller_id","2");
    request.addParam("phone","123456");
    System.out.println(client.build(request).get());
  }


  /**
   * 测试获取用户购物车信息列表
     */
  @Test
  public void testGet() {
    request.addParam("buyer_id","1");
    System.out.println(client.build(request).get());
  }



  /**
   * 测试购物车商品
   */
  @Test
  public void testDelete() {
    request.addParam("buyer_id","1");
    request.addParam("goods_id","1");
    request.addParam("goods_sku_id","1");
    System.out.println(client.build(request).delete());
  }

 /* @Test
  public void testDelete() {
    ClientRequest request = new ClientRequest("/tests/1");
    System.out.println(client.build(request).delete());
  }

  @Test
  public void testPut() {
    ClientRequest request = new ClientRequest("/tests/1");
    request.setJsonParam("{\"id\":\"1\",\"username\":\"哈市大\"}");
    System.out.println(client.build(request).put());
  }*/
//  httpurlconnection patch
//  @Test
//  public void testPatch() {
//    ClientRequest request = new ClientRequest("/tests/1");
//    request.setJsonParam("{\"id\":\"1\",\"username\":\"k\"}");
//    System.out.println(client.build(request).patch());
//  }
/*

  @Test
  public void testUpload() throws FileNotFoundException {
    //upload
    ClientRequest uploadRequest = new ClientRequest("/tests/file");
    uploadRequest.addUploadFile("testfile", BuyerIndexTest.class.getResource("/resty.jar").getFile());
    uploadRequest.addParam("des", "test file  paras  测试笔");
    ClientResult uploadResult = client.build(uploadRequest).post();
    System.out.println(uploadResult.getResult());
  }

  @Test
  public void testDownload() {
    //download  支持断点续传
    ClientRequest downloadRequest = new ClientRequest("/tests/file");
    downloadRequest.setDownloadFile(BuyerIndexTest.class.getResource("/").getFile(), false);
    ClientResult downloadResult = client.build(downloadRequest).get();
    System.out.println(downloadResult);
  }

  @Test
  public void testSave() {
    ClientRequest request = new ClientRequest("/users/1?x");
    String json = Jsoner.toJSON(
//        new HashMap<String, Object>() {
//          {
//            put("users",
        new ArrayList() {
          {
            add(new HashMap<String, String>() {{
              put("sid", "1");
              put("username", "test1");
              put("providername", "test1");
              put("password", "123456");
              put("created_at", "2014-10-11 10:09:12");
            }});

            add(new HashMap<String, String>() {{
              put("sid", "2");
              put("username", "test2");
              put("providername", "tes2");
              put("password", "123456");
              put("created_at", "2014-10-12 10:09:12");
            }});
          }
        }
    );
    request.setJsonParam(json);
    System.out.println(client.build(request).post());
  }
*/

}
