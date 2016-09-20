package cn.yanf.StringUtils;


import cn.yanf.entity.Customer;

import org.springframework.boot.CommandLineRunner;



import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30 0030.
 */

public class SpringUtils implements Serializable,CommandLineRunner {
    private static String message;
    public  static void getMessage (String message) throws Exception {
        SpringUtils.message=message;
    }

    @Override
    public void run(String... strings) throws Exception {

    }
    public  static <T> boolean writeData(List<T> list,String fileName){
        Date date=new Date();
        DateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        File file=new File(fileName+dateFormat.format(date)+".txt");
        if(!file.exists()){//文件不存在
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter=new FileWriter(file.getName(),true);
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

            bufferedWriter.write(dateFormatter.format(date));
            bufferedWriter.newLine();
            for(T t:list){
                bufferedWriter.write(t.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
