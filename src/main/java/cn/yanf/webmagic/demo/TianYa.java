package cn.yanf.webmagic.demo;

import cn.yanf.StringUtils.SpringUtils;
import cn.yanf.entity.TianYaTieZ;
import cn.yanf.webmagic.piplline.MongodbPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

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
        for(Selectable s:page.getHtml().xpath("//table[@class='tab-bbs-list tab-bbs-list-2']/tbody").nodes()){
            if(s.xpath("//th").nodes().size()>0){
                continue;
            }
            for(Selectable selectable:s.xpath("/tbody/tr").nodes()){
                TianYaTieZ tianYaTieZ=new TianYaTieZ();
                tianYaTieZ.setProperty(selectable.xpath("//span[@class='face']/@title").toString());
                //如果后面有多个<b>，你可以用 //a/following-sibling::b来得到所有的b，或者用 //a/following-sibling::b[index] 来得到特定的。index从1开始
                tianYaTieZ.setTitle(SpringUtils.trim(selectable.xpath("//td[@class='td-title faceblue']/a/text()").toString()));
                tianYaTieZ.setUrl(SpringUtils.trim(selectable.xpath("//td[@class='td-title faceblue']/a/@href").toString()));
                tianYaTieZ.setAuthor(SpringUtils.trim(selectable.xpath("//a[@class='author']/text()").toString()));
                tianYaTieZ.setAuthor_url(SpringUtils.trim(selectable.xpath("//a[@class='author']/@href").toString()));
                tianYaTieZ.setReadNum(SpringUtils.trim(selectable.xpath("/tr/td[3]/text()").toString()));
                tianYaTieZ.setReplyNum(SpringUtils.trim(selectable.xpath("/tr/td[4]/text()").toString()));
                System.out.println(tianYaTieZ.toString());
            }

        }
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
                            "http://bbs.tianya.cn/list-develop-1.shtml"
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
