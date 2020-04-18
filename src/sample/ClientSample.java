package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientSample {
    public static TextArea textArea;
    public BorderPane borderPane;

    public static Socket s;
    public TextField port;
    public TextField send;
    public TextField benutzer;
    public TextField address;
    public TextField sprache;
    public Button connechtButton;
    public Button sendFile;
    public Button disconnectButton;


    public void setPort(TextField port) {
        this.port = port;
    }

    public void setBenutzer(TextField benutzer) {
        this.benutzer = benutzer;
    }

    public void buttonSprache() throws IOException {
        connechtButton.setText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "Connect"));
        sendFile.setText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "send File"));
        disconnectButton.setText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "Disconnect"));
        send.setPromptText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "Nachricht eingeben"));
        benutzer.setPromptText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "Benutzername"));
        if (clientSettingsController.benutzername != null) {
            benutzer.setText(clientSettingsController.benutzername);
        }
        address.setPromptText(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "IP-Address"));
        if (clientSettingsController.ipAdress != null) {
            address.setText(clientSettingsController.ipAdress);
        }
        if (clientSettingsController.portInt != 0) {
            address.setText(String.valueOf(clientSettingsController.portInt));
        }
    }

    public void initialize() throws IOException {
        textArea = new TextArea();
        borderPane.setCenter(textArea);

        textArea.setPrefHeight(borderPane.getCenter().getLayoutY());
        textArea.setPrefWidth(borderPane.getCenter().getLayoutX());
        textArea.setEditable(false);

        if(clientSettingsController.spracheBeienFieldStr != null){
            buttonSprache();
        }
    }


    public void connectClickedHandler(ActionEvent actionEvent) throws IOException {
        try{
            s=new Socket(address.getText(),Integer.parseInt(port.getText()));

            ListenToServerRunnable.main(null);

        }catch(Exception e){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, GoogleTranslate.translate(
                    clientSettingsController.spracheBeienFieldStr, "Adresse oder Port ungültig") ,
                    GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr, "Achtung"),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void writeMessage(Socket s2, String nachricht) throws Exception {
        PrintWriter printWriter = new PrintWriter(s2.getOutputStream(), true);
        printWriter.println(nachricht);
    }

    public static String readMessage(Socket s2) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s2.getInputStream()));
        return bufferedReader.readLine();
    }

    public void sendClickedHandler(ActionEvent actionEvent) {
        try{
            if(benutzer.getText().equals("")){
                String str = "Client: ".concat(send.getText());
                writeMessage(s, str);
            } else {
                String str = benutzer.getText().concat(": ".concat(send.getText()));
                writeMessage(s, str);
            }
            send.setText("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dataSendClickedHandler(ActionEvent actionEvent) throws Exception {
        writeMessage(s, "DOWNLOAD_FILE");
        Path pfad = Paths.get(JOptionPane.showInputDialog(GoogleTranslate.translate
                (clientSettingsController.spracheBeienFieldStr,"Pfad eingeben:")));
        Path fileName = pfad.getFileName();

        writeMessage(s, String.valueOf(fileName));

        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pfad)));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            String message = String.join("#/noeiariga/" , records);
            writeMessage(s, message);
            reader.close();
        }
        catch (Exception e) {
            String str = GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr,
                    "Exception beim lesen: ");
            textArea.appendText(str + pfad);
            e.printStackTrace();
        }
    }


    public void settingsHandler(ActionEvent actionEvent) throws IOException {

        Parent root=FXMLLoader.load(getClass().getResource("/sample/fxml/clientSettingsSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.clientStage.setScene(scene);
        MenuSample.clientStage.show();

    }
}


//Auf Nchricht vom Server warten
class ListenToServerRunnable extends ClientSample implements java.lang.Runnable {
    public static Thread listenToServerThread;
    public static void main(String[] args) {
        ListenToServerRunnable listenToServer = new ListenToServerRunnable();
        listenToServerThread = new Thread(listenToServer);
        listenToServerThread.start();
    }

    @Override
    public void run() {
        while(true){
            try {
                String read = readMessage(s);
                switch (read){
                    case "DOWNLOAD_FILE":

                        Path fileName = Paths.get(readMessage(s));
                        Path pfad = Paths.get("C:\\Users\\Daniel Nagler\\Google Drive\\TFO 4BT\\Systeme_Netze\\Server\\Client\\ServerClienFX\\src\\sample\\data\\empfangeneDateien", String.valueOf(fileName));
                        System.out.println(pfad);
                        String str = readMessage(s);
                        String[] line = str.split("#/noeiariga/");

                        try {
                            File file = new File(String.valueOf(pfad));
                            if (file.createNewFile()) {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                for (String s: line) {
                                    writer.write(s);
                                    writer.write("\n");
                                }
                                writer.close();
                            } else {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                for (String s: line) {
                                    writer.write(s);
                                    writer.write("\n");
                                }
                                writer.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        textArea.appendText("\n".concat(GoogleTranslate.translate(clientSettingsController.spracheBeienFieldStr,read)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
