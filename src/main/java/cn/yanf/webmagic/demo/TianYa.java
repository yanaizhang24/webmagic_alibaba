package cn.yanf.webmagic.demo;

import cn.yanf.webmagic.piplline.MongodbPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class TianYa implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(500);

    public void process(Page page) {
        page.getHtml();
        //<a href="/list.jsp?item=no05&amp;nextid=1474857572000" rel="nofollow">下一页</a>
        page.addTargetRequests(page.getHtml().xpath("//div[@class='short-pages-2 clearfix']//a")
                .links().regex(".*/list.jsp\\?item=.*&nextid=\\d*").all());
    }
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {



        //StringBuffer stringBuffer=new StringBuffer(args[0]);
        //for(int i=1;i<args.length;i++)
            //stringBuffer.append(","+args[i]);
       // System.out.println("解析个数："+args.length+",解析内容："+stringBuffer.toString());

            Spider git=Spider.create(new TianYa()).
                    addUrl(
                            //"http://bbs.tianya.cn/list-no05-1.shtml"
                            "http://bbs.tianya.cn/list-666-1.shtml"
                    ).
                    addPipeline(new ConsolePipeline())
                    //.addPipeline(new MongodbPipeline())
                    ;
            try {
                SpiderMonitor.instance().register(git);
            } catch (JMException e) {
                e.printStackTrace();
            }
            git.thread(5).run();



    }
}
