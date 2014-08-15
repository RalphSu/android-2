package org.app.qqmusicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import org.app.netmusic.tool.NetMusicAdapter;
import org.app.netmusic.tool.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MusicOnlineActivity extends Activity {
	// ���еľ�̬����
	public static final String URL = "http://api.androidhive.info/music/music.xml";// xmlĿ�ĵ�ַ,�򿪵�ַ��һ��
	// XML �ڵ�
	public static final String KEY_SONG = "song";
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_THUMB_URL = "thumb_url";
	ListView listview;
	NetMusicAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_music);
		listview = (ListView) findViewById(R.id.net_list);
		LoadData();

	}

	private void LoadData() {
		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // �������ȡxml
		Document doc = parser.getDomElement(xml); // ��ȡ DOM �ڵ�

		NodeList nl = doc.getElementsByTagName(KEY_SONG);
		// ѭ���������еĸ�ڵ� <song>
		for (int i = 0; i < nl.getLength(); i++) {
			// �½�һ�� HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			// ÿ���ӽڵ���ӵ�HashMap�ؼ�= >ֵ
			map.put(KEY_ID, parser.getValue(e, KEY_ID));
			map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
			map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
			map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
			map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

			// ��ӵ������б�
			songsList.add(map);
		}
		adapter = new NetMusicAdapter(this, songsList);
		listview.setAdapter(adapter);
	}

}
