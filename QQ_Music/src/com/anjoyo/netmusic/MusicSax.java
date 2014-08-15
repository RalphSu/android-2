package com.anjoyo.netmusic;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MusicSax extends DefaultHandler {

	static ArrayList<MusicModel> mlistData;
	MusicModel musicmodel;

	public static ArrayList<MusicModel> getListData() {
		return mlistData;
	}

	private final int resource = 1;
	private final int id = 2;
	private final int mp3Name = 3;
	private final int mp3Artist = 4;
	private final int mp3Album = 5;
	private final int mp3Image = 6;
	private final int mp3Size = 7;
	private final int mp3FileName = 8;
	private final int lrcName = 9;
	private final int lrcSize = 10;

	private int musicinfoType;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String musicinfo = new String(ch, start, length);

		switch (musicinfoType) {
		case resource:

			break;
		case id:
			musicmodel.setId(musicinfo);// 歌ID
			break;

		case mp3Name:
			musicmodel.setMp3Name(musicinfo);// 歌名
			break;

		case mp3Artist:
			musicmodel.setMp3Artist(musicinfo);// 作者名
			break;

		case mp3Album:
			musicmodel.setMp3Album(musicinfo);// 专辑名
			break;

		case mp3Image:
			musicmodel.setMp3Image(musicinfo);// 专辑图片
			break;

		case mp3Size:
			musicmodel.setMp3Size(musicinfo);// 歌曲大小

			break;
		case mp3FileName:
			musicmodel.setMp3FileName(musicinfo);// 歌曲文件名

		case lrcName:
			musicmodel.setLrcName(musicinfo);//歌词名称
			break;

		case lrcSize:
			musicmodel.setLrcSize(musicinfo);// 歌词大小
			break;
		default:
			break;
		}
		musicinfoType = 0;
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (localName.endsWith("resource")) {
			mlistData.add(musicmodel);
		}

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		mlistData = new ArrayList<MusicModel>();
	}

	boolean mp3info = false;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);

		if (localName.equals("resource")) {
			musicmodel = new MusicModel();
			mp3info = true;
			return;
		}
		if (mp3info) {
			if (localName.equals("id")) {
				musicinfoType = id;
				return;
			}
			if (localName.equals("mp3Name")) {
				musicinfoType = mp3Name;
				return;
			}
			if (localName.equals("mp3Artist")) {
				musicinfoType = mp3Artist;
				return;
			}
			if (localName.equals("mp3Album")) {
				musicinfoType = mp3Album;
				return;
			}
			if (localName.equals("mp3Image")) {
				musicinfoType = mp3Image;
				return;
			}
			if (localName.equals("mp3Size")) {
				musicinfoType = mp3Size;
				return;
			}
			if (localName.equals("mp3FileName")) {
				musicinfoType = mp3FileName;
				return;
			}
			if (localName.equals("lrcName")) {
				musicinfoType = lrcName;
				return;
			}
			if (localName.equals("lrcSize")) {
				musicinfoType = lrcSize;
				return;
			}
		}
		musicinfoType = 0;
	}
}