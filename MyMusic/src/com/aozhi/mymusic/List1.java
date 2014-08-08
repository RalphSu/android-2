package com.aozhi.mymusic;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class List1 extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
		getListView().setTextFilterEnabled(true);
	}

	private String[] mStrings = new String[] { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese", "Ami du Chambertin", "Anejo Enchilado",
			"Anneau du Vic-Bilh", "Anthoriro", "Appenzell", "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String",
			"Aromes au Gene de Marc", "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss", "Babybel",
			"Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon", "Barry's Bay Cheddar", "Basing",
			"Basket Cheese", "Bath Cheese", "Bavarian Bergkase", "Baylough", "Beaufort" };
}