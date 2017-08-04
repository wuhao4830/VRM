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

public class Article {
	private long id;
	private String skuId;
	private int marketId;
	private String brand;
	private String name;
	private String tax;
	private String barcode;
	private String topCat;
	private String secondCat;
	private String thirdCat;
	private String properties;
	private int isValid;
	private int status;
	private String createdBy;
	private long createAt;
	private String updatedBy;
	private String picUrl;
	private int isOld;
	private long updatedAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getTopCat() {
		return topCat;
	}

	public void setTopCat(String topCat) {
		this.topCat = topCat;
	}

	public String getSecondCat() {
		return secondCat;
	}

	public void setSecondCat(String secondCat) {
		this.secondCat = secondCat;
	}

	public String getThirdCat() {
		return thirdCat;
	}

	public void setThirdCat(String thirdCat) {
		this.thirdCat = thirdCat;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
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

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getIsOld() {
		return isOld;
	}

	public void setIsOld(int isOld) {
		this.isOld = isOld;
	}

	public Article(){
		this.status=1;
		this.isValid=1;
		this.picUrl="";
		this.isOld=1;

	}

	@Override
	public String toString() {
		return "Article{" +
				"id=" + id +
				", skuId='" + skuId + '\'' +
				", marketId=" + marketId +
				", brand='" + brand + '\'' +
				", name='" + name + '\'' +
				", tax='" + tax + '\'' +
				", barcode='" + barcode + '\'' +
				", topCat='" + topCat + '\'' +
				", secondCat='" + secondCat + '\'' +
				", thirdCat='" + thirdCat + '\'' +
				", properties='" + properties + '\'' +
				", isValid=" + isValid +
				", status=" + status +
				", createdBy='" + createdBy + '\'' +
				", createAt=" + createAt +
				", updatedBy='" + updatedBy + '\'' +
				", picUrl='" + picUrl + '\'' +
				", isOld=" + isOld +
				", updatedAt=" + updatedAt +
				'}';
	}
}
