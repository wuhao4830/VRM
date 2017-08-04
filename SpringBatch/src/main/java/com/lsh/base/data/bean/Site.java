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

package com.lsh.base.data.bean;
import java.math.BigDecimal;
import java.util.Date;

public class Site {
	private long id;
	private String bwscl;
	private String prerf;
	private String rbzul;
	private String scagr;
	private String kwdht;
	private BigDecimal pprice;
	private BigDecimal sprice;
	private int marketId;
	private int packSize;
	private String skuId;
	private String dcId;
	private int isValid;
	private int status;
	private long createdAt;
	private long updatedAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBwscl() {
		return bwscl;
	}

	public void setBwscl(String bwscl) {
		this.bwscl = bwscl;
	}

	public String getPrerf() {
		return prerf;
	}

	public void setPrerf(String prerf) {
		this.prerf = prerf;
	}

	public String getRbzul() {
		return rbzul;
	}

	public void setRbzul(String rbzul) {
		this.rbzul = rbzul;
	}

	public BigDecimal getPprice() {
		return pprice;
	}

	public void setPprice(BigDecimal pprice) {
		this.pprice = pprice;
	}

	public String getScagr() {
		return scagr;
	}

	public void setScagr(String scagr) {
		this.scagr = scagr;
	}

	public BigDecimal getSprice() {
		return sprice;
	}

	public void setSprice(BigDecimal sprice) {
		this.sprice = sprice;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public int getPackSize() {
		return packSize;
	}

	public void setPackSize(int packSize) {
		this.packSize = packSize;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getDcId() {
		return dcId;
	}

	public void setDcId(String dcId) {
		this.dcId = dcId;
	}

	public String getKwdht() {
		return kwdht;
	}

	public void setKwdht(String kwdht) {
		this.kwdht = kwdht;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Site(){
		this.packSize=0;
		this.isValid=1;
		this.status=1;
		this.kwdht="";
		this.createdAt=new Date().getTime()/1000;
		this.updatedAt=new Date().getTime()/1000;
	}

	@Override
	public String toString() {
		return "Site{" +
				"id=" + id +
				", bwscl='" + bwscl + '\'' +
				", prerf='" + prerf + '\'' +
				", rbzul='" + rbzul + '\'' +
				", scagr='" + scagr + '\'' +
				", kwdht='" + kwdht + '\'' +
				", pprice=" + pprice +
				", sprice=" + sprice +
				", marketId=" + marketId +
				", packSize=" + packSize +
				", skuId='" + skuId + '\'' +
				", dcId='" + dcId + '\'' +
				", isValid=" + isValid +
				", status=" + status +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}
}
