/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsh.base.data.internal;

import com.alibaba.fastjson.JSON;
import com.lsh.base.data.SkuProperties;
import com.lsh.base.data.bean.Article;
import com.lsh.base.data.bean.IssueInfo;
import com.lsh.base.data.jira.KafkaProducer;
import com.lsh.base.data.utils.LoadProps;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lsh.base.data.bean.IssueBean;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ArticleRowMapper implements RowMapper<Article> {
    private static final Logger logger = LoggerFactory.getLogger(ArticleRowMapper.class);
    private static final String MANDT="MANDT";
    private static final String MATNR="MATNR";
    private static final String MAKTX="MAKTX";
    private static final String EXTWG="EXTWG";
    private static final String ZZSEAART="ZZSEAART";
    private static final String ERSDA="ERSDA";
    private static final String MATKL="MATKL";
    private static final String PUUNIT="PUUNIT";
    private static final String LVORM="LVORM";
    private static final String TAKLV="TAKLV";
    private static final String TAKLV1="TAKLV1";
    private static final String EAN11="EAN11";
    private static final String CD="CD";
    private static final String SPEC_QT="SPEC_QT";
    private static final String SPEC_UT="SPEC_UT";
    private static final String GRADE="GRADE";
    private static final String MEINS="MEINS";
    private static final String FREE_CHAR="FREE_CHAR";
    private static final String MTART="MTART";
    private static final String ATTYP="ATTYP";
    private static final String COMP="COMP";
    private static final String METHODEAT="METHODEAT";
    private static final String METHODSTORE="METHODSTORE";
    private static final String MHDRZ="MHDRZ";
    private static final String MHDHB="MHDHB";
    private static final String LAENG="LAENG";
    private static final String BREIT="BREIT";
    private static final String HOEHE="HOEHE";
    private static final String MEABM="MEABM";
    private static final String BRGEW="BRGEW";
    private static final String GEWEI="GEWEI";
    private static final String ERNAM="ERNAM";
    private static final String LAEDA="LAEDA";
    private static final String AENAM="AENAM";
    private static final String SPEC_NAM="SPEC_NAM";
    private static final String ZZSEASM="ZZSEASM";
    private static final String ZZSEAEM="ZZSEAEM";
    private static final String BRAND_ID="BRAND_ID";
    private static final long MAX=4294967295l;
    private static Map marketConf= LoadProps.getMarketProps();

    public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        Article article = new Article();
        SkuProperties properties=new SkuProperties();
        try {
            String marketKey=rs.getString(MANDT);
            int marketId=0;
            if(marketConf.get(marketKey)!=null){
               marketId=Integer.parseInt((String)marketConf.get(marketKey));
            }
            article.setMarketId(marketId);
            article.setSkuId(rs.getString(MATNR));
            article.setName(rs.getString(MAKTX));
            article.setThirdCat(rs.getString(EXTWG));
            Date createDate = format.parse(rs.getString(ERSDA));
            if(createDate.getTime()/1000>MAX){
                article.setCreateAt(MAX);
            }
            else if(createDate.getTime()/1000>0){
                article.setCreateAt(createDate.getTime() / 1000);
            }else
                article.setCreateAt(0);

            article.setSecondCat(rs.getString(MATKL));
            article.setTopCat(rs.getString(PUUNIT));
            article.setTax(rs.getString(TAKLV1));
            article.setBarcode(rs.getString(EAN11));
            article.setBrand(rs.getString(FREE_CHAR));
            article.setCreatedBy(rs.getString(ERNAM));
            Date updateDate = format.parse(rs.getString(LAEDA));
            if(updateDate.getTime()/1000>MAX){
                article.setUpdatedAt(MAX);
            }
            if(updateDate.getTime()>0) {
                article.setUpdatedAt(updateDate.getTime() / 1000);
            }else
                article.setUpdatedAt(0);

            article.setUpdatedBy(rs.getString(AENAM));

            properties.setList(rs.getString(ZZSEAART),rs.getString(LVORM),rs.getString(CD),rs.getString(SPEC_QT),
                    rs.getString(SPEC_UT),rs.getString(GRADE),rs.getString(MEINS),rs.getString(MTART),rs.getString(ATTYP),rs.getString(COMP)
            ,rs.getString(METHODEAT),rs.getString(METHODSTORE),rs.getInt(MHDRZ),rs.getInt(MHDHB),rs.getFloat(LAENG),rs.getFloat(BREIT),rs.getFloat(HOEHE),rs.getString(MEABM)
            ,rs.getInt(BRGEW),rs.getString(GEWEI),rs.getString(SPEC_NAM),rs.getString(ZZSEAEM),rs.getString(ZZSEASM),rs.getString(BRAND_ID));

            JSONArray object = JSONArray.fromObject(properties.getList());
            article.setProperties(object.toString());
        }catch (Exception e){
            logger.error(String.format("article error:%s", e.toString()));
            //发消息
            IssueInfo info=new IssueInfo();
            String desc="error:"+e.toString()+",value:"+ JSON.toJSON(article).toString();
            IssueBean bean=new IssueBean();
            bean.setDescription(desc);
            info.setIssue(bean);
            try {
                KafkaProducer kafkaProducer=KafkaProducer.getInstance();
                kafkaProducer.produce(info);
            }catch (Exception error){
                logger.error(error.getMessage(),error);
            }

        }
        return article;
	}

}
