package fqlite.ui;

import java.util.Iterator;
import fqlite.analyzer.BinaryLoader;
import fqlite.analyzer.ConverterFactory;
import fqlite.analyzer.Names;
import fqlite.analyzer.avro.Avro;
import fqlite.analyzer.javaserial.Deserializer;
import fqlite.analyzer.pblist.BPListParser;
import fqlite.base.GUI;
import fqlite.base.Global;
import fqlite.base.Job;
import fqlite.descriptor.TableDescriptor;
import fqlite.util.Auxiliary;
import fqlite.util.Base64EncoderDecoder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * Just like a normal table cell, but each table cell has a tooltip that will display its contents. This makes
 * it easier for the user: they can read the contents without having to expand the table cell.
 * <p>
 * Look it's easy:
 * <code>
 * someColumn.setCellFactory(TooltippedTableCell.forTableColumn());
 * </code>
 */
public class TooltippedTableCell<S, T> extends TableCell<S, T> {
  
	private String tablename = null;
	private Job job = null;
		
	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(String tablename, Job job) {
        return forTableColumn(new DefaultStringConverter(), tablename, job);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter, String tablename, Job job) {
        return list -> new TooltippedTableCell<>(converter,tablename, job);
    }
	
	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
        return list -> new TooltippedTableCell<>(converter);
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem()
                .toString() : converter.toString(cell.getItem());
    }

    public void setTablename(String name){
    	this.tablename = name;
    }
    
    public void setJob(Job job) {
    	this.job = job;
    }
    
  
    @SuppressWarnings("unchecked")
	private void updateItem(final Cell<T> cell, final StringConverter<T> converter) {

    	
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setTooltip(null);
        } 
        else {
                	
        	//System.out.println(" tooltip>>>" + this.getTableColumn().getText());
        	
        	if(this.getTableColumn().getText().equals("")){
        		Tooltip tooltip = new Tooltip("state (D: deleted, F: freelist)");
	            //tooltip.prefWidthProperty().bind(cell.widthProperty());
	            cell.setTooltip(tooltip);	 
	        	String s = getItemText(cell, converter);
	        	cell.setText(s);     
	            return;
        	}
        	
        	if(this.getTableColumn().getText().equals("Offset")){
        		Tooltip tooltip = new Tooltip("byte position");
	            //tooltip.prefWidthProperty().bind(cell.widthProperty());
	            cell.setTooltip(tooltip);	 
	        	String s = getItemText(cell, converter);
	        	cell.setText(s);     
	            return;
        	}
        	
        	if(this.getTableColumn().getText().trim().equals("PLL")){
        		Tooltip tooltip = new Tooltip("payload length");
	            //tooltip.prefWidthProperty().bind(cell.widthProperty());
	            cell.setTooltip(tooltip);	 
	        	String s = getItemText(cell, converter);
	        	cell.setText(s);     
	            return;
        	}
        	
        	if(this.getTableColumn().getText().trim().equals("HL")){
        		Tooltip tooltip = new Tooltip("header length");
	            //tooltip.prefWidthProperty().bind(cell.widthProperty());
	            cell.setTooltip(tooltip);	 
	        	String s = getItemText(cell, converter);
	        	cell.setText(s);     
	            return;
        	}
        	
        	String s = getItemText(cell, converter);
        	cell.setText(s);
        	
        	
        	String tttype = null;
        	/* determine column type for tooltip info panel */
        	
        	Iterator<TableDescriptor> tbls = job.headers.iterator();
        	while(tbls.hasNext()){
        		TableDescriptor td = tbls.next();
        		
        		if(td.tblname.equals(tablename)) {
	        		tttype = td.getToolTypeForColumn(this.getTableColumn().getText());
	            	
        		}	
        	}
        	
        	if (null != tttype)
        		tttype = tttype.toUpperCase();
        	
        	/* if no table description could be found -> lookup index table */
//        	if(tttype == null){
//        		
//        		Iterator<IndexDescriptor> idxs = job.indices.iterator();
//        		while(idxs.hasNext()){
//        			
//        			IndexDescriptor id = idxs.next();
//        			
//        			tttype = id.getToolTypeForColumn(this.getTableColumn().getText());
//        			
//        		}
//        		
//        		
//        	}
        	
        	if(tttype == null){
        		
        		tttype = "";
        	}
        	
        	if(tttype.equals("REAL") || tttype.equals("DOUBLE") || tttype.equals("FLOAT")) {

        		String bb = (String)cell.getItem();
            	int point = bb.indexOf(",");
                String firstpart;
            	if (point > 0)
                	firstpart = bb.substring(0, point);
                else
                	firstpart = bb;
            	//System.out.println("firstpart" + firstpart);
               
            	String value = Auxiliary.int2Timestamp(firstpart);
        		Tooltip tooltip = new Tooltip("[" + tttype + "] " +  bb + "\n" + value );
        		cell.setTooltip(tooltip);
        		return;
        	}
        	if(tttype.equals("INTEGER") || tttype.equals("INT") || tttype.equals("BIGINT") || tttype.equals("LONG") || tttype.equals("TINYINT") || tttype.equals("INTUNSIGNED") || tttype.equals("INTSIGNED") || tttype.equals("MEDIUMINT")) {
        		
        		String bb = (String)cell.getItem();
            	
        		String value = Auxiliary.int2Timestamp(bb);
        		Tooltip tooltip = new Tooltip("[" + tttype + "] " +  bb + "\n" + value );
        		cell.setTooltip(tooltip);
        		return;

        	}
        	else if(s.contains("[BLOB")){
        		
        		int row = -1;
        		// we need the column name
        		try {
        		
        			javafx.scene.control.TableRow<S> tr = this.getTableRow();
        		
        			if (tr == null)
        				return;
        		
        			row = tr.getIndex();
            	
        		}
        		catch(Exception err){
        		   // There is a bug actually under windows -> no idea why	
        		   return;
        		}
        		
      
             	ObservableList<String> hl = (ObservableList<String>)this.getTableView().getItems().get(row);
        	    int from = s.indexOf("BLOB-");
        	    int to = s.indexOf("]");
        	    String number = s.substring(from+5, to);
        	    
        	    //int id = Integer.parseInt(number);
        	    if (hl.get(5)== null || hl.get(5).trim().equals(""))
        	    	return;
        	    Long hash = Long.parseLong(hl.get(5));
        	    String shash = hash + "-" + number;
        	    Image ii = job.Thumbnails.get(shash);
        	    
        	    boolean bson 	= false;
        	    boolean fleece 	= false;
        	    boolean msgpack = false;
        	    boolean thriftb = false;
        	    boolean thriftc = false;
        	    boolean protobuf = false;
        	    
        	    if (job.convertto.containsKey(tablename)){
        	    	
        	    	String con = job.convertto.get(tablename);
        	    	
        	    	switch(con){
        	    	
        	    	case Names.BSON   : 	bson = true;
        	    					    	 break;
        	    	case Names.Fleece : 	 fleece = true;
					  						 break;
        	    	case Names.MessagePack  : msgpack = true;
					  						 break;
        	    	case Names.ProtoBuffer	: protobuf = true;
        	    							 break;
        	    	case Names.ThriftBinary : thriftb = true;
						 					 break;
        	    	case Names.ThriftCompact : thriftc = true;
					 						 break;
        	    	
        	    	}
        	    }
        	    
        	    /* image file -> show picture in tool tip */        	    
        	    if(null != ii)
        	    {
        	    	
        	        Tooltip tooltip = new Tooltip();
      	    		ImageView iv = new ImageView(ii);
      	          	tooltip.setGraphic(iv);   
    	            cell.setTooltip(tooltip);
        	    }
        	    else
        	    {	cell.setTooltip(new Tooltip(""));
        	    	
        	    	if(s.contains("java"))
        	    	{
        	    		long off = Long.parseLong(hl.get(5));
         	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";

         	        	String javaclass = Deserializer.decode(path); 
        	           	
        	           	if(javaclass.length() > 2000){
        	           		javaclass = javaclass.substring(0,2000);
        	           	}
        	           	
        	    		Tooltip tooltip = new Tooltip(javaclass);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
   	    	            
        	    	}    	
        	    	else if(s.contains("plist"))
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".plist";

        	           	String plist = BPListParser.parse(path); 
        	           	
        	           	if(plist.length() > 2000){
        	           		plist = plist.substring(0,2000);
        	           	}
        	           	
        	    		Tooltip tooltip = new Tooltip(plist);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	   
        	    	}
        	    	else if(s.contains("avro")) {
        	    		
        	    		  try {
        	    			  
        	    			  long off = Long.parseLong(hl.get(5));
        	    			  String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	    		      String buffer = Avro.decode(path);
        	    		      Tooltip tooltip = new Tooltip(buffer);
         	    	          tooltip.setWrapText(true);
         	    	          tooltip.prefWidthProperty().bind(cell.widthProperty());
         	    	          cell.setTooltip(tooltip);	  
        	    		   
        	    		  } catch (Exception e) {
        	    		    throw new RuntimeException(e);
        	    		  }
        	    		
        	    		
        	    	}
        	    	
        	    	else if(fleece)
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	            System.out.println("offset :" + off);

        	        	/* inspection is enabled */
        	        	String result = ConverterFactory.build(Names.Fleece).decode(path);
        	        	System.out.println(result);
        	        	Tooltip tooltip = new Tooltip();
        	        	if (null != result)
        	        		tooltip.setText("TEST" + result);
        	        	else
        	        		tooltip.setText("invalid value");
        	        	tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(protobuf)
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	
        	        	/* inspection is enabled */
        	        	String buffer = ConverterFactory.build(Names.ProtoBuffer).decode(path);
        	        	Tooltip tooltip = new Tooltip(buffer);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(thriftb)
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	        	
        	        	/* inspection is enabled */
        	        	String buffer = ConverterFactory.build(Names.ThriftBinary).decode(path);
        	        	Tooltip tooltip = new Tooltip(buffer);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(thriftc)
        	    	{        	    	
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	        	
        	        	/* inspection is enabled */
        	        	String buffer = ConverterFactory.build(Names.ThriftCompact).decode(path);
        	        	Tooltip tooltip = new Tooltip(buffer);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(msgpack)
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	        	
        	        	/* inspection is enabled */
        	        	String buffer = ConverterFactory.build(Names.MessagePack).decode(path);
        	        	Tooltip tooltip = new Tooltip(buffer);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(bson)
        	    	{
        	            long off = Long.parseLong(hl.get(5));
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + ".bin";
        	        	
        	        	/* inspection is enabled */
        	        	String buffer = ConverterFactory.build(Names.BSON).decode(path);
        	        	Tooltip tooltip = new Tooltip(buffer);
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	    	
        	    	else if(s.contains("pdf") || s.contains("heic") || s.contains("tiff")) {
        	    		Tooltip tooltip = new Tooltip("double-click to preview.");
   	    	            tooltip.setWrapText(true);
   	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
   	    	            cell.setTooltip(tooltip);
        	    	}
        	     	else
        	    	{
        	            long off = Long.parseLong(hl.get(5));

        	    		//String text = Auxiliary.hex2ASCII(getItemText(cell, converter));   
        	            
        	            String fext = ".bin";
        	            if(s.contains("<tiff>"))
        	            		fext = ".tiff";
        	            else if(s.contains("<pdf>"))
        	            		fext = ".pdf";
        	            else if(s.contains("<heic>"))
    	            		fext = ".heic";
        	            else if(s.contains("<gzip>"))
    	            		fext = ".gzip";
        	            else if(s.contains("<avro>"))
        	            	fext = ".avro";
    	              	           
        	        	String path = GUI.baseDir + Global.separator + job.filename + "_" + off + "-" + number + fext;
        	        	String text = BinaryLoader.parseASCII(path);
        	    	    // only show the first 2000 characters - it's just a tooltip ;-)
        	    		if(text.length() > 2000)
        	    			text = text.substring(0,2000);
        	    		
        	    		//Add text as "tooltip" so that user can read text without editing it.
	    	            Tooltip tooltip = new Tooltip(text);
	    	            tooltip.setWrapText(true);
	    	            tooltip.prefWidthProperty().bind(cell.widthProperty());
	    	            if(null!=tttype)
	    	            	cell.setTooltip(tooltip);
	    	      
	    	            s = GUI.class.getResource("/hex-32.png").toExternalForm();
	    	    		ImageView iv = new ImageView(s);
	    	    		tooltip.setGraphic(iv);   
        	    	}
        	    
        	    }
        	    	
        	 
        	}
        	else{
        		Tooltip tooltip = null;
        		

        		//String bb = (String)cell.getItem();
        		//System.out.println(">>>" + bb + " " + job.timestamps.size());
        		//if(job.timestamps.containsKey(bb)){
        		//	Object value = job.timestamps.get(bb);        			
        		//	tooltip = new Tooltip("[" + tttype + "] " +  value);
        		//   return;
        		//}
        		
        		if (tttype.equals("TEXT") || tttype.contains("VARCHAR"))
        			if(job.inspectBASE64.contains(tablename)) {
	        			//boolean isBase64 = Base64.isBase64(s);
	        			
	        			boolean isBase64 = Base64EncoderDecoder.isBase64Encoded(s);
	        			
	        			if(isBase64 && s.length() > 2 ){
		                    try {
		                    	System.out.println("inside BASE64 check");
		                    	
		                    	String decodedString = Base64EncoderDecoder.decodeFromBase64(s);
		                    	
		                    	//byte[] decodedBytes = java.util.Base64.getDecoder().decode(s);
		                    	
		                    	//String decodedString = new String(decodedBytes);
		                   
			                	if (decodedString != null && decodedString.length() > 0)
			            			tooltip = new Tooltip(decodedString);
			            		else
			            			tooltip = new Tooltip("[" + tttype + "] " + s);
			                	tooltip.setWrapText(true);
			                	tooltip.prefWidthProperty().bind(cell.widthProperty());
			    	            cell.setTooltip(tooltip);	            
			                	
			    	            return;
		                    }catch(Exception err){
		                    	//System.out.println(err);
		                    }
		    			}
	    		}
                	
        		
        		//Add text as tooltip so that user can read text without editing it.
        		if (tttype == null || tttype == "")
        			tooltip = new Tooltip(getItemText(cell, converter));
        		else
        			tooltip = new Tooltip("[" + tttype + "] " + getItemText(cell, converter));
	            tooltip.setWrapText(true);
	            tooltip.prefWidthProperty().bind(cell.widthProperty());
	            cell.setTooltip(tooltip);	            
        	}
        }
    }

    private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");

    /**
     * The easiest way to get this working is to call this class's static forTableColumn() method:
     * <code>
     * someColumn.setCellFactory(TooltippedTableCell.forTableColumn());
     * </code>
     */
    public TooltippedTableCell() {
        this(null);
    }
    
  
    public TooltippedTableCell(StringConverter<T> converter) {
        this.getStyleClass().add("tooltipped-table-cell");
        setConverter(converter);
    }

    public TooltippedTableCell(StringConverter<T> converter,String tablename, Job job) {
        this.getStyleClass().add("tooltipped-table-cell");
        setConverter(converter);
        setTablename(tablename);
        setJob(job);
    }

    
    
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }


    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter());
    }
    
    
    
}