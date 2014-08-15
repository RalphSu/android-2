package com.anjoyo.netmusic;

import java.io.Serializable;

public class MusicModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String mp3Name;
	private String mp3Artist;
	private String mp3Album;
	private String mp3Image;
	private String mp3FileName;
	private String mp3Size;
	private String lrcName;
	private String lrcSize;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMp3Name() {
		return mp3Name;
	}

	public void setMp3Name(String mp3Name) {
		this.mp3Name = mp3Name;
	}

	public String getMp3Artist() {
		return mp3Artist;
	}

	public void setMp3Artist(String mp3Artist) {
		this.mp3Artist = mp3Artist;
	}

	public String getMp3Album() {
		return mp3Album;
	}

	public void setMp3Album(String mp3Album) {
		this.mp3Album = mp3Album;
	}

	public String getMp3Image() {
		return mp3Image;
	}

	public void setMp3Image(String mp3Image) {
		this.mp3Image = mp3Image;
	}

	public String getMp3FileName() {
		return mp3FileName;
	}

	public void setMp3FileName(String mp3FileName) {
		this.mp3FileName = mp3FileName;
	}

	public String getMp3Size() {
		return mp3Size;
	}

	public void setMp3Size(String mp3Size) {
		this.mp3Size = mp3Size;
	}

	public String getLrcName() {
		return lrcName;
	}

	public void setLrcName(String lrcName) {
		this.lrcName = lrcName;
	}

	public String getLrcSize() {
		return lrcSize;
	}

	public void setLrcSize(String lrcSize) {
		this.lrcSize = lrcSize;
	}

}
