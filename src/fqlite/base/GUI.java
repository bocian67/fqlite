package fqlite.base;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.Taskbar;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import fqlite.analyzer.BinaryLoader;
import fqlite.analyzer.Names;
import fqlite.analyzer.javaserial.Deserializer;
import fqlite.analyzer.pblist.BPListParser;
import fqlite.descriptor.TableDescriptor;
import fqlite.log.AppLog;
import fqlite.types.CtxTypes;
import fqlite.types.FileTypes;
import fqlite.ui.AboutDialog;
import fqlite.ui.DBPropertyPanel;
import fqlite.ui.FQTableView;
import fqlite.ui.FileInfo;
import fqlite.ui.FontDialog;
import fqlite.ui.Importer;
import fqlite.ui.NodeObject;
import fqlite.ui.RollbackPropertyPanel;
import fqlite.ui.TooltippedTableCell;
import fqlite.ui.UserGuideWindow;
import fqlite.ui.WALPropertyPanel;
import fqlite.util.Auxiliary;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/*
    ---------------
    GUI.java
    ---------------
    (C) Copyright 2023.

    Original Author:  Dirk Pawlaszczyk
    Contributor(s):   -;

   
    Project Info:  http://www.hs-mittweida.de

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 
*/

/**
 * This class offers a basic graphical user interface. It is based on JavaFX 
 * and extends the class <code>Application</code>.
 * 
 * Most of the decoration code was generated manually.
 * 
 * If you want to start FQLite in aphic-mode you have to call this class. 
 * 
 * It contains a main()-function.
 * 
 *     __________    __    _ __     
 *    / ____/ __ \  / /   (_) /____ 
 *   / /_  / / / / / /   / / __/ _ \
 *  / __/ / /_/ / / /___/ / /_/  __/
 * /_/    \___\_\/_____/_/\__/\___/ 
 * 
 * 
 * 
 * @author Dirk Pawlaszczyk
 *
 *
 */

public class GUI extends Application {

	
	public static File baseDir;
	
	public static int pos = 0;
	
	public static final CountDownLatch latch = new CountDownLatch(1);
   	public static GUI mainwindow;
	public ConcurrentHashMap<String, javafx.scene.Node> tables = new ConcurrentHashMap<String, javafx.scene.Node>();
	//ConcurrentHashMap<String, JTextPane> hexviews = new ConcurrentHashMap<String, JTextPane>();
	private Hashtable<Object, String> rowcolors = new Hashtable<Object, String>();

	ContextMenu cm = null;
	TextArea logwindow;
//	static Statusbar statusbar = new Statusbar();
	MenuBar menuBar;
	
	//ScrollPane scrollpane_tables;
//	ScrollPane hexScrollPane;
//	static ScrollPane LogwindowScrollPane;
	public static ScrollPane CellDetailsScrollPane;
	VBox table_panel_with_filter;
	SplitPane splitPane;
	final StackPane leftSide = new StackPane();
    final VBox rightSide = new VBox();

    //static HexViewerApp HEXVIEWER_WINDOW = null; 
    static HexViewManager HEXVIEW = HexViewManager.getInstance();
    
	/* search filter */
	TextField currentFilter = null;
	HBox head;
    int datacounter = 0;
	
	static GUI app;
	static TreeView<NodeObject> tree;
	//TreeItem<NodeObject>  dbNode;
	TreeItem<NodeObject>  walNode;
	TreeItem<NodeObject>  rjNode;

	public static Font ttfFont = null;

	TreeItem<NodeObject>  root = new TreeItem<NodeObject>(new NodeObject("Databases",true));
    
	ConcurrentHashMap<String, TreeItem<NodeObject>> treeitems  = new ConcurrentHashMap<String, TreeItem<NodeObject>>();
	
	String lasthit = "";
	int lasthitrow = 0;
	int lasthitcol = 0;

	File lastDir = null;

	ImageIcon facewink;
	ImageIcon findIcon;
	ImageIcon errorIcon;
	ImageIcon infoIcon;
	ImageIcon questionIcon;
	ImageIcon warningIcon;

	StackPane rootPane = new StackPane();

	Stage stage;
	Scene scene;
	public static VBox  topContainer;
	
	Font appFont = null;
	
	/**
	 * Launch the graphic front-end with this method.
	 */
    public static void main(String[] args) {
   	
		/**
		  * This is needed because only one main class can be called in an
		  * executable jar archive. 
		  * 
		  **/
		if (args.length > 0)
		{			     
			// There is a least one parameter -> check, if nogui-option is set
            String option = args[0];
			
            // take the first argument - if there is one - an put to global variables
            Global.WORKINGDIRECTORY = option;
            
            //if (args[0].contains(option))
			//try {
				//switch to CLI mode instead
				//System.out.println("[nogui] option is set => starting in CLI-mode...\n");
				//MAIN.main(args);
			
			
			//} catch (Exception e) {
			//	System.out.println("ERROR while running MANI.main(). Leave program now.");
			//}
		    // do not call the HexViewFactory and leave right now.
		    //return;
		}
		
		
		ImageIcon img = new ImageIcon(GUI.class.getResource("/logo.png"));
		
		
		 	SystemTray st = SystemTray.getSystemTray();
			
			//this is new since JDK 9
			if (Taskbar.isTaskbarSupported())
		    {
				try {
					final Taskbar taskbar = Taskbar.getTaskbar();
					taskbar.setIconImage(img.getImage());
					taskbar.setIconBadge("FQLite");
				}catch(Exception err) {
					// do nothing 
				}
				
		    }
			else if(SystemTray.isSupported()){
				try {
					TrayIcon ti = new java.awt.TrayIcon(img.getImage());						
					st.add(ti);
				} catch (AWTException e) {
					// do nothing - no logo - no problem ;-)
				}
			}
		//}
		
		Application.launch(args);
		
    }

