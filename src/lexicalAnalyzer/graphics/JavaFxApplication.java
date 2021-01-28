package lexicalAnalyzer.graphics;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lexicalAnalyzer.Main;
import lexicalAnalyzer.logic.Token;
import lexicalAnalyzer.logic.TokenType;

/**
 * Is designed to show the table of tokens which its tokens are obtained from the analyser of the Main class.
 */
public class JavaFxApplication extends Application {
    private ObservableList<Token> data = FXCollections.observableArrayList();

    /**
     * Calls the launch method passing the args String array as the argument.
     * @param args
     * the arguments that it passes to the launch method.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Shows the table of the tokens with two columns in a new made scene.
     * @param stage
     * the stage that its scene would be set in the method.
     */
    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 600, 600, Color.rgb(240, 240, 240));
        TableView table = initializeTable();
        fillColumns();
        Label label = initializeLabel("Tokens");
        pane.getChildren().addAll(table, label);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Lexical Analyser");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Fills the data observable list with the tokens from the token list of the Main class's analyser.
     */
    private void fillColumns(){
        Main.start();
        data.addAll(Main.getAnalyzer().getTokens());
    }

    /**
     * Puts a label on the coordinates 30, 30 of the scene named as the input string, title.
     * @param title
     * would be set as the label's title string.
     * @return
     * returns a label as specified above.
     */
    private Label initializeLabel(String title){
        Label label  = new Label(title);
        label.relocate(30, 30);
        label.setScaleX(2);
        label.setLayoutY(4);
        label.setTextFill(Color.BLACK);
        return label;
    }

    /**
     * Makes a table with two columns named Token Type and Token Value and locates it in the scene and sets the
     * ObservableList {@code data} as its data
     * @return
     * returns the table that it made as specified above.
     */
    private TableView initializeTable(){
        TableView table = new TableView();
        table.setEditable(true);
        table.relocate(30, 40);
        table.setPrefSize(500, 550);
        TableColumn column1 = new TableColumn("Token Type");
        column1.setCellValueFactory(new PropertyValueFactory<Token, TokenType>("tokenType"));
        TableColumn column2 = new TableColumn("Token Value");
        column2.setCellValueFactory(new PropertyValueFactory<Token, String>("tokenValue"));
        table.getColumns().addAll(column1, column2);
        table.setItems(data);
        return table;
    }
}
