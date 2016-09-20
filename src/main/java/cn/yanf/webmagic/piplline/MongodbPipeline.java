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
    public MongodbPipeline() {
    }
    private List<TieBar> t;
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        Iterator i$ = resultItems.getAll().entrySet().iterator();
        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            System.out.println((String)entry.getKey() + ":\t" + entry.getValue());
        }
        if(resultItems.getAll().containsKey("list_ali")){
            SpringUtils.writeData((List<TieBar>)resultItems.getAll().get("list_ali"),"ali");
        }



    }
}
