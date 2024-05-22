package fqlite.analyzer;

/**
 * This abstract class defines a generic method for decoding a 
 * binary file and returns a human-readable string of the given
 * decoded binary.
 * 
 */
public abstract class Converter {
	
	public abstract String decode(String path);
}
