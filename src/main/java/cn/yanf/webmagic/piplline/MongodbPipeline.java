package cn.yanf.webmagic.piplline;


import cn.yanf.StringUtils.SpringUtils;
import cn.yanf.entity.TieBar;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class MongodbPipeline implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
      // 连接到 mongodb 服务
      public static MongoClient mongoClient = new MongoClient(
              new MongoClientURI("uri" ) );

    public MongodbPipeline() {
    }
    private List<TieBar> t;
    public void process(ResultItems resultItems, Task task) {
//        System.out.println("get page: " + resultItems.getRequest().getUrl());
//        Iterator i$ = resultItems.getAll().entrySet().iterator();
//        while(i$.hasNext()) {
//            Map.Entry entry = (Map.Entry)i$.next();
//            System.out.println((String)entry.getKey() + ":\t" + entry.getValue());
//        }
        Map<String,Object> result=resultItems.getAll();
        //是列表页信息
        if(resultItems.getAll().containsKey("pageInfo")){
//            SpringUtils.writeData((List<TieBar>)resultItems.getAll().get("list_ali"),"ali");
            try {
                Document document=new Document();
                document.append("pageUrl",result.get("pageUrl"));
                document.append("bugLists",result.get("bugLists"));
                mongoClient.getDatabase("ku").getCollection("bug_page_info").insertOne(document);
            } catch (Exception e) {
                logger.error("mongodb",e);
            }

        }
        //是详情页信息
        if(resultItems.getAll().containsKey("bugInfo")){
//            SpringUtils.writeData((List<TieBar>)resultItems.getAll().get("list_ali"),"ali");
            try {
                Document document=new Document(result);
//                document.append("pageUrl",result.get("pageUrl"));
//                document.append("bugLists",result.get("bugLists"));
                mongoClient.getDatabase("ku").getCollection("bug_all_info").insertOne(document);
            } catch (Exception e) {
                logger.error("mongodb",e);
            }


        }


    }
}
