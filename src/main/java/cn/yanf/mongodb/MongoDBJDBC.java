package cn.yanf.mongodb;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class MongoDBJDBC {
    public static void main( String args[] ){
        try{
            // 连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

            // 连接到数据库,没有数据库会新建，但是新建的数据库在rebomongo里不显示，需要增加collection
            MongoDatabase mongoDatabase = mongoClient.getDatabase("test_1");
            System.out.println("Connect to database successfully");
            //mongoDatabase.createCollection("test");
            //document为key-value格式
            MongoCollection<Document> collection = mongoDatabase.getCollection("test");
            Document document=new Document("title","mongodb").
                    append("description", "database").
                    append("likes", 100).
                    append("by", "Fly");
            List<Document> documents=new ArrayList<>();
            documents.add(document);
            collection.insertMany(documents);
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}

