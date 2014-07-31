package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Test2 {

	public static void main(String[] args) {
		System.out.println("=======================");
		List<String> aList = new ArrayList<String>();
		for (int i = 1; i < 10; i++)
			aList.add(i + "");
		
		String start="9";
		int s=aList.indexOf(start);
		for (int i = s; i < aList.size(); i++) {
			System.out.println(i+":"+aList.get(i));
		}
		
		Iterator< String> iterator=aList.iterator();
		ListIterator< String> listIterator=aList.listIterator(s);
	}

}
