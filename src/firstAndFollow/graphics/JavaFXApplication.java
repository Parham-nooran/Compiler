package firstAndFollow.graphics;

import firstAndFollow.logic.Main;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Set;


public class JavaFXApplication extends Application {
    private ObservableList<NonTerminal> data = FXCollections.observableArrayList();
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 1200, 560, Color.rgb(240, 240, 240));
        TableView table = new TableView();
        table.setEditable(true);
        table.relocate(30, 40);
        table.setPrefSize(320, 500);
        table.setItems(data);
        TableColumn tableColumn1 = new TableColumn("Non terminal");
        TableColumn tableColumn2 = new TableColumn("First");
        TableColumn tableColumn3 = new TableColumn("Follow");
        tableColumn1.setCellValueFactory(new PropertyValueFactory<NonTerminal, String>("name"));
        tableColumn2.setCellValueFactory(new PropertyValueFactory<NonTerminal, Set<String>>("firsts"));
        tableColumn3.setCellValueFactory(new PropertyValueFactory<NonTerminal, Set<String>>("follows"));
        table.getColumns().addAll(tableColumn1, tableColumn2, tableColumn3);
        addData();
        pane.getChildren().addAll(table);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Lexical Analyser");
        stage.setScene(scene);
        stage.show();
    }
    private void addData(){
        Main main = new Main();
        data.addAll(main.getNonTerminals());
    }
}
