package cn.yanf.test;


import org.jsoup.Jsoup;


import java.io.IOException;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class Test1 {
    public void test1(){
        try {
            org.jsoup.nodes.Document document= Jsoup.connect("http://offer.alibaba.com/products/fishing/3.html").get();
            System.out.println(document.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Test1 test1=new Test1();
        test1.test1();
    }
}
