package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    public static ArrayList<Stage> ServerList = new ArrayList<>();
    public static ArrayList<Stage> ClientList = new ArrayList<>();

    @Override
    public void start(Stage stageMenu) throws Exception{
        System.out.println(stageMenu.toString());
        Parent rootMenu = FXMLLoader.load(getClass().getResource("fxml/MenuSample.fxml"));
        stageMenu.setTitle("Menu");
        stageMenu.setResizable(false);
        rootMenu.requestFocus();
        stageMenu.setScene(new Scene(rootMenu, 600, 400));
        stageMenu.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
