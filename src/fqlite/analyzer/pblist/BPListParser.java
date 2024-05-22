package fqlite.analyzer.pblist;

import java.io.File;
import java.util.List;

import nl.pvanassen.bplist.converter.ConvertToXml;
import nl.pvanassen.bplist.ext.nanoxml.XMLElement;
import nl.pvanassen.bplist.parser.BPListElement;
import nl.pvanassen.bplist.parser.ElementParser;

public class BPListParser {

	private static final ConvertToXml convetToXml = new ConvertToXml();
	private static final ElementParser elementParser = new ElementParser();

	
	public static String parse(String path) {

		List<BPListElement<?>> elements;

		try {

			elements = elementParser.parseObjectTable(new File(path));
			XMLElement xmlElement = convetToXml.convertToXml(elements);
			//System.out.println(xmlElement);		
			return xmlElement.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "<no valid bplist>";
	}

	public static void main(String [] args){
		

	}

}


