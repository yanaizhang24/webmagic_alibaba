package cn.yanf.webmagic.demo;


import cn.yanf.entity.AlibabaEN;

import cn.yanf.webmagic.piplline.MongodbPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class Alibaba implements PageProcessor{



    private Site site = Site.me().setRetryTimes(3).setSleepTime(500).addHeader("Referer","http://offer.alibaba.com");
    public void process(Page page) {
            List<AlibabaEN> list=new ArrayList<>() ;
            //site.getHeaders();
            //System.out.println(page.getHtml().toString());
            for(Selectable s : page.getHtml().xpath("//div[@class='item-main item-main-180 util-clearfix']").nodes()){
                if(s.xpath("//span[@class='ico-narr']").nodes().size()>0){
                    int i =1;
                    for(Selectable x:s.xpath("//span[@class='ico-narr']").nodes())
                    {
                        page.putField("page",page.getUrl());
                        page.putField("newTarget"+i,s.xpath("//div[@class='lwrap']/h2/a/@href"));
                        //page.putField("newTargetTitle"+i,s.xpath("//div[@class='lwrap']/h2/a/@href"));
                        AlibabaEN al=new AlibabaEN(page.getUrl().toString(),s.xpath("//div[@class='lwrap']/h2/a/@href").toString());
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
            Spider git=Spider.create(new cn.yanf.webmagic.demo.Alibaba()).
                    addUrl("http://offer.alibaba.com/products/led.html").
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


