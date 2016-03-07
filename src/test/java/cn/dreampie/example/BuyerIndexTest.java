//package cn.dreampie.example;
//
//import cn.dreampie.client.Client;
//import cn.dreampie.client.ClientRequest;
//import cn.dreampie.client.ClientResult;
//import cn.dreampie.client.ClientUser;
//import cn.dreampie.common.util.json.Jsoner;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * 首页买家测试类
// * Created by zhangyang on 16-3-3.
// */
//@FixMethodOrder(MethodSorters.JVM)
//public class BuyerIndexTest {
//
//  private Client client;
//
//  @Before
//  public void setUp() throws Exception {
//    client = new Client("http://localhost:8081/api/v1.0");
//  }
//
//  /**
//   *
//   * 获取详情测试方法
//   */
//  @Test
//  public void testGet() {
//    ClientRequest request = new ClientRequest("/buyer/total");
//    request.addParam("buyer_id","321");
////    Jsoner.addDeserializer(User.class, ModelDeserializer.instance());
////    System.out.println(Jsoner.toObject(client.build(request).get().getResult(),new TypeReference<List<User>>(){}));
//    System.out.println(client.build(request).get());
//  }
//
//  @Test
//  public void testSellerGet() {
//    ClientRequest request = new ClientRequest("/buyer/seller");
//    request.addParam("bind_code","123");
//    request.addParam("buyer_id","321");
////    Jsoner.addDeserializer(User.class, ModelDeserializer.instance());
////    System.out.println(Jsoner.toObject(client.build(request).get().getResult(),new TypeReference<List<User>>(){}));
//    System.out.println(client.build(request).get());
//  }
//
//  /**
//   *
//   * 绑定测试方法
//   */
//  @Test
//  public void testPost() {
//    ClientRequest request = new ClientRequest("/buyer/bind");
//    request.addParam("bind_code","123");
//    request.addParam("buyer_id","321");
//    System.out.println(client.build(request).post());
//  }
//
// /* @Test
//  public void testDelete() {
//    ClientRequest request = new ClientRequest("/tests/1");
//    System.out.println(client.build(request).delete());
//  }
//
//  @Test
//  public void testPut() {
//    ClientRequest request = new ClientRequest("/tests/1");
//    request.setJsonParam("{\"id\":\"1\",\"username\":\"哈市大\"}");
//    System.out.println(client.build(request).put());
//  }*/
////  httpurlconnection patch
////  @Test
////  public void testPatch() {
////    ClientRequest request = new ClientRequest("/tests/1");
////    request.setJsonParam("{\"id\":\"1\",\"username\":\"k\"}");
////    System.out.println(client.build(request).patch());
////  }
///*
//
//  @Test
//  public void testUpload() throws FileNotFoundException {
//    //upload
//    ClientRequest uploadRequest = new ClientRequest("/tests/file");
//    uploadRequest.addUploadFile("testfile", BuyerIndexTest.class.getResource("/resty.jar").getFile());
//    uploadRequest.addParam("des", "test file  paras  测试笔");
//    ClientResult uploadResult = client.build(uploadRequest).post();
//    System.out.println(uploadResult.getResult());
//  }
//
//  @Test
//  public void testDownload() {
//    //download  支持断点续传
//    ClientRequest downloadRequest = new ClientRequest("/tests/file");
//    downloadRequest.setDownloadFile(BuyerIndexTest.class.getResource("/").getFile(), false);
//    ClientResult downloadResult = client.build(downloadRequest).get();
//    System.out.println(downloadResult);
//  }
//
//  @Test
//  public void testSave() {
//    ClientRequest request = new ClientRequest("/users/1?x");
//    String json = Jsoner.toJSON(
////        new HashMap<String, Object>() {
////          {
////            put("users",
//        new ArrayList() {
//          {
//            add(new HashMap<String, String>() {{
//              put("sid", "1");
//              put("username", "test1");
//              put("providername", "test1");
//              put("password", "123456");
//              put("created_at", "2014-10-11 10:09:12");
//            }});
//
//            add(new HashMap<String, String>() {{
//              put("sid", "2");
//              put("username", "test2");
//              put("providername", "tes2");
//              put("password", "123456");
//              put("created_at", "2014-10-12 10:09:12");
//            }});
//          }
//        }
//    );
//    request.setJsonParam(json);
//    System.out.println(client.build(request).post());
//  }
//*/
//
//}
