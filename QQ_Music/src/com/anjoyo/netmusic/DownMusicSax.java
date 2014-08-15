package com.anjoyo.netmusic;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DownMusicSax extends DefaultHandler {
	public void Sax(String resultVal, MusicSax musicSax) {

		SAXParserFactory saxFactory = SAXParserFactory.newInstance();

		try {

			// 创建XMLReader读取对象，xml文件解析器
			XMLReader reader = saxFactory.newSAXParser().getXMLReader();
			// 注册内容事件处理器（设置xml文件解析器的解析方式）
			reader.setContentHandler(musicSax);

			// 编码格式转化
			String strUTF = new String(resultVal.getBytes("gb2312"));
			// 开始解析xml格式文件
			reader.parse(new InputSource(new StringReader(strUTF)));

		} catch (Exception e) {
			System.out.println("sax 异常");
			e.printStackTrace();
		}

	}
}
