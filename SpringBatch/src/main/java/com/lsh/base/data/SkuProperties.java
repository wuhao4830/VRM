package com.lsh.base.data;

import com.lsh.base.data.bean.PropertiesBean;
import com.lsh.base.data.utils.LoadProps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhao on 15/12/15.
 */
public class SkuProperties {
   List<PropertiesBean> list=new ArrayList<PropertiesBean>();
    private static Map methodstoreConf= SkuProperties.getMethodStoreProps();


    public void setList(String zzseaart,String lvorm,String cd,String specQt,String specUt,String grade,
                        String meins,String mtart,String attyp,String comp,String methodeat,String methodstore,int mhdrz,
                        int mhdhb,float laeng,float breit,float hoehe,String meabm,int brgew,String gewei,String specNam,String zzseasm,String zzseaem,String brand_type){


            list.add(new PropertiesBean("zzseaart",zzseaart));
            list.add(new PropertiesBean("lvorm",lvorm));
            list.add(new PropertiesBean("cd",cd));
            list.add(new PropertiesBean("specQt",specQt));
            list.add(new PropertiesBean("specUt",specUt));
            list.add(new PropertiesBean("grade",grade));
            list.add(new PropertiesBean("meins",meins));
            list.add(new PropertiesBean("mtart",mtart));
            list.add(new PropertiesBean("attyp",attyp));
            list.add(new PropertiesBean("comp",comp));
            list.add(new PropertiesBean("methodeat",methodeat));
            if(methodstoreConf.get(methodstore)!=null){
                methodstore = methodstoreConf.get(methodstore).toString();
            }
            list.add(new PropertiesBean("methodstore",methodstore));
            list.add(new PropertiesBean("mhdrz",mhdrz+""));
            list.add(new PropertiesBean("mhdhb",mhdhb+""));
            list.add(new PropertiesBean("laeng",laeng+""));
            list.add(new PropertiesBean("breit",breit+""));
            list.add(new PropertiesBean("hoehe",hoehe+""));
            list.add(new PropertiesBean("meabm",meabm+""));
            list.add(new PropertiesBean("brgew",brgew+""));
            list.add(new PropertiesBean("gewei",gewei+""));
            list.add(new PropertiesBean("specNam",specNam+""));
            list.add(new PropertiesBean("zzseaem",zzseaem));
            list.add(new PropertiesBean("zzseasm",zzseasm));
            list.add(new PropertiesBean("brand_type",brand_type));
            list.add(new PropertiesBean("product_country",""));
            list.add(new PropertiesBean("product_supply",""));
            list.add(new PropertiesBean("bar_code",""));
            list.add(new PropertiesBean("pack_length",""));
            list.add(new PropertiesBean("pack_width",""));
            list.add(new PropertiesBean("pack_height",""));
            list.add(new PropertiesBean("single_length",""));
            list.add(new PropertiesBean("single_width",""));
            list.add(new PropertiesBean("single_height",""));

    }
    public List<PropertiesBean>getList(){
        return list;
    }

    public static Map getMethodStoreProps(){
        Map<String,String> methodStoreProps = new HashMap<>();
        methodStoreProps.put("常温","01");
        methodStoreProps.put("低温","02");
        methodStoreProps.put("冷藏","03");
        return methodStoreProps;
    }
}
