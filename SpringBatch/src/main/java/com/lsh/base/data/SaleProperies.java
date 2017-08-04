package com.lsh.base.data;

import com.lsh.base.data.bean.PropertiesBean;
import com.lsh.base.data.utils.PackUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhao on 15/12/15.
 */
public class SaleProperies {
    List<PropertiesBean> list=new ArrayList<PropertiesBean>();
    public void setList(String infnr,String ekorg,String esokz,String meins,int umrez,int umren,String idnlf,String rdprf){
        list.add(new PropertiesBean("infnr",infnr+""));
        list.add(new PropertiesBean("ekorg",ekorg+""));
        list.add(new PropertiesBean("esokz",esokz+""));
        list.add(new PropertiesBean("meins",umrez+""));
        list.add(new PropertiesBean("umrez",umrez+""));
        list.add(new PropertiesBean("umren",umren+""));
        list.add(new PropertiesBean("idnlf",idnlf));
        list.add(new PropertiesBean("rdprf",rdprf));
        list.add(new PropertiesBean("brand_type",""));
        list.add(new PropertiesBean("supplier_code",""));
    }
    public List<PropertiesBean> getList(){
        return list;
    }

}
