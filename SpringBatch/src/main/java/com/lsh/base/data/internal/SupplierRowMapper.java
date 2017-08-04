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

import com.lsh.base.data.bean.Sitearticle;
import com.lsh.base.data.bean.Supplier;
import com.lsh.base.data.utils.LoadProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SupplierRowMapper implements RowMapper<Supplier> {

    private static final Logger logger = LoggerFactory.getLogger(SupplierRowMapper.class);
    private static final String LIFNR="LIFNR";
    private static final String NAME1="NAME1";
    private static final String MANDT="MANDT";
    private static final Map marketConf= LoadProps.getMarketProps();

	public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {

        String marketKey=rs.getString(MANDT);
        int marketId=0;
        if(marketConf.get(marketKey)!=null){
            marketId=Integer.parseInt((String)marketConf.get(marketKey));
        }else {
            logger.info("is null"+marketKey);
        }
        Supplier supplier = new Supplier();
        supplier.setMarketId(marketId);
        supplier.setName(rs.getString(NAME1));
        supplier.setSupNo(rs.getString(LIFNR));
        return supplier;
	}

}
