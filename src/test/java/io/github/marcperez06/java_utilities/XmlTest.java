package io.github.marcperez06.java_utilities;

import java.util.HashMap;

import org.junit.Test;

import io.github.marcperez06.java_utilities.logger.Logger;
import io.github.marcperez06.java_utilities.xml.Xml;

public class XmlTest {
	
	@Test
	public void xmlToJson() {
		Xml xml = new Xml(Xml.FILE_SOURCE, "C:\\Users\\mperezro\\Desktop\\body.xml");
		HashMap<String, Object> map = xml.toMap();
		Logger.println(map.toString());
	}

}
