package fqlite.analyzer.protobuf;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import fqlite.analyzer.Converter;
import fqlite.base.Global;
import fqlite.util.Auxiliary;

public class Protoc extends Converter{

	public String decode(String path) {
		
		String result = "";
		String shellscript = "";
		try {
			String cwd = Path.of("").toAbsolutePath().toString();
			System.out.println("Protoc Path::" + cwd);
			String os = System.getProperty("os.name");
			System.out.println("Using System Property: " + os);
			String separator = FileSystems.getDefault().getSeparator();
			
			
			shellscript = cwd + separator + "./proto.run";
			if (Auxiliary.isWindowsSystem())
			{
				shellscript = cwd + separator + "protoc.bat";
				
			}
			else if (Auxiliary.isMacOS()){
				// do nothing take shellscript with absolute path
				//shellscript = shellscript; //"./proto.run";
				if (null != Global.WORKINGDIRECTORY){
					shellscript = Global.WORKINGDIRECTORY + Global.separator + "proto.run";
				}
			}
			
			//System.out.println(" Pfad " + path);
			ProcessBuilder pb = new ProcessBuilder(shellscript,path);
			Process p = pb.start();
			p.waitFor();
			result = new String(p.getInputStream().readAllBytes());
			//System.out.println("Result: " + result);
			
			if(result == null || result.length()==0){
				result = "not a valid buffer, shellscript:" + shellscript + " path: " + path;
			}
			
		}catch(Exception err){
			err.printStackTrace();
			return "not a valid buffer, shellscript:" + shellscript + " path: " + path;
		}
		
		return result;
		
	}
	
}
