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

import com.lsh.base.data.bean.Cusarticle;
import com.lsh.base.data.bean.Sitearticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CusarticleRowMapper implements RowMapper<Cusarticle> {

    private static final Logger logger = LoggerFactory.getLogger(CusarticleRowMapper.class);
    public static final String VKORG="VKORG";
    public static final String VTWEG="VTWEG";
    public static final String KUNNR="KUNNR";
    public static final String KDMAT="KDMAT";
    public static final String MATNR="MATNR";
    public static final String KDPTX="KDPTX";
    public static final String KBETR="KBETR";
    public static final String mmsta="mmsta";
    private static final String MANDT="MANDT";

	public Cusarticle mapRow(ResultSet rs, int rowNum) throws SQLException {

        Cusarticle cusarticle = new Cusarticle();
        cusarticle.setMandt(rs.getString(MANDT));
        cusarticle.setSkuId(rs.getString(MATNR));
        cusarticle.setVkorg(rs.getString(VKORG));
        cusarticle.setVtweg(rs.getString(VTWEG));
        cusarticle.setKunnr(rs.getString(KUNNR));
        cusarticle.setKdmat(rs.getString(KDMAT));
        cusarticle.setKdptx(rs.getString(KDPTX));
        cusarticle.setKbetr(rs.getFloat(KBETR));
        cusarticle.setMmsta(rs.getString(mmsta));

        return cusarticle;
	}

}
