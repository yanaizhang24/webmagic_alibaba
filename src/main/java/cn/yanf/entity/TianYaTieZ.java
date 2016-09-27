package cn.yanf.entity;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class TianYaTieZ {
    private String title;
    private String url;
    private String author;
    private String author_url;
    private String property;
    private String replyNum;
    private String readNum;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(String replyNum) {
        this.replyNum = replyNum;
    }

    public String getReadNum() {
        return readNum;
    }

    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    @Override
    public String toString() {
        return "TianYaTieZ{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", author_url='" + author_url + '\'' +
                ", property='" + property + '\'' +
                ", replyNum='" + replyNum + '\'' +
                ", readNum='" + readNum + '\'' +
                '}';
    }
}
