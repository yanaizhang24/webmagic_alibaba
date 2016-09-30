package cn.yanf.webmagic.demo;


import cn.yanf.entity.AlibabaEN;

import cn.yanf.webmagic.piplline.MongodbPipeline;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.management.JMException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class Alibaba implements PageProcessor{


    private static int id=0;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(500).addHeader("Referer","http://offer.alibaba.com");
    public void process(Page page) {
            List<AlibabaEN> list=new ArrayList<>() ;
            //site.getHeaders();
            //System.out.println(page.getHtml().toString());
        //String noHtmlContent = content.replaceAll("<[^>]*>","");
            for(Selectable s : page.getHtml().xpath("//div[@class='item-main item-main-180 util-clearfix']").nodes()){
                if(s.xpath("//span[@class='ico-narr']").nodes().size()>0){
                    int i =1;
                    for(Selectable x:s.xpath("//span[@class='ico-narr']").nodes())
                    {
                        page.putField("page",page.getUrl());
                        page.putField("newTarget"+i,s.xpath("//div[@class='lwrap']/h2/a/@href"));
                        //page.putField("newTargetTitle"+i,s.xpath("//div[@class='lwrap']/h2/a/@href"));
                        id++;
                        AlibabaEN al=new AlibabaEN(new String(id+""),page.getUrl().toString(),
                                //s.xpath("//div[@class='lwrap']/h2/a/@href").toString()
                                s.xpath("//h3[@class='ellipsis']/a/@href").toString()
                        );
                        list.add(al);
                    }
                }
            }
            //如果找到，存入list
            if(list.size()>0){
                page.putField("list_ali",list);
            }
            //修改获取的href,不清楚为何错误
           for(String s:page.getHtml().xpath("//div[@class='ui2-pagination-pages']//a").links().all())
            {
                //需要去除一个"/products/fishing"
                System.out.println(s);
                String[] ss=s.split("/");
                List<String> list2=new ArrayList();
                for(int i=3;i<ss.length;i++){
                    if(!list2.contains(ss[i]))
                        list2.add(ss[i]);
                }
                s="http://offer.alibaba.com";
                for(String str:list2){
                    s+="/"+str;
                }
                //s=s.replaceFirst("/products/machine","");
                System.out.println(s);
                page.addTargetRequest(s);
            }

        }

        public Site getSite() {
            return site;
        }
        public static void main(String[] args) {
            //args[0]="led";
            //System.out.println(args[1]);
            //System.out.println(args.length);
            if(args.length==0){
                String fileName="reference.txt";
                File file=new File(fileName);
                if(file.exists())
                {
                    try {
                        args=readFile(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("未输入相关参数同时也未找到参数文件“reference.txt”!!");
                    return;
                }

            }
            String regex="[a-zA-Z0-9\\-_,]*";
            for(int i=1;i<args.length;i++){
                if("products".equals(args[i])){
                    System.out.println("参数不可为products！");
                    return;
                }
                if(!startCheck(regex,args[i])){
                    System.out.println(args[i]+"不符合要求");
                    System.out.println("请输入只包含数字字母和-_,的参数！");
                    return;
                }
            }
            StringBuffer stringBuffer=new StringBuffer(args[0]);
            for(int i=1;i<args.length;i++)
                stringBuffer.append(","+args[i]);
            System.out.println("解析个数："+args.length+",解析内容："+stringBuffer.toString());
            for(int i=0;i<args.length;i++){
                Spider git=Spider.create(new cn.yanf.webmagic.demo.Alibaba()).
                        addUrl("http://offer.alibaba.com/products/"+
                                //"led"
                                args[i]
                                +".html").
                        addPipeline(new ConsolePipeline()).addPipeline(new MongodbPipeline())
                        ;
                try {
                    SpiderMonitor.instance().register(git);
                } catch (JMException e) {
                    e.printStackTrace();
                }
                git.thread(5).run();

            }


        }
    public  static  boolean startCheck(String reg,String string)
    {
        boolean tem=false;

        Pattern pattern = Pattern.compile(reg);
        java.util.regex.Matcher matcher=pattern.matcher(string);

        tem=matcher.matches();
        return tem;
    }
    public static final String[] readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath)));
       List<String> args=new ArrayList<>();
        int i=0;
        for (String line = br.readLine(); line != null; line = br.readLine(),i++) {
            if(StringUtils.isNotEmpty(line.trim())){
                args.add(line.trim());
            }
        }

        br.close();
        return args.toArray(new String[]{});
    }

}


