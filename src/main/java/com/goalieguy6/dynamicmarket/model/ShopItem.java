package com.goalieguy6.dynamicmarket.model;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dynamicmarket")
public class ShopItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;
	
	@NotNull
	private int item;
	private int subtype = 0;
	
	@NotNull
	@NotEmpty
	@Length(max=64)
	private String name;
	
	private int count = 1;
	private int stock = 0;

	private double price = 0;
	private int tax = 0;
	private int volatility = 0;
	
	private boolean buyable = true;
	private boolean sellable = true;
	
	@NotNull
	private String permission = "";
	
	public ShopItem() {
		
	}
	
	public int getId() {
		return id;
	}
	
	public int getItemId() {
		return item;
	}
	
	public void setItemId(int item) {
		this.item = item;
	}
	
	public int getItemSubtype() {
		return subtype;
	}
	
	public void setItemSubtype(int subtype) {
		this.subtype = subtype;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getStock() {
		return stock;
	}
	
	public void setStock(int stock) {
		this.stock = stock;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public int getTax() {
		return tax;
	}
	
	public void setTax(int tax) {
		this.tax = tax;
	}
	
	public int getVolatility() {
		return volatility;
	}
	
	public void setVolatility(int volatility) {
		this.volatility = volatility;
	}
	
	public boolean isBuyable() {
		return buyable;
	}
	
	public void setBuyable(boolean buyable) {
		this.buyable = buyable;
	}
	
	public boolean isSellable() {
		return sellable;
	}
	
	public void setSellable(boolean sellable) {
		this.sellable = sellable;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
}
