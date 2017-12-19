package cn.potmart.geo.process.test;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/12/15.
 */
public class JSON {


    public static void main(String[] args) {
        //Object object = com.alibaba.fastjson.JSON.parse("adad");

        /*try{
            String a = assertString(null);
            out.println(a);
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }*/

        loop();

    }

    public static String assertString(String m) {
        Assert.notNull(m, "value cant be null");
        return m;
    }

    private static void loop() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try{
                if (i == 8) {
                    new Integer("sdc");
                }
            }catch (Exception e) {
                e.printStackTrace();
                //continue;
            }

            list.add(new Integer(i));

        }

        out.print(list);
    }
}
