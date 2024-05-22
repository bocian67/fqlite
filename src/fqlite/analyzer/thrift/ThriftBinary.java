package fqlite.analyzer.thrift;

import org.json.JSONObject;
import fqlite.analyzer.BinaryLoader;
import fqlite.analyzer.Converter;
import trust.nccgroup.readablethrift.ThriftCodec;

public class ThriftBinary extends Converter {

	@Override
	public String decode(String path) {

		String data = BinaryLoader.parse(path);//
		data = "0b000100000004444649520800020000001800";
		
		System.out.println("Pfad :: "+  path);
		System.out.println(data);
		data = data.toUpperCase();
		//byte[] stream = FleeceDecoder.hexStringToByteArray(data);
		//String b64 = Base64.encodeBase64String(stream);
	    try {
	    	//JSONObject decoded = ThriftCodec.decodeB64String("[B@3d494fbf");
	    	JSONObject decoded = ThriftCodec.decodeB64String(data);
	    	return decoded.toString();
		} catch (Exception e) {
			return "invalid stream";
		}
		
	}

}
