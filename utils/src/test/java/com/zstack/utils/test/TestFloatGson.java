package com.zstack.utils.test;

import com.google.gson.Gson;
import org.junit.Test;

/**
 * Created by chukun.lu on 7/15/16.
 */
public class TestFloatGson {
    public class TestFloat{
        public double dou;
        public float flo;
    }
    @Test
    public void test(){
        TestFloat b =new TestFloat();
        b.dou = 999999999;
        b.flo = 999999999;
        Gson gson = new Gson();
        String str = gson.toJson(b);
        System.out.println(str);
        String testFloat ="99999799";
        float resultFloat = gson.fromJson(testFloat,float.class);
        System.out.println("float:"+resultFloat);
        double resultDouble = gson.fromJson(testFloat,double.class);
        System.out.println("double:"+resultDouble);

    }
}
