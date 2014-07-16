package com.demo.sqlit.entity;



public class Person {
	private int id;
	private String name;
	private String phone;
	private int amount;

	
	public Person() {
		
	}
	public Person(int id,String name,String phone,int amount) {
		this.id=id;
		this.name=name;
		this.phone=phone;
		this.amount=amount;
		
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
@Override
public String toString() {
	return id+","+name+","+phone+","+amount+";";
}
}
