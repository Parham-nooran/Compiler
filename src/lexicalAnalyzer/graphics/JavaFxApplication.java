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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JavaFxApplication extends Application {
    private ObservableList<Token> data = FXCollections.observableArrayList();
    private ObservableList<TokenType> data2 = FXCollections.observableArrayList();
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 1200, 560, Color.rgb(240, 240, 240));
        //Main.start();
        TableView table = new TableView();
        table.setEditable(true);
        table.relocate(30, 40);
        table.setPrefSize(320, 500);
        TableView table2 = new TableView();

        table2.setItems(data2);
        table2.relocate(390, 40);
        table2.setPrefSize(750, 500);
        try (
                FileReader fileReader = new FileReader(new Main().getFile());
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            makeTable(table2, "IDENTIFIER", "KEYWORD",
                    "SEPARATOR", "OPERATOR", "LITERAL", "COMMENT", "CONSTANT", "ANNOTATION", "UNDEFINED");
            TableColumn column1 = new TableColumn("Token Type");
            column1.setCellValueFactory(new PropertyValueFactory<Token, TokenType>("tokenType"));
            TableColumn column2 = new TableColumn("Token Value");
            column2.setCellValueFactory(new PropertyValueFactory<Token, String>("tokenValue"));
            table.getColumns().addAll(column1, column2);
            fillColumns(bufferedReader);
        } catch (IOException e){
            System.out.println("Something went wrong while initializing the fileReader");
            e.printStackTrace();
        }
        Label label  = new Label("Token");
        label.setLayoutX(50);
        label.setLayoutY(50);
        table.setItems(data);
        pane.getChildren().addAll(table, table2);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Lexical Analyser");
        stage.setScene(scene);
        stage.show();
    }
    private void makeTable(TableView table, String... columns) {
        ArrayList<TableColumn> tableColumns = new ArrayList<>();
        for (int i=0;i<columns.length;i++){
            TableColumn temp = new TableColumn(columns[i]);
            tableColumns.add(temp);
            temp.setCellValueFactory(new PropertyValueFactory<TokenType, String>(columns[i]));
        }
        table.getColumns().addAll(tableColumns);
    }
    private void fillColumns(BufferedReader bufferedReader) throws IOException {
        Token token;
        while ((token = getNextToken(bufferedReader)) != null) {
            data.add(token);
            data2.add(token.getTokenType());
        }
    }

    private Token getNextToken(BufferedReader bufferedReader) throws IOException {
        bufferedReader.mark(1);
        if (bufferedReader.read() == -1) {
            return null;
        }
        bufferedReader.reset();
        return Main.getAnalyzer().getNextToken(bufferedReader);

    }
}
