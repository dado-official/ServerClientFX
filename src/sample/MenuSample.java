package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MenuSample {
    public void OpenServer(javafx.event.ActionEvent actionEvent) throws Exception {
        Stage serverStage = new Stage();
        System.out.println(serverStage.toString());
        Parent rootServer = FXMLLoader.load(getClass().getResource("fxml/ServerSample.fxml"));
        serverStage.setScene(new Scene(rootServer, 600, 400));
        serverStage.setTitle("Server");
        serverStage.setResizable(false);
        rootServer.requestFocus();
        serverStage.show();

        Main.ServerList.add(serverStage);
    }

    public void openClient(ActionEvent actionEvent) throws Exception {
        Stage serverStage = new Stage();
        Parent rootServer = FXMLLoader.load(getClass().getResource("fxml/ClientSample.fxml"));
        serverStage.setScene(new Scene(rootServer, 600, 400));
        serverStage.setTitle("Client");
        serverStage.setResizable(false);
        rootServer.requestFocus();
        serverStage.show();

        Main.ServerList.add(serverStage);
    }
}
