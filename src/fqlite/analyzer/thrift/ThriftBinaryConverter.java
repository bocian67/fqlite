package fqlite.analyzer.thrift;

import java.io.FileInputStream;
import java.io.InputStream;

import org.json.JSONObject;

import fqlite.analyzer.*;

public class ThriftBinaryConverter extends Converter {

	@Override
	public String decode(String path) {
	
	
		String rvalue = "invalid stream";
		try {

			InputStream is = new FileInputStream(path);
			byte[] bytes = is.readAllBytes();
			is.close();
						
			ThriftCodec tc = new ThriftCodec();
			// call the binary decoder 
			JSONObject obj = tc.decode(bytes);
			rvalue = obj.toString(4) + "\n";
			System.out.println(rvalue);

		} catch (Exception e) {
			//e.printStackTrace();
		}
		return rvalue;
		
	}

}
