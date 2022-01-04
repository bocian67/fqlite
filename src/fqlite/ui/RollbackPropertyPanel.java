package fqlite.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class RollbackPropertyPanel extends JPanel {

	
	private static final long serialVersionUID = 3116420145072139414L;
	public JLabel ldbpath;
    public JLabel lpagesize;
    public JLabel lencoding;
    public JLabel ltotalsize;
    public JLabel lpagesizeout;
    public JLabel lencodingout;
    public JLabel ltotalsizeout;
    private FileInfo info;

    
    JTabbedPane tabpane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

    
	public RollbackPropertyPanel(FileInfo info)
	{
		this.info = info;
	}
	
	
	public void initHeaderTable(String[][] data)
	{
		setLayout(new BorderLayout());
		
		//JLabel heading = new JLabel(" Database Pragmas ");
		//add(heading,BorderLayout.NORTH);
		
		
		JTextArea headerinfo = new JTextArea();
		PopupFactory.createPopup(headerinfo);
		headerinfo.setColumns(85);
		headerinfo.setAlignmentX(CENTER_ALIGNMENT);
		//add(headerinfo,BorderLayout.NORTH);
		Font font = new Font("Courier", Font.BOLD, 12);
	    headerinfo.setFont(font);
	    headerinfo.setText(info.toString());
		tabpane.addTab("File info",headerinfo);
	
		String column[]={"Offset","Property","Value"};         
		JTable jt=new JTable(data,column);  
		PopupFactory.createPopup(jt);

			
		JTableHeader th = jt.getTableHeader();
		th.setFont(new Font("Serif", Font.BOLD, 15));
		
				
		TableColumnModel tcm = jt.getColumnModel();

		// Columns don't fill the viewport, invoke default layout

		JScrollPane sp = new JScrollPane(jt);    
		//add(sp,BorderLayout.CENTER);
	    tabpane.addTab("Header Fields",sp);

		add(tabpane, BorderLayout.CENTER);
		
		if (tcm.getTotalColumnWidth() < jt.getParent().getWidth())
			jt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		
		
	}
	
	
	
}