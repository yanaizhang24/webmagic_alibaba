package cn.yanf.entity;



/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class Customer {

    public String id;
    public String firstName;
    public String lastName;

    public Customer(){}

    public Customer(String firstName,String lastName){
        this.firstName=firstName;
        this.lastName=lastName;
    }
    @Override
    public String toString(){
        return String.format("Cuntomer[id]=%s,firstName='%s',lastName='%s'",id,firstName,lastName);
    }
}
