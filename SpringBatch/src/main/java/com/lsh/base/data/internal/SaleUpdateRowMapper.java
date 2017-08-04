package com.lsh.base.data.internal;

import com.lsh.base.data.bean.SaleUpdate;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by wuhao on 15/12/30.
 */
public class SaleUpdateRowMapper implements RowMapper<SaleUpdate> {
    private static final Logger logger = LoggerFactory.getLogger(SaleUpdateRowMapper.class);

    private static final String MATNR="matnr";
    private static final String ASORT="asort";
    private static final String MMSTA="mmsta";
    private static final String MANDT="mandt";
    private static final Map marketConf= LoadProps.getMarketProps();

    public SaleUpdate mapRow(ResultSet rs, int rowNum) throws SQLException{
        SaleUpdate saleUpdate=new SaleUpdate();
        int marketId=0;
        String key=rs.getString(MANDT);
        String ugroup=rs.getString(ASORT);
        if(marketConf.get(key)!=null){
            marketId=Integer.parseInt((String)marketConf.get(key));
        }
        saleUpdate.setUgroupSys("");
        saleUpdate.setMarketId(marketId);
        saleUpdate.setSkuId(rs.getString(MATNR));
        saleUpdate.setSapStatus(rs.getString(MMSTA));
        saleUpdate.setUgroup(ugroup);
        return saleUpdate;
    }
}
