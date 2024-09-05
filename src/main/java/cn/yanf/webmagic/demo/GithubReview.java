package cn.yanf.webmagic.demo;


import cn.yanf.entity.AlibabaEN;
import cn.yanf.webmagic.piplline.MongodbPipeline;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import javax.management.JMException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class GithubReview implements PageProcessor{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Set<String> bugLists=new LinkedHashSet<>();

    public Set<String> pageLists=new LinkedHashSet<>();

    //列表页
    //https://github.com/advisories?page=1&query=type%3Areviewed+ecosystem%3Amaven

    public static final String URL_LIST = "https://github\\.com/advisories\\?page=\\d+\\&\\S+";
    //详情页https://github.com/advisories/GHSA-c6c3-h4f7-3962
    public static final String URL_POST = "https://github\\.com/advisories/\\S+";

    private Site site = Site.me().setRetryTimes(3).setTimeOut(10000).setSleepTime(2000).addHeader("Referer","https://github.com/advisories")
                .setUserAgent(
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        page.putField("pageUrl",page.getUrl().toString());
        //列表页
        if (page.getUrl().regex(URL_LIST).match()) {
            //先排出所有的link
            List<String> infoLists=page.getHtml().xpath("//div[@class=\"flex-auto col-12 col-lg-9\"]").links().regex(URL_POST).all();
//            System.out.println(JSON.toJSONString(infoLists));
            page.putField("pageInfo","pageInfo");
            page.putField("bugLists",infoLists);
            bugLists.addAll(infoLists);
//                page.addTargetRequests(page.getHtml().xpath("//div[@class=\"flex-auto col-12 col-lg-9\"]").links().regex(URL_POST).all());
            //next page
            List<String> nextPage=page.getHtml().xpath("//a[@class=\"next_page\"]").links().all();
            if(nextPage==null||nextPage.size()==0){
                System.out.println("bugLists:"+JSON.toJSONString(bugLists)+"\n");
                System.out.println("pageLists:"+JSON.toJSONString(pageLists));
            }else{
                pageLists.addAll(nextPage);
//            System.out.println(JSON.toJSONString(nextPage));
                page.addTargetRequests(nextPage);
                page.addTargetRequests(infoLists);
            }


        } else {
            page.putField("bugInfo","bugInfo");
            page.putField("bugUrl",page.getUrl().toString());
            page.putField("bugPageInfo",page.getHtml().toString());
            List<String> list=new ArrayList<>();
            try {
                list=page.getHtml().xpath("//div[@class='discussion-sidebar-item']/div/text()").all();
                if(list!=null){
                    for(String info:list){
                        if(info.contains("CVE")){
                            page.putField("CVEID",info);
                        }
                        if(info.contains("GHSA")){
                            page.putField("GHSAID",info);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("list",e);
            }
//            page.putField("CVEID",page.getHtml().xpath("//div[@class='discussion-sidebar-item']/div[contains(text(),'CVE-2024-42469')]").toString());
//            page.putField("GHSAID",page.getHtml().xpath("//div[@class='discussion-sidebar-item']/div[@class='color-fg-muted' AND contains(text(),'GHSA')]").toString());
            try {
                page.putField("SOURCE",page.getHtml().xpath("//div[@class='Subhead-description']").links().toString());
            } catch (Exception e) {
                logger.error("SOURCE",e);
            }
            try {
                page.putField("describe",page.getHtml().xpath("//h2[@class='lh-condensed Subhead-heading Subhead-heading--large']/text()").toString());
            } catch (Exception e) {
                logger.error("describe",e);
            }
            try {
                page.putField("package",page.getHtml().xpath("//div[@class='Box Box--responsive']//span[@class='f4 color-fg-default text-bold']/text()").toString());
            } catch (Exception e) {
                logger.error("package",e);
            }
//            try {
//                page.putField("affected",page.getHtml().xpath("//div[@class='Box Box--responsive']").toString());
//            } catch (Exception e) {
//                logger.error("affected",e);
//            }
            try {
                List<String> apLists=page.getHtml().xpath("//div[@class='Box Box--responsive']//div[@class='f4 color-fg-default']/text()").all();
                if(apLists!=null){
                    page.putField("apLists",apLists);
                    if(apLists.size()>=2){
                        page.putField("affected",apLists.get(0));
                        page.putField("patched",apLists.get(1));
                    }else{
                        page.putField("affected",apLists.get(0));
                    }

                }
//                page.putField("patched",page.getHtml().xpath("//div[@class='Box Box--responsive']//h2[contains(text(),'Patched versions')]").toString());
            } catch (Exception e) {
                logger.error("apLists",e);
            }
            //详情页
//                page.putField("title", page.getHtml().xpath("//div[@class='articalTitle']/h2"));
//                page.putField("content", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
//                page.putField("date",
//                        page.getHtml().xpath("//div[@id='articlebody']//span[@class='time SG_txtc']").regex("\\((.*)\\)"));

            try {
                List<String> allInfos=page.getHtml().xpath("//div[@class='markdown-body comment-body p-0']/p/text()").all();
                if(allInfos!=null){
                    page.putField("allInfos",allInfos);
//                    if(apLists.size()>=2){
//                        page.putField("affected",apLists.get(0));
//                        page.putField("patched",apLists.get(1));
//                    }else{
//                        page.putField("affected",apLists.get(0));
//                    }

                }
//                page.putField("patched",page.getHtml().xpath("//div[@class='Box Box--responsive']//h2[contains(text(),'Patched versions')]").toString());
            } catch (Exception e) {
                logger.error("allInfos",e);
            }

            try {
                List<String> references=page.getHtml().xpath("//div[@class='markdown-body comment-body p-0']//ul").links().all();
                if(references!=null){
                    page.putField("references",references);
//                    if(apLists.size()>=2){
//                        page.putField("affected",apLists.get(0));
//                        page.putField("patched",apLists.get(1));
//                    }else{
//                        page.putField("affected",apLists.get(0));
//                    }

                }
//                page.putField("patched",page.getHtml().xpath("//div[@class='Box Box--responsive']//h2[contains(text(),'Patched versions')]").toString());
            } catch (Exception e) {
                logger.error("references",e);
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
//        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
                new Proxy("127.0.0.1",10809)
                ));
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "10809");
//        System.setProperty("https.proxyPort", "10809");

        Spider.create(new GithubReview())

                .addUrl("https://github.com/advisories?page=1&query=type%3Areviewed+ecosystem%3Amaven")

                .setDownloader(httpClientDownloader)
//                .addPipeline(new ConsolePipeline())
                .addPipeline(new MongodbPipeline())
//                    .addPipeline(SpringContextHolder.getBean("MongodbPipeline"))
                .run();
    }

}


