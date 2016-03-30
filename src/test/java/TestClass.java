/*
import cn.dreampie.client.Client;
import cn.dreampie.client.ClientRequest;
import cn.dreampie.common.util.json.Jsoner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

*/
/**
* Created by fxg06 on 2016/3/3.
*//*

@FixMethodOrder(MethodSorters.JVM)
public class TestClass {
   private Client client;
   @Test
   public void testGet() {
       ClientRequest request = new ClientRequest("/tests/哈哈");
      request.setJsonParam("2013-03-23 00:00:00");
    Jsoner.addDeserializer(User.class, ModelDeserializer.instance());
   System.out.println(Jsoner.toObject(client.build(request).get().getResult(),new TypeReference<List<User>>(){}));
       System.out.println(client.build(request).get());
////    }
//
////    @Test
////    public void testPost() {
////        ClientRequest request = new ClientRequest("/goods");
////        Map map = new HashMap();
////        map.put()
////        request.addParam("params", Jsoner.toJSON(new HashMap<String, String>() {{
////            put("a", "哈哈");
////        }}));
////        System.out.println(client.build(request).post());
////    }
//
////    @Test
////    public void testDelete() {
////        ClientRequest request = new ClientRequest("/tests/1");
////        System.out.println(client.build(request).delete());
////    }
////
////    @Test
////    public void testPut() {
////        ClientRequest request = new ClientRequest("/tests/1");
////        request.setJsonParam("{\"id\":\"1\",\"username\":\"哈市大\"}");
////        System.out.println(client.build(request).put());
////    }
//}
*/
