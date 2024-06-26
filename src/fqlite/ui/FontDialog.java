package fqlite.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import fqlite.base.GUI;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class FontDialog extends javafx.scene.control.Dialog<Font> {
    
    private FontPanel fontPanel;
    static  Font defaultFont;
    static  javafx.scene.Node root;
     
    public FontDialog(Font defaultFont, javafx.scene.Node rootelement) {
        fontPanel = new FontPanel();
        root = rootelement;
        FontDialog.defaultFont = defaultFont;
        
        setResultConverter(dialogButton -> dialogButton == ButtonType.PREVIOUS ? fontPanel.getFont() : null);
        
        final DialogPane dialogPane = getDialogPane();
        
        setTitle("Select font");
        dialogPane.setHeaderText("Select font");

        // FIXME extract to CSS
        String s = GUI.class.getResource("/icon_checked.png").toExternalForm();		
        dialogPane.setGraphic(new ImageView(new Image(s)));
        dialogPane.getButtonTypes().addAll(ButtonType.PREVIOUS);
       
        Button button = new Button("<Select Default>");
        button.setOnAction(e -> { fontPanel.setFont(defaultFont);});
   
        VBox vb = new VBox();
        vb.getChildren().add(fontPanel);
        vb.getChildren().add(button);
        dialogPane.setContent(vb);
    }
    

    
    /**************************************************************************
     * 
     * Support classes
     * 
     **************************************************************************/

    /**
     * Font style as combination of font weight and font posture. 
     * Weight does not have to be there (represented by null)
     * Posture is required, null posture is converted to REGULAR
     */
    private static class FontStyle implements Comparable<FontStyle> {

        private FontPosture posture; 
        private FontWeight weight;

        public FontStyle( FontWeight weight, FontPosture posture ) {
            this.posture = posture == null? FontPosture.REGULAR: posture;
            this.weight = weight;
        }

        public FontStyle() {
            this( null, null);
        }

        public FontStyle(String styles) {
            this();
            String[] fontStyles = (styles == null? "": styles.trim().toUpperCase()).split(" ");
            for ( String style: fontStyles) {
                FontWeight w = FontWeight.findByName(style);
                if ( w != null ) {
                    weight = w;
                } else {
                    FontPosture p = FontPosture.findByName(style);
                    if ( p != null ) posture = p;
                }
            }
        }

        public FontPosture getPosture() {
            return posture;
        }

        public FontWeight getWeight() {
            return weight;
        }


        @Override public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((posture == null) ? 0 : posture.hashCode());
            result = prime * result + ((weight == null) ? 0 : weight.hashCode());
            return result;
        }

        @Override public boolean equals(Object that) {
            if (this == that)
                return true;
            if (that == null)
                return false;
            if (getClass() != that.getClass())
                return false;
            FontStyle other = (FontStyle) that;
            if (posture != other.posture)
                return false;
            if (weight != other.weight)
                return false;
            return true;
        }

        private static String makePretty(Object o) {
            String s = o == null? "": o.toString();
            if ( !s.isEmpty()) { 
                s = s.replace("_", " ");
                s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            }
            return s;
        }

        @Override public String toString() {
            return String.format("%s %s", makePretty(weight), makePretty(posture) ).trim();
        }

        private <T extends Enum<T>> int compareEnums( T e1, T e2) {
            if ( e1 == e2 ) return 0;
            if ( e1 == null ) return -1;
            if ( e2 == null ) return 1;
            return e1.compareTo(e2);
        }

        @Override public int compareTo(FontStyle fs) {
            int result = compareEnums(weight,fs.weight);
            return ( result != 0 )? result: compareEnums(posture,fs.posture);
        }

    }    


    private static class FontPanel extends GridPane {
        private static final double HGAP = 10;
        private static final double VGAP = 5;

        private static final Predicate<Object> MATCH_ALL = new Predicate<Object>() {
            @Override public boolean test(Object t) {
                return true;
            }
        };

        private static final Double[] fontSizes = new Double[] {8d,9d,11d,12d,14d,16d,18d,20d};

        private static List<FontStyle> getFontStyles( String fontFamily ) {
            Set<FontStyle> set = new HashSet<FontStyle>();
            for (String f : Font.getFontNames(fontFamily)) {
                set.add(new FontStyle(f.replace(fontFamily, "")));
            }

            List<FontStyle> result =  new ArrayList<FontStyle>(set);
            Collections.sort(result);
            return result;

        }


        private final FilteredList<String> filteredFontList = new FilteredList<>(FXCollections.observableArrayList(Font.getFamilies()), MATCH_ALL);
        private final FilteredList<FontStyle> filteredStyleList = new FilteredList<>(FXCollections.<FontStyle>observableArrayList(), MATCH_ALL);
        private final FilteredList<Double> filteredSizeList = new FilteredList<>(FXCollections.observableArrayList(fontSizes), MATCH_ALL);

        private final ListView<String> fontListView = new ListView<String>(filteredFontList);
        private final ListView<FontStyle> styleListView = new ListView<FontStyle>(filteredStyleList);
        private final ListView<Double> sizeListView = new ListView<Double>(filteredSizeList);
        private final Text sample = new Text("\ud83d\udc3b" + "\n What is bear + 1 ? " + "\ud83d\udc3c");

        public FontPanel() {
            setHgap(HGAP);
            setVgap(VGAP);
            setPrefSize(500, 300);
            setMinSize(500, 300);

            ColumnConstraints c0 = new ColumnConstraints();
            c0.setPercentWidth(60);
            ColumnConstraints c1 = new ColumnConstraints();
            c1.setPercentWidth(25);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setPercentWidth(15);
            getColumnConstraints().addAll(c0, c1, c2);

            RowConstraints r0 = new RowConstraints();
            r0.setVgrow(Priority.NEVER);
            RowConstraints r1 = new RowConstraints();
            r1.setVgrow(Priority.NEVER);
            RowConstraints r2 = new RowConstraints();
            r2.setFillHeight(true);
            r2.setVgrow(Priority.NEVER);
            RowConstraints r3 = new RowConstraints();
            r3.setPrefHeight(250);
            r3.setVgrow(Priority.NEVER);
            getRowConstraints().addAll(r0, r1, r2, r3);

           
            // layout hello.dialog
            add(new Label("Font"), 0, 0);
            //fontSearch.setMinHeight(Control.USE_PREF_SIZE);
            //            add( fontSearch, 0, 1);
            add(fontListView, 0, 1);
            fontListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override public ListCell<String> call(ListView<String> listview) {
                    return new ListCell<String>() {
                        @Override protected void updateItem(String family, boolean empty) {
                            super.updateItem(family, empty);

                            if (! empty) {
                                setFont(Font.font(family));
                                setText(family);
                            } else {
                                setText(null);
                            }
                        }
                    };
                }
            });


            ChangeListener<Object> sampleRefreshListener = new ChangeListener<Object>() {
                @Override public void changed(ObservableValue<? extends Object> arg0, Object arg1, Object arg2) {
                    refreshSample();
                }
            };

            fontListView.selectionModelProperty().get().selectedItemProperty().addListener( new ChangeListener<String>() {

                @Override public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                    String fontFamily = listSelection(fontListView);
                    styleListView.setItems(FXCollections.<FontStyle>observableArrayList(getFontStyles(fontFamily)));       
                    refreshSample();
                }});

            add( new Label("Style"), 1, 0);
            //            postureSearch.setMinHeight(Control.USE_PREF_SIZE);
            //            add( postureSearch, 1, 1);
            add(styleListView, 1, 1);
            styleListView.selectionModelProperty().get().selectedItemProperty().addListener(sampleRefreshListener);

            add( new Label("Size"), 2, 0);
            //            sizeSearch.setMinHeight(Control.USE_PREF_SIZE);
            //            add( sizeSearch, 2, 1);
            add(sizeListView, 2, 1);
            sizeListView.selectionModelProperty().get().selectedItemProperty().addListener(sampleRefreshListener);

            final double height = 45;
            final DoubleBinding sampleWidth = new DoubleBinding() {
                {
                    bind(fontListView.widthProperty(), styleListView.widthProperty(), sizeListView.widthProperty());
                }

                @Override protected double computeValue() {
                    return fontListView.getWidth() + styleListView.getWidth() + sizeListView.getWidth() + 3 * HGAP;
                }
            };
            StackPane sampleStack = new StackPane(sample);
            sampleStack.setAlignment(Pos.CENTER_RIGHT);
            sampleStack.setMinHeight(height);
            sampleStack.setPrefHeight(height);
            sampleStack.setMaxHeight(height);
            sampleStack.prefWidthProperty().bind(sampleWidth);
            Rectangle clip = new Rectangle(0, height);
            clip.widthProperty().bind(sampleWidth);
            sampleStack.setClip(clip);
            add(sampleStack, 0, 3, 1, 3);
        }

        public void setFont(final Font font) {
            final Font _font = font == null ? Font.getDefault() : font;
            if (_font != null) {
                selectInList( fontListView,  _font.getFamily() );
                //selectInList( styleListView, _font.getStyle());
                selectInList( sizeListView,  _font.getSize() );
            }
        }

        public Font getFont() {
            try {
                FontStyle style = listSelection(styleListView);
                
                if ( style == null ) {
                    Font f =  Font.font(
                            listSelection(fontListView),
                            listSelection(sizeListView));
                    System.out.println("Style 1" + f.getStyle());
                    root.setStyle("-fx-font: "+ listSelection(sizeListView)  +" \""+ f.getName() + "\"; ");
                    return f; 
                } else { 
                    Font f = Font.font(
                            listSelection(fontListView),
                            style.getWeight(),
                            style.getPosture(),
                            listSelection(sizeListView));
                    System.out.println("Style 2" + f.getStyle() + " " + style.getWeight() + " " +style.getPosture() + " " + listSelection(sizeListView));
                    root.setStyle("-fx-font: "+ listSelection(sizeListView)  +" \""+ f.getName() + "\"; ");
                    return f;
                }

            } catch( Throwable ex ) {
                return null;
            }
        }

        private void refreshSample() {
            System.out.println(getFont());
            sample.setFont(getFont());

            
           // String os = System.getProperty("os.name","generic").toLowerCase(Locale.US);
           // if (os.indexOf("mac") > 0) {
           //     FontDialog.root.setStyle("-fx-font-size: 14pt");           
           // }
            
        }

        @SuppressWarnings("unused")
		private <T> void selectInList( final ListView<T> listView, final T selection ) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    listView.scrollTo(selection);
                    listView.getSelectionModel().select(selection);
                }
            });
        }

        private <T> T listSelection(final ListView<T> listView) {
            return listView.selectionModelProperty().get().getSelectedItem();
        }
    }  
}