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


public class Sale {
	private long id;
	private String skuId;
	private int marketId;
	private String supNo;
	private BigDecimal netPrice;
	private int minQty;
	private int maxQty;
	private int refundable;
	private int isValid;
	private int dueDay;
	private String unit;
	private long effectedAt;
	private long endAt;
	private String properties;
	private long createAt;
	private long updateAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getSupNo() {
		return supNo;
	}

	public void setSupNo(String supNo) {
		this.supNo = supNo;
	}

	public BigDecimal getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(BigDecimal netPrice) {
		this.netPrice = netPrice;
	}

	public int getMinQty() {
		return minQty;
	}

	public void setMinQty(int minQty) {
		this.minQty = minQty;
	}

	public int getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(int maxQty) {
		this.maxQty = maxQty;
	}

	public int getRefundable() {
		return refundable;
	}

	public void setRefundable(int refundable) {
		this.refundable = refundable;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public int getDueDay() {
		return dueDay;
	}

	public void setDueDay(int dueDay) {
		this.dueDay = dueDay;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getEffectedAt() {
		return effectedAt;
	}

	public void setEffectedAt(long effectedAt) {
		this.effectedAt = effectedAt;
	}

	public long getEndAt() {
		return endAt;
	}

	public void setEndAt(long endAt) {
		this.endAt = endAt;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public long getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(long updateAt) {
		this.updateAt = updateAt;
	}

	public Sale() {
		this.isValid=1;
		this.createAt=(new Date().getTime())/1000;
		this.updateAt=(new Date().getTime())/1000;
	}

	@Override
	public String toString() {
		return "Sale{" +
				"id=" + id +
				", skuId='" + skuId + '\'' +
				", marketId=" + marketId +
				", supNo='" + supNo + '\'' +
				", netPrice=" + netPrice +
				", minQty=" + minQty +
				", maxQty=" + maxQty +
				", refundable=" + refundable +
				", isValid=" + isValid +
				", dueDay=" + dueDay +
				", unit='" + unit + '\'' +
				", effectedAt=" + effectedAt +
				", endAt=" + endAt +
				", properties='" + properties + '\'' +
				", createAt=" + createAt +
				", updateAt=" + updateAt +
				'}';
	}
}
