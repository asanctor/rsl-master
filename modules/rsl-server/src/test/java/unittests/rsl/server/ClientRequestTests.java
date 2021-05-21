package unittests.rsl.server;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rsl.server.ClientRequest;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class ClientRequestTests {

    @Test
    public void stringAndIntegerParametersTest()
    {

        HashMap<String, Object> params = new HashMap<>();
        params.put("param1", 8876);
        params.put("param2", "8876");
        params.put("param3", "test");

        ClientRequest req = new ClientRequest("fooCommand", params);

        HashMap<String, Object> test = req.getQueryParameters();
        Assert.assertNotNull(test);

        int param1_int = req.getQueryParameter("param1", Integer.class);
        Assert.assertEquals(8876, param1_int);

        String param1_string = req.getQueryParameter("param1", String.class);
        Assert.assertEquals("8876", param1_string);

        long param1_long = req.getQueryParameter("param1", Long.class);
        Assert.assertEquals(8876, param1_long);

        int param2_int = req.getQueryParameter("param2", Integer.class);
        Assert.assertEquals(8876, param2_int);

        String param2_string = req.getQueryParameter("param2", String.class);
        Assert.assertEquals("8876", param2_string);


        int param3_int = 0;
        boolean fail = false;
        try{
            param3_int = req.getQueryParameter("param3", Integer.class);
        }catch(NullPointerException e) {
            fail = true;
        }
        Assert.assertTrue(fail);

    }

    @Test
    public void listParameterTest()
    {
        HashMap<String, Object> params = new HashMap<>();
        List<Integer> param1 = Arrays.asList(1,2,3,4);
        params.put("param1", param1);

        ClientRequest req = new ClientRequest("fooCommand", params);

        List param1_list = req.getQueryParameter("param1", List.class);
        Assert.assertNotNull(param1_list);
        Assert.assertEquals(param1.size(), param1_list.size());

    }


    @Test
    public void mapParameterTest()
    {
        HashMap<String, Object> params = new HashMap<>();

        HashMap<String, Object> param1 = new HashMap<>();
        param1.put("a", 1);
        param1.put("b", "2");
        param1.put("c", 3);
        params.put("param1", param1);

        ClientRequest req = new ClientRequest("fooCommand", params);

        Map param1_map = req.getQueryParameter("param1", HashMap.class);
        Assert.assertNotNull(param1_map);
        Assert.assertEquals(param1.size(), param1_map.size());

    }

    @Test
    public void mapConversionParameterTest()
    {
        HashMap<String, Object> params = new HashMap<>();

        LinkedTreeMap<String, Object> param1 = new LinkedTreeMap<>();
        param1.put("a", 1);
        param1.put("b", "2");
        param1.put("c", 3);
        params.put("param1", param1);

        ClientRequest req = new ClientRequest("fooCommand", params);

        Map param1_map = req.getQueryParameter("param1", HashMap.class);
        Assert.assertNotNull(param1_map);
        Assert.assertEquals(param1.size(), param1_map.size());

    }




}