	/**
	 * Set the TTF-font for the user interface. 
	 * @param f the font we want to set. 
	 */
	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}
	
	
	

	/**
	 * Constructor of the user interface class. 
	 * @throws IOException
	 */
	@Override
	public void start(Stage stage) throws Exception {
	
			//Platform.setImplicitExit(true);
			stage.setOnCloseRequest(e->{
					System.out.println(" OnCloseRequest() was called");
					Platform.exit(); System.exit(0);}
			);

		
			//javafx.application.Platform.setImplicitExit(false);
		    HexViewManager.setParent(stage);
		
			baseDir = new File(System.getProperty("user.home"), ".fqlite");

			  //Attach the icon to the stage/window
		    stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/logo.png")));
		 	  
			
			/* create hidden directory inside users home */
			Path pp = Path.of(baseDir.getAbsolutePath());
            System.out.println("FQlite home::" + baseDir.getAbsolutePath());
			if (!Files.exists(pp)) {
				// path does not exist in the moment -> create a new hidden folder
				baseDir.mkdir();
			}
			
			clearCacheFromPreviousRun();
		
			this.stage = stage;			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
   	    
   	    	 // let us first have a look on the currently installed Fonts
   	    	 // if Segoe HexViewFactory Emoji (the standard font for emojis on win10)
   	    	 // go for it
   	    	
   	    	 //String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
             boolean msfontinstalled = false;
     							
		mainwindow = this;
		
		stage.setTitle("FQLite Carving Tool");
	
		rootPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		
		/* set Icon for treeview root node */
		String s = GUI.class.getResource("/leaf.jpg").toExternalForm();
		ImageView iv = new ImageView(s);
		root.setGraphic(iv);
        root.setExpanded(true);
			
		URL url = GUI.class.getResource("/find.png");
		findIcon = new ImageIcon(url);
		
		//javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

	
		MenuItem mntopen = new MenuItem("Open Database...");
		mntopen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		//mntopen.getAccessibleContext().setAccessibleDescription("Open a sqlite database file to analyse...");
		mntopen.setOnAction(e -> open_db(null));

		MenuItem mntmExport = new MenuItem("Export Node...");
		mntmExport.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		//mntmExport.getAccessibleContext().setAccessibleDescription("Start export a database to csv...");
		mntmExport.setOnAction(e -> doExport());

		MenuItem mntclose = new MenuItem("Close All");
		mntclose.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
		//mntclose.getAccessibleContext().setAccessibleDescription("Close All currently open databases");
		mntclose.setOnAction(e -> closeAll());

		
		MenuItem mntmExit = new MenuItem("Exit");
		mntmExit.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
		mntmExit.setOnAction(e -> {Platform.exit(); System.exit(0);});



		MenuItem mntAbout = new MenuItem("About...");
		mntAbout.setOnAction(e -> new AboutDialog(topContainer));
		
		
		MenuItem mntFont= new MenuItem("Fonts...");
		mntFont.setOnAction(e -> {
				FontDialog fdia = new FontDialog(javafx.scene.text.Font.getDefault(),topContainer);
				fdia.show();
			}
		);

		MenuItem mntmLog = new MenuItem("View Log...");
		mntmLog.setOnAction( e -> {
			showLog();
		});
		
		
		MenuItem mntmHelp = new MenuItem("Help");
		mntmHelp.setOnAction(e -> {
				showHelp();
			}
		);
	
		SeparatorMenuItem sep = new SeparatorMenuItem();
		SeparatorMenuItem sep2 = new SeparatorMenuItem();

		
		Menu mnFiles = new Menu("File");
		Menu mnInfo = new Menu("Info");

		mnFiles.getItems().addAll(mntopen,mntmExport,sep,mntclose,sep2,mntmExit);
		mnInfo.getItems().addAll(mntmHelp,mntmLog,mntFont,mntAbout);
		
		
		/* MenuBar */
		menuBar = new MenuBar();
		menuBar.getMenus().addAll(mnFiles,mnInfo);
	    	
		splitPane = new SplitPane();

		 s = GUI.class.getResource("/root.png").toExternalForm();
		 Button starthere = new Button();
		 starthere.setMaxSize(200, 200);

		 iv = new ImageView(s);
		 starthere.setGraphic(iv);
		 starthere.setOnAction(e->open_db(null));
		 rootPane.setAlignment(Pos.CENTER);
		 rootPane.getChildren().add(starthere);
  		 rootPane.setPrefHeight(4000);
		
		prepare_tree();
        leftSide.getChildren().add(tree);
                
		splitPane.getItems().add(leftSide);
	    splitPane.getItems().add(rightSide);
	    SplitPane.setResizableWithParent(leftSide, true);
	    SplitPane.setResizableWithParent(rightSide, true);

		ToolBar toolBar = new ToolBar();
		
	    s = GUI.class.getResource("/openDB_gray.png").toExternalForm();
		Button btnOeffne = new Button();
		iv = new ImageView(s);
		btnOeffne.setGraphic(iv);
		btnOeffne.setOnAction(e->open_db(null));
		btnOeffne.setTooltip(new Tooltip("open database file"));
		toolBar.getItems().add(btnOeffne);

		s = GUI.class.getResource("/export_gray.png").toExternalForm();
		Button btnExport = new Button();
		iv = new ImageView(s);
		btnExport.setGraphic(iv);
		btnExport.setOnAction(e->doExport());
		btnExport.setTooltip(new Tooltip("export data base to file"));
		toolBar.getItems().add(btnExport);

		s = GUI.class.getResource("/closeDB_gray.png").toExternalForm();
		Button btnClose = new Button();
		iv = new ImageView(s);
		btnClose.setGraphic(iv);
		btnClose.setTooltip(new Tooltip("close All"));
		toolBar.getItems().add(btnClose);
		btnClose.setOnAction(e->closeAll());

		s = GUI.class.getResource("/helpcontent_gray.png").toExternalForm();
		Button about = new Button();
		iv = new ImageView(s);
		about.setGraphic(iv);
		about.setOnAction(e->
		{
			showHelp();
		}
		
				
		);
		about.setTooltip(new Tooltip("help"));
		toolBar.getItems().add(about);

		s = GUI.class.getResource("/exit3_gray.png").toExternalForm();
		Button btnexit = new Button();
		iv = new ImageView(s);
		btnexit.setGraphic(iv);
		btnexit.setTooltip(new Tooltip("exit"));
		btnexit.setOnAction(e->{Platform.exit(); System.exit(0);});
		toolBar.getItems().add(btnexit);

		s = GUI.class.getResource("/hex-32.png").toExternalForm();
		Button hexViewBtn = new Button();
		iv = new ImageView(s);
		hexViewBtn.setGraphic(iv);
		hexViewBtn.setTooltip(new Tooltip("open database in HexView"));
		hexViewBtn.setOnAction(e->{

			
			openHexViewer();	
				
				
		});
		toolBar.getItems().add(hexViewBtn);	
		
		url = GUI.class.getResource("/facewink.png");
		facewink = new ImageIcon(url);

		url = GUI.class.getResource("/error-48.png");
		errorIcon = new ImageIcon(url);

		url = GUI.class.getResource("/information-48.png");
		infoIcon = new ImageIcon(url);

		url = GUI.class.getResource("/question-48.png");
		questionIcon = new ImageIcon(url);

		url = GUI.class.getResource("/warning-48.png");
		warningIcon = new ImageIcon(url);

		
		/**
		    Bring together all components:
		 */
		topContainer = new VBox();
		topContainer.getChildren().add(menuBar);
		topContainer.getChildren().add(toolBar);
		topContainer.getChildren().add(splitPane);
		scene = new Scene(topContainer,Screen.getPrimary().getVisualBounds().getWidth()*0.9,Screen.getPrimary().getVisualBounds().getHeight()*0.9);	
	    VBox.setVgrow(splitPane, Priority.ALWAYS);
        
		
		stage.showingProperty().addListener(new ChangeListener<Boolean>() {

		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if (newValue) {
		            splitPane.setDividerPositions(0.25f);
		            observable.removeListener(this);
		    	    //rightSide.autosize();

		        }
		    }
		});
		
		
		tree.setOnContextMenuRequested(new
				EventHandler<ContextMenuEvent>() {
				 @Override
				 public void handle(ContextMenuEvent event) {
				 
					 hideContextMenu();
					 cm = createContextMenu(CtxTypes.TABLE);
					 tree.setContextMenu(cm);
					 cm.show(tree,event.getScreenX(), event.getScreenY());
					 cm.show(tree.getScene().getWindow(), event.getScreenX(), event.getScreenY());

				 }
		});
		
		// OnDrag a file Over
		scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        System.out.println(filePath);
                        open_db(file);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
        
        	 // if the Segoe Emoji font is not installed -> use an opensource TTF
	    	 // that is part of the Archive file
			if (!msfontinstalled)
			{	
				
			    try
			    {
				 	// System.out.println(" Didn't find the Microsoft font. Use OpenSansEmoji instead.");
		    	     InputStream is = this.getClass().getResourceAsStream("/OpenSansEmoji.ttf");
		    	     appFont=Font.createFont(Font.TRUETYPE_FONT,is);
		    	     ge.registerFont(appFont);
		    	     rootPane.setStyle("-fx-font: 13 \""+ appFont.getFontName() + "\"; ");
		    	     topContainer.setStyle("-fx-font: 13 \""+ appFont.getFontName() + "\"; ");
			   
		             javafx.scene.text.Font f = javafx.scene.text.Font.font(
	                            "Segoe UI Emoji",
	                            null,
	                            FontPosture.REGULAR,
	                            14.0);  
		             topContainer.setStyle("-fx-font: 13 \""+ f.getName() + "\"; ");
			    } 
			    catch (FontFormatException | IOException e) 
			    {
			    	e.printStackTrace();
			    }
		}


        tree.autosize(); 

        
     
        
		stage.setScene(scene);
        stage.centerOnScreen();
        stage.sizeToScene();
        stage.show();
        stage.toFront();
        stage.requestFocus();
     
       // HEXVIEWER_WINDOW = new HexViewerApp();
       // Stage hexstage = new Stage();
       // HEXVIEWER_WINDOW.start(hexstage);
       
	}
	
	private void openHexViewer(){
		TreeItem<NodeObject> node = (TreeItem<NodeObject>) tree.getSelectionModel().getSelectedItem();
		if (node == null || node == root) {
			
		} else if (null != node.getValue()) {
			NodeObject no = (NodeObject) node.getValue();
		
			// prepare Hex-View of DB-File
			if (no.type == FileTypes.SQLiteDB)
			{			
				if (null != no.job) {
					
					HEXVIEW.load(no.job.path);
				}
			}
			// prepare Hex-View of WAL-File
			else if (no.type == FileTypes.WriteAheadLog)
			{
				
				if (null != no.job.wal) {
					
					HEXVIEW.load(no.job.wal.path);

				}
			}	
			// prepare Hex-View of Rollback-Journal
			else if (no.type == FileTypes.RollbackJournalLog)
			{
				
				if (null != no.job.rol) {
					
					HEXVIEW.load(no.job.rol.path);
				}
				

			}

		}	
		
		
		Platform.runLater(new Runnable() {
			       public void run() {             
			           try {
			        	   HEXVIEW.show();
			        	   
			           } catch (Exception e) {
						e.printStackTrace();
					}
			       }
		});			    
		
		
			
	}
	
	
	private void showHelp()
	{
		Platform.runLater(new Runnable() {
			 
			   public void run() {     
				   
			    	new UserGuideWindow().start(new Stage());									   	
			   }  
		});
		
	}
	
	private void hideContextMenu()
	{
		 if (null != cm)
			   cm.hide();
	}
	
	private void showLog(){
		AppLog logwindow = new AppLog();
		
		Stage stage = new Stage();
	    try {
			logwindow.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  
	}
	
	private ContextMenu createContextMenu(CtxTypes type){
		
		final ContextMenu contextMenu = new ContextMenu();
	
		MenuItem mntclosesingle = new MenuItem("Close Database");
		String s = GUI.class.getResource("/closeDB_gray.png").toExternalForm();
		ImageView iv = new ImageView(s);
		mntclosesingle.setGraphic(iv);
		mntclosesingle.setOnAction(e->{
			TreeItem<NodeObject> node = tree.getSelectionModel().getSelectedItem();
			if (node == null || node.getValue().isRoot) {
				return;
			} 
			else if (null != node.getValue()) {
				NodeObject no = (NodeObject) node.getValue();
	            boolean remove = node.getParent().getChildren().remove(node);
	            if (remove) {
	            	AppLog.info(" Database " + no.name + " closed.");
	            	this.tables.remove(no); 
	        	    this.treeitems.remove(no);
	            }
	            if (null == no.job) {}
			}	
		});
    
       
		MenuItem mntopen = new MenuItem("Open Database...");
		s = GUI.class.getResource("/openDB_gray.png").toExternalForm();
		iv = new ImageView(s);
		mntopen.setGraphic(iv);
		mntopen.setOnAction(e->open_db(null));

		MenuItem mntmExport = new MenuItem("Export Node...");
		mntmExport.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		//mntmExport.getAccessibleContext().setAccessibleDescription("Start export a database to csv...");
		mntmExport.setOnAction(e ->{ doExport();});
		
		//MenuItem mntclose = new MenuItem("Close All");
		//mntclose.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
		//s = GUI.class.getResource("/closeDB_gray.png").toExternalForm();
		//iv = new ImageView(s);
		//mntclose.setGraphic(iv);

		MenuItem mnthex = new MenuItem("Open HexViewer");
		mnthex.setAccelerator(KeyCombination.keyCombination("Ctrl+H"));
		s = GUI.class.getResource("/hex-32.png").toExternalForm();
		iv = new ImageView(s);
		mnthex.setOnAction(e ->{openHexViewer();});
		mnthex.setGraphic(iv);
			
	
	//mntclose.getAccessibleContext().setAccessibleDescription("Close All currently open databases");
	//	mntclose.setOnAction(e -> closeAll());

		
	//	MenuItem mntmExit = new MenuItem("Exit");
	//	mntmExit.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
	//	mntmExit.setOnAction(e -> {Platform.exit(); System.exit(0);});

		SeparatorMenuItem sepA = new SeparatorMenuItem();
		SeparatorMenuItem sepB = new SeparatorMenuItem();
		//SeparatorMenuItem sepC = new SeparatorMenuItem();

		
	    contextMenu.getItems().addAll(sepA,mntopen,mntmExport,mntclosesingle,sepB,mnthex);
		
	    
	    return contextMenu;
	}


	
	/**
	 *  Delete all Files from a previous run of the program. 
	 */
	private void clearCacheFromPreviousRun(){
		
		if(baseDir != null){
			
			try {
				File [] cache = baseDir.listFiles();
				
				if(null == cache)
					return;
				
				for(File file: baseDir.listFiles()) 
				    if (!file.isDirectory()) 
				        file.delete();
			}
			catch(Exception err){
				// do nothing - no cache directory	
			}
		}
	}
	
	
	/**
	 *  Close all database nodes that are currently open. 
	 */
	public void closeAll() {

		TreeItem<NodeObject> root = tree.getRoot();
		Iterator<TreeItem<NodeObject>> it = root.getChildren().iterator();
	
	
		while (it.hasNext()) {
			
			TreeItem<NodeObject> node = it.next();
			if (null != node.getValue()) {
				NodeObject no = (NodeObject) node.getValue();
				if (null != no.job) {
					no.job = null;
					
				}
			}
		}
		
		// remove all nodes -> simply create a new TreeView 
		if (root != null) {
			 root.getChildren().clear();
		}

		this.tables.clear(); 
	    this.treeitems.clear(); 
	    HEXVIEW.close();
        System.gc();	

	}

	/**
	 * Start a data export.
	 */
	public void doExport() {
		NodeObject no = null;
		
		/* Do we really have a database node currently selected ? */
		TreeItem<NodeObject> node = tree.getSelectionModel().getSelectedItem();
		if (node == null || node.getValue().isRoot) {
			return;
		} 
		else if (null != node.getValue()) {
			no = (NodeObject) node.getValue();
			if (null == no.job) {}
		}

		/* it is indeed a valid database node -> begin with export */
		export_table(no);
	}


	 
	/**
	 * Add a new table header to the database tree.
	 * 
	 * @param job
	 * @param tablename
	 * @param columns
	 * @return tree path
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	String add_table(Job job, String tablename, List<String> columns, List<String> columntypes, List<String> PK, List<String> BoolColumns, boolean walnode,
			boolean rjnode, int db_object) {

		NodeObject o = null;
		
		Path p = Paths.get(job.path);
		
		FQTableView<Object> table = new FQTableView<Object>(tablename,p.getFileName().toString(),job, columns);
		
 		
		table.getSelectionModel().setSelectionMode(
			    SelectionMode.MULTIPLE
		);
		
		
		Image img = new Image(GUI.class.getResource("/icon_status.png").toExternalForm());
	    ImageView view = new ImageView(img);
	  
	
		// normal table ?
		if (!walnode) {
	    //yes		
		//add the standard columns (index 0 <>'line number', 2 <> 'status',3 <> 'offset' - '1' <>is the table name 
	    //Label NoLabel = new Label("No.");
	    //NoLabel.setTooltip(new Tooltip("row number")); 
		TableColumn numbercolumn = new TableColumn<>("No.");
		//numbercolumn.setGraphic(NoLabel);
		numbercolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
			public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
					return new SimpleStringProperty(param.getValue().get(0).toString());               //line number index   
			}                    
		});  
		
		numbercolumn.setStyle( "-fx-alignment: TOP-RIGHT;");


		//Label PLLLabel = new Label("PLL");
		//PLLLabel.setTooltip(new Tooltip("shows payload length in bytes")); 
		TableColumn pllcolumn = new TableColumn<>("PLL");
		//pllcolumn.setGraphic(PLLLabel);
		pllcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
     	pllcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                return new SimpleStringProperty(param.getValue().get(2).toString());                        
            }                    
		});
     	
		pllcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

     	
    	//Label HLLabel = new Label("HL");
		//HLLabel.setTooltip(new Tooltip("shows the header length in bytes")); 
		TableColumn hlcolumn = new TableColumn<>("HL");
		//hlcolumn.setGraphic(HLLabel);
		hlcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
     	hlcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                return new SimpleStringProperty(param.getValue().get(3).toString());                        
            }                    
		});
     	
		hlcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

     	 
     	
     	Label statusLabel = new Label(Global.STATUS_CLOMUN);
		
     	statusLabel.setTooltip(new Tooltip("indicates if data record is deleted or not")); 
     	TableColumn statuscolumn = new TableColumn<>();
        statuscolumn.setGraphic(statusLabel);
		statuscolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
     	statuscolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
			public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                return new SimpleStringProperty(param.getValue().get(4).toString());                        
            } 
     	});
     	statuscolumn.setGraphic(view);
		statuscolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

	  	
     	TableColumn offsetcolumn = new TableColumn<>("Offset");
		offsetcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
     	offsetcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
			public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                return new SimpleStringProperty(param.getValue().get(5).toString());                        
            }                    
		});
     		
		offsetcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

     		
     	//[no,pll,hl,tabname,status,...]
		table.getColumns().addAll(numbercolumn,statuscolumn,offsetcolumn,pllcolumn,hlcolumn);
		
		}
		else {
			/**
			 * Attention: Table is a WAL-Table and has some extra columns !!!
			 */
		
			//add the standard columns (index 0 <>'line number', 2 <> 'status',3 <> 'offset' - '1' <>is the table name 
		    //Label NoLabel = new Label("No.");
		    //NoLabel.setTooltip(new Tooltip("row number")); 
			TableColumn numbercolumn = new TableColumn<>("No.");
			//numbercolumn.setGraphic(NoLabel);
			numbercolumn.setComparator(new CustomComparator());
			numbercolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
				public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
						return new SimpleStringProperty(param.getValue().get(0).toString());               //line number index   
				}                    
			});  
			numbercolumn.setStyle( "-fx-alignment: TOP-RIGHT;");


			//Label PLLLabel = new Label("PLL");
			//PLLLabel.setTooltip(new Tooltip("shows payload length in bytes")); 
			TableColumn pllcolumn = new TableColumn<>("PLL");
			pllcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
			//pllcolumn.setGraphic(PLLLabel);
			pllcolumn.setComparator(new CustomComparator());
	     	pllcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
	            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
	                return new SimpleStringProperty(param.getValue().get(2).toString());                        
	            }                    
			});
			pllcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

	     	
	    	//Label HLLabel = new Label("HL");
			//HLLabel.setTooltip(new Tooltip("shows the header length in bytes")); 
			TableColumn hlcolumn = new TableColumn<>("HL");
			hlcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
			//hlcolumn.setGraphic(HLLabel);
			hlcolumn.setComparator(new CustomComparator());
	     	hlcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
	            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
	                return new SimpleStringProperty(param.getValue().get(3).toString());                        
	            }                    
			});
			hlcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

	     	
	     	Label statusLabel = new Label(Global.STATUS_CLOMUN);
			statusLabel.setTooltip(new Tooltip("indicates if data record is deleted or not")); 
	     	TableColumn statuscolumn = new TableColumn<>();
	        statuscolumn.setGraphic(statusLabel);
			statuscolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
	        statuscolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
	            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
	                return new SimpleStringProperty(param.getValue().get(4).toString());                        
	            } 
	     	});
	     	statuscolumn.setGraphic(view);
			statuscolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

		  	
	     	TableColumn offsetcolumn = new TableColumn<>("Offset");
			offsetcolumn.setComparator(new CustomComparator());
			offsetcolumn.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
	     	offsetcolumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
	            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
	                return new SimpleStringProperty(param.getValue().get(5).toString());                        
	            }                    
			});
			offsetcolumn.setStyle( "-fx-alignment: TOP-RIGHT;");

	     		
	     	//[no,pll,hl,tabname,status,...]
			table.getColumns().addAll(numbercolumn,statuscolumn,offsetcolumn,pllcolumn,hlcolumn);
		
		
		
		
		}	// end of else (walnode = true)	
		
		datacounter = 0;
		
	   /**********************************
        * ADD TABLE COLUMNS DYNAMICALLY *
        **********************************/
		
		
		for (int i = 0; i < columns.size(); i++) {
            String colname = columns.get(i);
            final int j = walnode ? i + 6 : i + 6;                
			TableColumn col = new TableColumn(colname);		
			col.setCellFactory(TooltippedTableCell.forTableColumn(tablename,job));
			col.setComparator(new CustomComparator());
			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
	  
			public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
            	if (param.getValue().size() <= j)
            		return new SimpleStringProperty("");
            	return new SimpleStringProperty(param.getValue().get(j).toString());                        

            }      
			});
			
			
			if (columntypes.size()>i && !columntypes.get(i).equals("BLOB") && !columntypes.get(i).equals("TEXT")) {				
				col.setStyle( "-fx-alignment: TOP-RIGHT;");
			}
			else {
			    if (columntypes.size()>i)
				col.setStyle( "-fx-alignment: TOP-LEFT;");
			}
			
			/* add icon to PRIMARYKEY columns */
			if (null != PK)
			{
				if(PK.contains(colname))
				{
					img = new Image(GUI.class.getResource("/key-icon.png").toExternalForm());
				    view = new ImageView(img);
				    col.setGraphic(view);
				}	
			}
			table.getColumns().add(col);
		}
						
			
	    VBox tablePane = new VBox();
	    
	    
		Image img2 = new Image(GUI.class.getResource("/closeDB_gray.png").toExternalForm());
	  
		ImageView view2 = new ImageView(img2);
	
	    //Label filterlabel = new Label();
	    //filterlabel.setGraphic(view2);
	    
	    Button clearFilter = new Button();
	    clearFilter.setGraphic(view2);
		view2.setFitHeight(14);
	    view2.setFitWidth(14);
	    view2.preserveRatioProperty();
	    clearFilter.setGraphic(view2);
	    clearFilter.setTooltip(new Tooltip("set back filter"));
		   
	    ComboBox<String> columnselection = new ComboBox<String>();

	    columnselection.getItems().add("All Columns (Filter) -> ");
	    columnselection.getItems().add("No.");
	    columnselection.getItems().add("Status");
	    columnselection.getItems().add("Offset");
	    columnselection.getItems().add("PLL");
	    columnselection.getItems().add("HL");
		    
	    
	    Iterator<String> cli = columns.iterator();
	    
	    while(cli.hasNext()) {
	        String choice = cli.next();
	    	if(choice != null)
	        	columnselection.getItems().add(choice);
	    }
	    
	    // Select All Columns as default 
	    columnselection.getSelectionModel().select(0);
	    
	    TextField filter = new TextField();
	    filter.setPrefWidth(300);
	    
	    HBox filterpane = new HBox();
	    filterpane.getChildren().add(0,columnselection);
	    filterpane.getChildren().add(1,filter);
	    filterpane.getChildren().add(2,clearFilter);
		 
	    
		tablePane.getChildren().add(filterpane);
		tablePane.getChildren().add(table);
		table.setPrefHeight(4000);
		VBox.setVgrow(table, Priority.ALWAYS);
	    Label l = new Label("Table: " + tablename);
	    tablePane.getChildren().add(l);
	
		if (walnode)
      		o = new NodeObject(tablename, tablePane, columns.size(), FileTypes.WriteAheadLog, db_object); // wal file
		if (rjnode)
			o = new NodeObject(tablename, tablePane, columns.size(), FileTypes.RollbackJournalLog, db_object); // rollback																									// journal file
		if (!walnode && !rjnode)
			o = new NodeObject(tablename, tablePane, columns.size(), FileTypes.SQLiteDB, db_object); // normal db

		o.job = job;
		TreeItem<NodeObject> dmtn = new TreeItem<NodeObject>(o);    
	    dmtn.setExpanded(true);
	    
		
		String s = null;
		
		switch(o.tabletype)
		{
			case 0   :	s = GUI.class.getResource("/table_icon_empty.png").toExternalForm(); // /.png
						break;
			case 1   : 	s = GUI.class.getResource("/table-key-icon-reddot.png").toExternalForm();
						break;
			case 99  :  s = GUI.class.getResource("/database-small-icon.png").toExternalForm();
						break;						
			case 100 :  s = GUI.class.getResource("/journal-icon.png").toExternalForm();  
						break; 			
			case 101 :  s = GUI.class.getResource("/wal-icon.png").toExternalForm();
						break;
		}
		
	    ImageView iv = new ImageView(s);
		dmtn.setGraphic(iv);
 		
		if (walnode) {
			
		
				/* WAL-tree node - add child node of table */
				walNode.getChildren().add(dmtn);

				/* main db */	
				Platform.runLater( () -> {
			
				
				FXCollections.sort(walNode.getChildren(), new Comparator<TreeItem<NodeObject>>() {
				     
					@Override
					public int compare(TreeItem<NodeObject> o1, TreeItem<NodeObject> o2) {
					
						return o1.getValue().name.compareTo(o2.getValue().name);
						
					}
					
				});
				
			 });
			
		}
		else if (rjnode) {
			/* Rollback Journal */
			rjNode.getChildren().add(dmtn);
			
		}
		else{
			 /* main db */	
		
				 Platform.runLater( () -> {
					
						
	  			    job.getTreeItem().getChildren().add(dmtn);
		 
					 
					FXCollections.sort(job.getTreeItem().getChildren(), new Comparator<TreeItem<NodeObject>>() {
					     
						@Override
						public int compare(TreeItem<NodeObject> o1, TreeItem<NodeObject> o2) {
						
							return o1.getValue().name.compareTo(o2.getValue().name);
							
						}
						
					});

				 
			 });
			 
			    
					 
					 
			
		}
				
		String tp = getPath(dmtn);
		
		// save assignment between tree item's path an a tree item
		treeitems.put(tp, dmtn);
		
		ContextMenu tcm = createContextMenu(CtxTypes.TABLE,tablename,table,job); 
		
		
		table.setOnKeyPressed(new EventHandler<KeyEvent>(){
			
		    @Override
		    public void handle(KeyEvent event) {
			    if (!table.getSelectionModel().isEmpty())
			    {   	
			    	KeyCodeCombination copylineCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN);
			    	KeyCodeCombination copycellCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
					 
			    	if(copylineCombination.match(event))
			    	{	
				    	copyLineAction(table);
				    	event.consume();
				    }
			    	else if(copycellCombination.match(event))
			    	{
			    		copyCellAction(table);
			    		event.consume();
			    	}
			    	
			    }
		    }
		});
		
		table.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			   public void handle(javafx.scene.input.MouseEvent event) {

				   //System.out.println("Quelle : " + event.getTarget());
				   if(event.getTarget().toString().startsWith("TableColumnHeader"))
					   return;
				   
				   int row = -1;
				   TablePosition pos = null;
				   try 
				   {
				     pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			         row = pos.getRow();

				   }catch(Exception err) {
					   return;
				   }
				      
				   
				   // Item here is the table view type:
				   Object item = table.getItems().get(row);
				   
				
				   TableColumn col = pos.getTableColumn();

				   if(col == null)
					   	return;
				   
				   // this gives the value in the selected cell:
				   Object data = col.getCellObservableValue(item).getValue();

				   // get the relative virtual address (offset) from the table
			       TableColumn toff = (TableColumn) table.getColumns().get(2);
			       
				   // get the actual value of the currently selected cell
			       ObservableValue off =  toff.getCellObservableValue(row);
			      
				   
				   if (col.getText().equals("Offset"))
				   {
					   if(row >= 0)
					   {   
						   // get currently selected database
						   NodeObject no = getSelectedNode();
						   						   
						   String model = null;
						   
						   if (no.type == FileTypes.SQLiteDB)
							model = no.job.path;
						   else if (no.type == FileTypes.WriteAheadLog)
							model = no.job.wal.path;
					       else if (no.type == FileTypes.RollbackJournalLog)
					    	model = no.job.rol.path;
						   
										   
						   long position = -1;
						   try {
							   position = Long.parseLong((String)data);

							   HEXVIEW.go2(model, position);
							   
						   }catch(Exception err) {
							   
						   }
						   
					       
					   	}
				   }
				   else  //another column was clicked
				   {
					   boolean doubleclicked = false;
					   
					   if(event.getButton().equals(MouseButton.PRIMARY)){
				            if(event.getClickCount() == 2){
				                doubleclicked = true;
				            }
				        }	  
					   
					   if(data != null && doubleclicked){
					   	
					   		   String cellvalue = (String)data;
				    		   if (cellvalue.startsWith("[BLOB-"))
				   			   {
					   			    int from = cellvalue.indexOf("BLOB-");
					   	        	int to = cellvalue.indexOf("]");
					   	        	String number = cellvalue.substring(from+5, to);			
					   				int start = cellvalue.indexOf("<");
					   				int end   = cellvalue.indexOf(">");
					   				
					   				String type;
					   				if (end > 0) {
					   					type = cellvalue.substring(start+1,end);
					   				}
					   				else 
					   					type = "";
					   				
					   				if(type.equals("pdf"))
					   				{
					   		
					   					Platform.runLater(new Runnable() {
											 
											   public void run() {     
												   
													String path = GUI.baseDir + Global.separator + table.dbname + "_" + off.getValue() + "-" + number + "." + type;
											    	new PDFPreviewer(path).start(new Stage());									   	
											   }  
										});
					   					
					   					
					   				}
					   				if(type.equals("gif") || type.equals("bmp") || type.equals("png") || type.equals("jpg")|| type.equals("heic") || type.equals("tiff"))
					   				{		
					   					String uri = "file:" + Global.separator + Global.separator + GUI.baseDir + Global.separator + table.dbname + "_" + off.getValue() + "-" + number + "." + type;
					 
					   					getHostServices().showDocument(uri);
					   				}
				   			   }
						   
					   	   }
				   	  
				   }
				   
				   if(event.getButton() == MouseButton.SECONDARY) {
					  
					   //ContextMenu tcm = createContextMenu(CtxTypes.TABLE,table); 
					   tcm.show(table.getScene().getWindow(), event.getScreenX(), event.getScreenY());
				   }
				   
				  
				   
			   }
			});
		
		
	
		tables.put(tp, tablePane);
		return tp;
	}
	
	@SuppressWarnings("rawtypes")
	private ContextMenu createContextMenu(CtxTypes type,String tablename, FQTableView table,Job job){
		
		final ContextMenu contextMenu = new ContextMenu();
	    FQTableView mytable = table;
		
		// copy a single table line
		MenuItem mntcopyline = new MenuItem("Copy Line(s)");
		String s = GUI.class.getResource("/edit-copy.png").toExternalForm();
	    ImageView iv = new ImageView(s); 

    	KeyCodeCombination copylineCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN);
    	KeyCodeCombination copycellCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
    	KeyCodeCombination copyttCombination = new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN);

    	
	    mntcopyline.setAccelerator(copylineCombination);
	    mntcopyline.setGraphic(iv);
		mntcopyline.setOnAction(e ->{
			copyLineAction(mytable);     		
			e.consume();
		}
		);

		
		// copy the complete table line (with all cells)
		MenuItem mntcopycell= new MenuItem("Copy Cell");
	    s = GUI.class.getResource("/edit-copy.png").toExternalForm();
		iv = new ImageView(s);
		mntcopycell.setGraphic(iv);
	    mntcopycell.setAccelerator(copycellCombination);

		mntcopycell.setOnAction(e ->{
			copyCellAction(mytable);
			e.consume();
		}
		);
		
		// copy the complete table line (with all cells)
		MenuItem mntcopytt = new MenuItem("Copy Tooltip");
		s = GUI.class.getResource("/edit-copy.png").toExternalForm();
		iv = new ImageView(s);
		mntcopytt.setGraphic(iv);
	    mntcopytt.setAccelerator(copyttCombination);

		mntcopytt.setOnAction(e ->{
					copyToolTipAction(mytable);
					e.consume();
		}
		);
		
		
        SeparatorMenuItem sepA = new SeparatorMenuItem();
    
        s = GUI.class.getResource("/edit-find.png").toExternalForm();
        iv = new ImageView(s);
     	
        Menu analyze = new Menu("Convert...");
        analyze.setGraphic(iv);
       
        Menu blob = new Menu("BLOB to ..");
        blob.setGraphic(iv);
     
        analyze.getItems().add(blob);
        
        
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	CheckMenuItem selected = ((CheckMenuItem)e.getSource());
            	String text = selected.getText();
            	
            	List<MenuItem> items = blob.getItems();
            	for(int i=0; i < items.size();i++){
            		CheckMenuItem cmi = (CheckMenuItem)items.get(i);
            		if(cmi.isSelected() && !cmi.getText().equals(selected.getText()))
            		{
            			cmi.setSelected(false);
            		}
            
            	}
            	
            	switch(text) 
            	{
            	
            		case Names.BSON: 
            			job.convertto.put(tablename,Names.BSON);
            			break;
            		case Names.Fleece: 
        				job.convertto.put(tablename,Names.Fleece);
        			break;
            		case Names.MessagePack: 
        				job.convertto.put(tablename,Names.MessagePack);
        			break;
            		case Names.ThriftBinary: 
        				job.convertto.put(tablename,Names.ThriftBinary);
        			break;
            		case Names.ThriftCompact: 
        				job.convertto.put(tablename,Names.ThriftCompact);
        			break;
            		case Names.ProtoBuffer: 
            			job.convertto.put(tablename,Names.ProtoBuffer);
        			break;
        			
        				
            	}
            	table.refresh();
    			
            	
            } 
        }; 
        
        CheckMenuItem protob = new CheckMenuItem(Names.ProtoBuffer);
        protob.setOnAction(event);
        blob.getItems().add(protob);

        CheckMenuItem bson = new CheckMenuItem(Names.BSON);
        bson.setOnAction(event);
        blob.getItems().add(bson);

        CheckMenuItem flatbuffer = new CheckMenuItem(Names.FlatBuffer);
        flatbuffer.setOnAction(event);
        blob.getItems().add(flatbuffer);
        
        CheckMenuItem fleece = new CheckMenuItem(Names.Fleece);
        fleece.setOnAction(event);
        blob.getItems().add(fleece);
        
        CheckMenuItem msgpack = new CheckMenuItem(Names.MessagePack);
        msgpack.setOnAction(event);
        blob.getItems().add(msgpack);
        
        CheckMenuItem tbp = new CheckMenuItem(Names.ThriftBinary);
        tbp.setOnAction(event);
        blob.getItems().add(tbp);
    
        CheckMenuItem tcp = new CheckMenuItem(Names.ThriftCompact);
        tcp.setOnAction(event);
        blob.getItems().add(tcp);
        
        
        /* This section is used to create a check item to BASE64 support for table cells (experimental)*/
        
        EventHandler<ActionEvent> eventBASE64 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                if (((CheckMenuItem)e.getSource()).isSelected()) 
                {
                	/* enable BASE64 for this table */
                	System.out.println("BASE64 support enabled." + tablename);
                	job.inspectBASE64.add(tablename);
                	table.refresh();
                }
                else
                {
                	/* disable protobuffer inspection for this table */
                	System.out.println("Off.");
                	job.inspectBASE64.remove(tablename);
                	table.refresh();
                }
            } 
        }; 
        
        CheckMenuItem base64 = new CheckMenuItem("BASE64 to..");
        base64.setOnAction(eventBASE64);
        analyze.getItems().add(base64);
        
        
		contextMenu.getItems().addAll(mntcopyline,mntcopycell,mntcopytt,sepA,analyze);
			    
	    return contextMenu;
	}
	
	
	
	/**
	 * This method is an action handler. It provides a copy of
	 * the tool tip content to clip board. Note: The tool tip content
	 * can significantly differ from the cell value. BLOB values
	 * like property lists or protobuffers are decoded and displayed
	 * within the tool tip info. 
	 *  
	 * @param table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void copyToolTipAction(FQTableView table){
	 
		String tttype = null;
	                
        ObservableList<TablePosition> selection = table.getSelectionModel().getSelectedCells();
        // nothing selected -> leave copy action
        if (selection.size() == 0)
        	return;
       
        // where am I?
        TablePosition tp = selection.get(0); 
        int row = tp.getRow();
		int col = tp.getColumn();

        
        TableColumn tc = (TableColumn) table.getColumns().get(col);
        ObservableValue observableValue =  tc.getCellObservableValue(row);
        String columnname =  tc.getText();
        // get the relative virtual address (offset) from the table
        TableColumn toff = (TableColumn) table.getColumns().get(2);
        // get the actual value of the currently selected cell
        ObservableValue off =  toff.getCellObservableValue(row);
        
        
        String cellvalue = "";
       
        Iterator<TableDescriptor> tbls = table.job.headers.iterator();
    	while(tbls.hasNext()){
    		TableDescriptor td = tbls.next();
    		
    		// What is the SQLType of the selected cell?
    		if(td.tblname.equals(table.tablename)) {
        		tttype = td.getToolTypeForColumn(columnname);
            	
    		}	
    	}
    	
    	
    	if (null != tttype)
    		tttype = tttype.toUpperCase();
    	
      
        // not null-check: provide empty string for nulls
		if (observableValue != null) {			
			
			cellvalue = (String)observableValue.getValue();
			
			if (cellvalue.startsWith("[BLOB-"))
			{
			    int from = cellvalue.indexOf("BLOB-");
	        	int to = cellvalue.indexOf("]");
	        	String number = cellvalue.substring(from+5, to);
	        	 
				
				int start = cellvalue.indexOf("<");
				int end   = cellvalue.indexOf(">");
				
				String type;
				if (end > 0) {
					type = cellvalue.substring(start+1,end);
				}
				else 
					type = "bin";
				
				if(type.equals("java"))
					type = "bin";
				
					
				String path = GUI.baseDir + Global.separator + table.dbname + "_" + off.getValue() + "-" + number + "." + type;
				
				String data = "";
				
			 	if(cellvalue.contains("java"))
    	    	{
    	        	 data = Deserializer.decode(path);   	 
    	    	}	
			 	else if(cellvalue.contains("plist"))
    	    	{ 
    	           	 data = BPListParser.parse(path);       	   	           	
    	    	}
			 	else 
			 	{
			 		 data = BinaryLoader.parse2(path);
			 	}
			 	
			 	setContent(data);
				return;
			}
			
			if(tttype.equals("REAL") || tttype.equals("DOUBLE") || tttype.equals("FLOAT")) {

        		String bb = cellvalue;
            	int point = bb.indexOf(",");
                String firstpart;
            	if (point > 0)
                	firstpart = bb.substring(0, point);
                else
                	firstpart = bb;
               
            	String value = Auxiliary.int2Timestamp(firstpart);
            	setContent("[" + tttype + "] " +  bb + "\n" + value);
          	    return;
        	}
        	if(tttype.equals("INTEGER") || tttype.equals("INT") ) {
        		
        		String bb = cellvalue;
            	
        		String value = Auxiliary.int2Timestamp(bb);
        		setContent("[" + tttype + "] " +  bb + "\n" + value);
          	    return;

        	}

			
		}
    
        content.putString("[" + tttype + "] " + cellvalue);
        clipboard.setContent(content);
       
	
	}
	
	private void setContent(String data){
		System.out.println(" Data::" + data);
	 	content.putString(data);
		clipboard.setContent(content);
	}
	
	
	/**
	 * Action handler method.   
	 * @param table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void copyCellAction(FQTableView table){

	 	final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
                 
        ObservableList<TablePosition> selection = table.getSelectionModel().getSelectedCells();
        if (selection.size() == 0)
        	return;
        TablePosition tp = selection.get(0); 
        int row = tp.getRow();
		int col = tp.getColumn();

        
        TableColumn tc = (TableColumn) table.getColumns().get(col);
        ObservableValue observableValue =  tc.getCellObservableValue(row);
		
        String cellvalue = "";
        
		// not null-check: provide empty string for nulls
		if (observableValue != null) {			
			cellvalue = (String)observableValue.getValue();
			
			// handle binary values like protocol buffers, java serials or property lists
			if (cellvalue.startsWith("[BLOB-"))
			{
			    int from = cellvalue.indexOf("BLOB-");
	        	int to = cellvalue.indexOf("]");
	        	String number = cellvalue.substring(from+5, to);
	        	 
				
				int start = cellvalue.indexOf("<");
				int end   = cellvalue.indexOf(">");
				
				String type;
				if (end > 0) {
					type = cellvalue.substring(start+1,end);
				}
				else 
					type = "bin";
				
				if(type.equals("java"))
					type = "bin";
				
			    tc = (TableColumn) table.getColumns().get(2);
				ObservableValue off =  tc.getCellObservableValue(row);
					
				String path = GUI.baseDir + Global.separator + table.dbname + "_" + off.getValue() + "-" + number + "." + type;
				String data = BinaryLoader.parse2(path);
				System.out.println(" Data" + data);				
				content.putString(data.toUpperCase());
				clipboard.setContent(content);
				return;
			}
			
		}
    
        content.putString(cellvalue);
        clipboard.setContent(content);
       
        
	}
	
	final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

	
	@SuppressWarnings("rawtypes")
	public void cellvalue2clipboard(String cellvalue, FQTableView table, int row){
		
	    
		int from = cellvalue.indexOf("BLOB-");
      	int to = cellvalue.indexOf("]");
      	String number = cellvalue.substring(from+5, to);
      	 
			
			int start = cellvalue.indexOf("<");
			int end   = cellvalue.indexOf(">");
			
			String type;
			if (end > 0) {
				type = cellvalue.substring(start+1,end);
			}
			else 
				type = "bin";
			
			if(type.equals("java"))
				type = "bin";
		
		    TableColumn tc = (TableColumn) table.getColumns().get(2);
			ObservableValue off =  tc.getCellObservableValue(row);
				
			String path = GUI.baseDir + Global.separator + table.dbname + "_" + off.getValue() + "-" + number + "." + type;
			String data = BinaryLoader.parse2(path);
			System.out.println(" Data" + data);				
			content.putString(data.toUpperCase());
			clipboard.setContent(content);
			return;
	}
	
	
	/**
	 * Action handler method.   
	 * @param table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void copyLineAction(TableView table){
		
		StringBuffer sb = new StringBuffer();			
	 	final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        ObservableList<TablePosition> selection = table.getSelectionModel().getSelectedCells();	        
        Iterator<TablePosition> iter = selection.iterator();
        
        while(iter.hasNext()) {
        	
        	TablePosition pos = iter.next();	        	
        	ObservableList<String> hl = (ObservableList<String>)table.getItems().get(pos.getRow());
        	  sb.append(hl.toString() + "\n");
        }
        System.out.println("Write value to clipboard " + sb.toString());
        content.putString(sb.toString());
        clipboard.setContent(content);

	}
	

	/**
	 * Returns the Path object of for a given treeNode.
	 * @param treeNode
	 * @return TreePath 
	 */
	public static TreePath getPath(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<Object>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}

	/**
	 * Returns the tree-path for a given node item. 
	 * @param item
	 * @return path as string
	 */
	private String getPath(TreeItem<NodeObject> item)
	{
		
		// create the entire path to the selected item.
        String path = item.getValue().name;
        TreeItem<NodeObject> tmp = item.getParent();

        while (tmp != null) {
           path = tmp.getValue().name + "/" + path;
           tmp = tmp.getParent();
        }
		//System.out.println("getPath()::"+ path);
		return path;
		
//		StringBuilder pathBuilder = new StringBuilder();
//		for (;item != null ; item = item.getParent()) {
//		    pathBuilder.insert(0, item.getValue());
//		    pathBuilder.insert(0, "/");
//		}
//		String path = pathBuilder.toString();
//		System.out.println("getPath()::"+ path);
//		return path;
	}
	
	/**
	 * Returns the currently selected treenode.
	 * @return
	 */
	private NodeObject getSelectedNode()
	{
	   return tree.getSelectionModel().getSelectedItem().getValue();	
	}
	
	/**
	 * Sometimes we need an empty table as placeholder.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	protected void prepare_tree() {

		if (tree == null) {
			tree = new TreeView<NodeObject>(root);
		 	tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			//tree.setCellRenderer(new CustomTreeCellRenderer());
		}
		
		rightSide.getChildren().add(rootPane);

		tree.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem>() {

		        @SuppressWarnings({ "unchecked" })
				@Override
		        public void changed(ObservableValue observable, TreeItem oldValue, TreeItem newValue) {

		       
		            TreeItem<NodeObject> selectedItem = (TreeItem<NodeObject>) newValue;
		            if (null == selectedItem)
		            {
		            	// after closeAll() we have to set back everything
		            	rightSide.getChildren().clear();
		            	rightSide.getChildren().add(rootPane);
		            	return;
		            }	
		          
		            NodeObject node = selectedItem.getValue();
		            if (null != node.tablePane)
		            {
		            	Platform.runLater(new Runnable() {
		    	            @Override public void run() {
		    	    
		            	
		    	            		rightSide.getChildren().clear();
		    	            		rightSide.getChildren().add(node.tablePane);
		    	            		
		    	            		VBox.setVgrow(node.tablePane,Priority.ALWAYS);

		    	            }
		            	});
		            }
		            else // no table -> show db pane
		            {
		            	
		            	if(node.isRoot){
		            		rightSide.getChildren().clear();
			            	rightSide.getChildren().add(rootPane);
			            }
		            	else{


							 Platform.runLater(() -> {
									String tp = getPath(selectedItem);
							 	StackPane dbpanel  = (StackPane)tables.get(tp);
							 
							 	rightSide.getChildren().clear();
							 	if(null != dbpanel)
							 	{
							 		rightSide.getChildren().add(dbpanel);		
							 		dbpanel.setPrefHeight(4000);
							 		VBox.setVgrow(dbpanel,Priority.ALWAYS);
							 	}
							
							 
							 });
			            }
		            }
		        }

		});
		
	}
	

	/**
	 * Show an open dialog and import <code>sqlite</code>-file.
	 * 
	 */
	public synchronized void open_db(File f) {
		File file = f;
		
		if (file == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll(
				    new FileChooser.ExtensionFilter("<all>", "*.*")
					,new FileChooser.ExtensionFilter(".sqlite", "*.sqlite")
				    ,new FileChooser.ExtensionFilter(".db", "*.db")
				);
		    file = fileChooser.showOpenDialog(this.stage);
		    if (file != null) {
                fileChooser.setInitialDirectory(file.getParentFile());
            }
		}

			
		if (file == null)
			return;
		
		
			
		/* check file size - the size has to be at least 512 Byte */
		if (file.length() < 512)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("File size is smaller than 512 bytes. Import stopped.");
			alert.showAndWait();		
			return;
		}
		else if (file.length()> 8000000000L)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("File is to large. Import stopped.");
			alert.showAndWait();		
			return;
		}
		RandomAccessFile raf = null;
		boolean abort = false;
		/* check header string for magic number to match */
		try 
		{
			raf = new RandomAccessFile(file,"r");
			byte h[] = new byte[16];
			raf.read(h);
			if (!Auxiliary.bytesToHex(h).equals(Job.MAGIC_HEADER_STRING)) // we currently
			{
				abort = true;
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setContentText("Couldn't find a valid SQLite3 magic. Import stopped");
				alert.showAndWait();
				
			}
		}
		catch(Exception err)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("IO-Exception. Cloud not open file.");
			alert.showAndWait();
			abort = true;
		}
		finally		
		{
			try { raf.close(); } catch(IOException err){}
		}
	    /* no valid file or no permissions -> cancel import */
		if (abort)
			return;
		
		FileInfo info = new FileInfo(file.getAbsolutePath());
		
		DBPropertyPanel panel = new DBPropertyPanel(info,file.getName());
		panel.setPrefHeight(4000);
		VBox.setVgrow(panel,Priority.ALWAYS);
		
			
		NodeObject o = new NodeObject(file.getName(), null, -1, FileTypes.SQLiteDB, 99);
		TreeItem<NodeObject> dbNode = new TreeItem<NodeObject>(o); 
		dbNode.setGraphic(createFadeTransition("loading..."));	
		root.getChildren().add(dbNode);
		
		/* insert Panel with general header information for this database */
		String tp = getPath(dbNode);
			
		Job job = new Job();
		tables.put(tp, panel);
			
			
		/* Does a companion RollbackJournal exist ? */
		if (doesRollbackJournalExist(file.getAbsolutePath()) > 0) {
				NodeObject ro = new NodeObject(file.getName() + "-journal", null, -1,
						FileTypes.RollbackJournalLog, 100);
				rjNode = new TreeItem<NodeObject>(ro);
				
				rjNode.setGraphic(createFadeTransition("loading..."));
				
				root.getChildren().add(rjNode);

				/* insert Panel with general header information for this database */
				String tpr = getPath(rjNode);
				FileInfo rinfo = new FileInfo(file.getAbsolutePath()+"-journal");
				RollbackPropertyPanel rpanel = new RollbackPropertyPanel(rinfo);
				tables.put(tpr, rpanel);

				job.rjNode = rjNode;
				job.setRollbackPropertyPanel(rpanel);
				ro.job = job;
				ro.job.readRollbackJournal = true;
				ro.job.readWAL = false;

		}

		/* Does a companion WAL-archive exist ? */
		else if (doesWALFileExist(file.getAbsolutePath()) > 0) {

			NodeObject wo = new NodeObject(file.getName() + "-wal", null, -1, FileTypes.WriteAheadLog, 101);
			walNode = new TreeItem<NodeObject>(wo);
			walNode.setGraphic(createFadeTransition("loading..."));
			
			root.getChildren().add(walNode);

			/* insert Panel with general header information for this database */
			String tpw = getPath(walNode);
			FileInfo winfo = new FileInfo(file.getAbsolutePath()+"-wal");
			WALPropertyPanel wpanel = new WALPropertyPanel(winfo,this);
			tables.put(tpw, wpanel);
			job.walNode =  walNode;
			
			job.setWALPropertyPanel(wpanel);
			wo.job = job;
			wo.job.readWAL = true;
			wo.job.readRollbackJournal = false;

		}

		tree.refresh();
	
		job.setPropertyPanel(panel);
		job.setGUI(this);
		job.setTreeItem(dbNode);
		job.setPath(file.getAbsolutePath());
		Importer.createAndShowGUI(this, file.getAbsolutePath(), job, dbNode);
		o.job = job;	

		int idx = tree.getRow(dbNode);
	
		tree.getSelectionModel().select(idx);
		tree.scrollTo(idx);
	
	}
	
	/**
	 * During import a fading "loading.." message is shown. This method 
	 * creates an label for this purpose.
	 * @param msg
	 * @return
	 */
	private Label createFadeTransition(String msg) 
	{
		  Label l = new Label(msg);
		  FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.0),l);
	      fadeTransition.setFromValue(1.0);
	      fadeTransition.setToValue(0.0);
	      fadeTransition.setCycleCount(Animation.INDEFINITE);
	        fadeTransition.play();
	      return l;
	}

	/**
	 * Try to find out, if there is a companion WAL-Archive. This file can be found
	 * in the same directory as the database file and also has the same name as the
	 * database, but with 4 character added to the end – “-wal”
	 *
	 * 
	 * @param dbfile
	 * @return
	 */
	public static long doesWALFileExist(String dbfile) {
		String walpath = dbfile + "-wal";
		Path path = Paths.get(walpath);

		if (Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
			try {
				return Files.size(path);
			} catch (IOException e) {
				e.printStackTrace();
			}

		return -1L;

	}

	/**
	 * Try to find out, if there is a companion RollbackJournal-File. This file can
	 * be found in the same directory as the database file and also has the same
	 * name as the database, but with 8 character added to the end – “-journal”
	 * 
	 * 
	 * @param dbfile
	 * @return file size of the rollback-journal archive or -1 if no -journal files exists. 
	 */
	public static long doesRollbackJournalExist(String dbfile) {
		String rolpath = dbfile + "-journal";
		Path path = Paths.get(rolpath);

		if (Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
			try {
				return Files.size(path);
			} catch (IOException e) {
				e.printStackTrace();
			}

		return -1L;

	}

	/**
	 * Print a new message to the trace window on the bottom of the screen. 
	 * @param message
	 */
	protected void doLog(String message) {
		AppLog.info(message);
	}

	/**
	 * This method is called to write the contents of a database to a CSV file. 
	 * 
	 * @param no Database node for export
	 */
	private void export_table(NodeObject no) {

		if (null == no)
			return;
			
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export Database");	
        fileChooser.setInitialFileName(prepareDefaultFileName(no.name));
		File f = fileChooser.showSaveDialog(stage);
        
		if(null == f)
			return;
		
		boolean success = false;	
		
		switch(no.tabletype)
		{
			case 0      :	// table 					   
			case 1      :   // index
					
						success = no.job.exportResults2File(f, no.name,-1);
						break;
						
			case 99  :   // database
				
						 success = no.job.exportResults2File(f, no.name,Global.REGULAR_DB_FILE);
						 break;
					
			case 100 :   // journal
					 			
						 success = no.job.exportResults2File(f, no.name,Global.ROLLBACK_JOURNAL_FILE);
						 break;
			
				
			case 101 :   // wal 
					    
						 success = no.job.exportResults2File(f, no.name,Global.WAL_ARCHIVE_FILE);
						 break;
						 
			default  :  // root;		
		}
		
		if(success) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success Info");
			alert.setContentText("Data of " + no.name + " exported successfully to \n" + f.getAbsolutePath());
			alert.showAndWait();
		}
		
	}
	
	/**
	 * Returns a filename with a time stamp in ISO_DATE_TIME format.
	 * @param nameofnode
	 * @return
	 */
	private String prepareDefaultFileName(String nameofnode){
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter df;
		df = DateTimeFormatter.ISO_DATE_TIME; // 2020-01-31T20:07:07.095
		String date = df.format(now);
		date = date.replace(":","_");
		return nameofnode + date + ".csv";
	}
	
	
	/**
	 * Prepares the table row - with all columns - for a TableView.  
	 * @param tp 
	 * @param linenumber the current line number (we put this number in front of the datarecord).
	 * @param data the actual line of data to fill in
	 * @param isWALTable whether or not the table to built is a WAL-Table 
	 * @return
	 */
	private ObservableList<String> prepareRow(int linenumber, LinkedList<String> row,boolean isWALTable)
	{
				
		ObservableList<String> list = FXCollections.observableArrayList(row);
		list.add(0,String.valueOf(++linenumber));	
		
		// add line number 
		return list;
	}
	
	
	/**
	 * This method is used to insert new records into a output table. 
	 * 
	 * @param tp  the table name
	 * @param data  String array with data rows
	 * @param isWALTable whether or not this table to fill is a WAL-Table
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void update_table(String treepath, ObservableList<LinkedList<String>> rows, boolean isWALTable) {
		
		
		// define array list for all table rows 
		ObservableList<ObservableList> obdata = FXCollections.observableArrayList();
		
	    // first get the right table
	    FQTableView tb = null;
		TextField filterField = null;
		ComboBox columnselector;
		Button clearFilter;
		
		try {	
			VBox tablepanel = (VBox)tables.get(treepath);	
			VBox.setVgrow(tablepanel,Priority.ALWAYS);
			HBox filterpane = (HBox)tablepanel.getChildren().get(0);
		    columnselector = (ComboBox) filterpane.getChildren().get(0);
			filterField = (TextField) filterpane.getChildren().get(1);
			clearFilter  = (Button) filterpane.getChildren().get(2);
		    tb = (FQTableView)tablepanel.getChildren().get(1);  
		    Label statusline = (Label)tablepanel.getChildren().get(2);
		    String text = statusline.getText();
		    statusline.setText(text + " | rows: " + rows.size());
		} catch (Exception err) {
			System.err.println(err);
			return ;
		}
		
		
		/* Just in case ;-) */
		if (tb == null) {
			doLog(">>>> Unkown tablename" + treepath);
			return;
		}
	
		//final FQTableView ftb = tb;
		
		// determine the right treeitem for a given treepath from hashtable
		TreeItem<NodeObject> node = treeitems.get(treepath);
		
		if (null != node && rows.size()>0 )
		{

			node.getValue().hasData = true;
			
			Platform.runLater(new Runnable() {
	            @Override public void run() {
	            	String s = null;
	            	if (node.getValue().tabletype == 0) // normal table with rows
	            		s = GUI.class.getResource("/table-icon.png").toExternalForm();  
	            	if (node.getValue().tabletype == 1) // index table with rows
	            		s = GUI.class.getResource("/table-key-icon.png").toExternalForm();  
	          	
	            	if (null == s)
	            		return;
	            	ImageView iv = new ImageView(s);
	    			node.setGraphic(null);
	    			node.setGraphic(iv); 
	    			TreeItem.graphicChangedEvent();
	    			TreeItem.valueChangedEvent();
	    			tree.refresh();
	    		}
	        });
		
		}
		
		final TextField ff = filterField;
		final ComboBox cs = columnselector;
		clearFilter.setOnAction(e ->{ ff.clear(); cs.getSelectionModel().select(0); });
		
		
	    // iterate over row array to create table rows  
		for(int i = 0; i < rows.size(); i++)
		{
			LinkedList<String> data = rows.get(i);
			// rearrange the row cell values for table view
			obdata.add(FXCollections.observableList(prepareRow(i,data,isWALTable)));
			
		}
		
		Iterator it = tb.getColumns().iterator();
		ArrayList<String> cnames = new ArrayList<String>();
		while (it.hasNext()){
			TableColumn tc = (TableColumn) it.next();
			cnames.add(tc.getText().toLowerCase());
		}
		
		final List<String> fnames = cnames;
		//final ObservableList<ObservableList> fdata = obdata; 	
		
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<ObservableList> filteredData = new FilteredList<>(obdata, p -> true);

		final TextField  ffield = filterField; 
		/* if column selection for column filter has changed -> do start new filtering */
		columnselector.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			filter(treepath, fnames, columnselector, filteredData, null, ffield.textProperty().getValue());				 
			updatestatusline(treepath,filteredData.size(),rows.size());
		}); 
		
		// 2. Set the filter predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {						
			  filter(treepath, fnames, columnselector, filteredData, oldValue, newValue);
			  updatestatusline(treepath,filteredData.size(),rows.size());	
		});	
				
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<ObservableList> sortedData = new SortedList<>(filteredData);
				
		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(tb.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table & update TableView with data set
		tb.setItems(sortedData);
		
		final TableView tb2 = tb;
		
		// 6. Update Status line 
		tb.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change c) -> {
         
			   if (tb2 != null){
		    	   int selecteditems = tb2.getSelectionModel().getSelectedCells().size();
				   VBox tablepanel = (VBox)tables.get(treepath);	
				   Label statusline = (Label)tablepanel.getChildren().get(2);
				   String text = statusline.getText();
				   int idx = text.indexOf(" | rows: ");
				   if (idx > 0)
					   statusline.setText(text.substring(0,idx) + " | rows: " + rows.size() + " | selected rows: " + selecteditems);	
		       }
		    	   
		    }
			
        );
 				
	}

	
	private int filter(String treepath, List<String> fnames, ComboBox<String> columnselector, @SuppressWarnings("rawtypes") FilteredList<ObservableList> filteredData, String oldValue, String newValue){		
		
		filteredData.setPredicate(r -> {
		
			// Compare the column values of all row columns with filter text.
			String lowerCaseFilter = newValue.toLowerCase();
			
			String clvalue = (String)columnselector.getSelectionModel().getSelectedItem();
			
			String searchfor = "";
			String cname = "";
			
			if((clvalue != null && !clvalue.startsWith("All Columns")))
			{
				//case: special column is selected
				
				if (clvalue.equals("Status"))
					clvalue = "";
				
				if (clvalue != null) {
					cname = clvalue.toLowerCase();
					searchfor = lowerCaseFilter;
				}
				
				searchfor = searchfor.trim();
				System.out.println("searchfor " + searchfor);
				System.out.println("cname "+ cname);
				int cnumber = fnames.indexOf(cname);
				cnumber++;		
				
				if (r.size()>cnumber) {
				
					switch(cname) {
						
						case "pll"  : cnumber = 2;
						break;
						
						case "hl"   : cnumber = 3;
						break;
					
						case ""     : cnumber = 4;
						break;
						
						case "offset"   : cnumber = 5;
						break;
					
					}
					
					String value = (String)r.get(cnumber);
					System.out.println("Value " + value + "Search for " + searchfor);
					if (StringUtils.containsIgnoreCase(value, searchfor))		
					{	
						return true;
					}
				}
			}
			else {
				// case: search all columns in a row
				for (int cc = 0; cc < r.size(); cc++) {
					// get next row
					String value = (String)r.get(cc);
					{
						if (StringUtils.containsIgnoreCase(value, newValue))	
						{
							return true; // Filter matches name
						}
					}
				}
	
			}
			
			return false; // Does not match.
			
		}); // end of setPredicate()
		
		return filteredData.size();
	} // end of filter()

	private void updatestatusline(String treepath,int number, int total){
		   
		    VBox tablepanel1 = (VBox)tables.get(treepath);	
		    Label statusline1 = (Label)tablepanel1.getChildren().get(2);
			String text1 = statusline1.getText();
			int idx1 = text1.indexOf(" | ");
			if (idx1 > 0)
				statusline1.setText(text1.substring(0,idx1) + " | showing " + number + " of "+ total + " rows ");
			else
				statusline1.setText(" | showing " + number + " of "+ total + " rows ");
	}
	
	

	
	
	public Hashtable<Object, String> getRowcolors() {
		return rowcolors;
	}

	public void setRowcolors(Hashtable<Object, String> rowcolors) {
		this.rowcolors = rowcolors;
	}

	

	private class CustomComparator implements Comparator<String>{

	    @Override
	    public int compare(String o1, String o2) {
	    
	    	if (o1 == null && o2 == null) 
	    		return 0;
	        if (o1 == null) 
	        	return -1;
	        if (o2 == null) 
	        	return 1;

	        if (o1.length() == 0)
	        	return -1;
	        
	        char ch = o1.charAt(0);
	       
	        // only if o1 start with a number or a sign
	        if((ch >= '0' && ch <= '9') || ch =='-' || ch =='+')
	        {
	        	Integer i1=null;
	        		try{ i1=Integer.valueOf(o1); } catch(NumberFormatException ignored){}
	        	Integer i2=null;
	        		try{ i2=Integer.valueOf(o2); } catch(NumberFormatException ignored){}

	        	if(i1==null && i2==null) 
	        		return o1.compareTo(o2);
	        	if(i1==null) 
	        		return -1;
	        	if(i2==null) 
	        		return 1;

	        	return i1-i2;
	        }
	        
	        // o1 does not start with a number -> compare String objects as usual 
	        return o1.compareTo(o2);
	    }
	}
	

} // End of class GUI


