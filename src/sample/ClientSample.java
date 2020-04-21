package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientSample extends clientSettingsController {

    public static ArrayList<Socket> clientSockets = new ArrayList<>();
    public static ArrayList<TextArea> clientTextArea = new ArrayList<>();
    public int socketInt;
    public int areaInt;
    public BorderPane borderPane;
    public TextField port;
    public TextField send;
    public TextField benutzer;
    public TextField address;
    public Button connechtButton;
    public Button sendFile;
    public Button disconnectButton;
    public Button sendButton;


    public void buttonSprache() throws IOException {
        connechtButton.setText(GoogleTranslate.translate(spracheBeienFieldStr, "Connect"));
        sendFile.setText(GoogleTranslate.translate(spracheBeienFieldStr, "send File"));
        disconnectButton.setText(GoogleTranslate.translate(spracheBeienFieldStr, "Disconnect"));
        send.setPromptText(GoogleTranslate.translate(spracheBeienFieldStr, "Nachricht eingeben"));
        benutzer.setPromptText(GoogleTranslate.translate(spracheBeienFieldStr, "Benutzername"));
        sendButton.setText(GoogleTranslate.translate(spracheBeienFieldStr, "senden"));
        if (benutzername != null) {
            benutzer.setText(benutzername);
        }
        address.setPromptText(GoogleTranslate.translate(spracheBeienFieldStr, "IP-Address"));
        if (ipAdress != null) {
            address.setText(ipAdress);
        }
        if (portInt != 0) {
            port.setText(String.valueOf(portInt));
        }
    }

    public void initialize() throws IOException {
        clientTextArea.add(new TextArea());
        areaInt = clientTextArea.size()-1;
        borderPane.setCenter(clientTextArea.get(areaInt));

        clientTextArea.get(areaInt).setPrefHeight(borderPane.getCenter().getLayoutY());
        clientTextArea.get(areaInt).setPrefWidth(borderPane.getCenter().getLayoutX());
        clientTextArea.get(areaInt).setEditable(false);

        if(spracheBeienFieldStr != null){
            buttonSprache();
        }
    }

    public void connectClickedHandler(ActionEvent actionEvent) throws IOException {
        try{
            clientSockets.add(new Socket(address.getText(),Integer.parseInt(port.getText())));
            socketInt = clientSockets.size()-1;
            ListenToServerRunnable.main(null);

        }catch(Exception e){
            final JPanel panel = new JPanel();
            String str = GoogleTranslate.translate(spracheBeienFieldStr, "Port oder IP ungültig");
            str.replace(GoogleTranslate.translate(spracheBeienFieldStr, "Port"), "Port");
            JOptionPane.showMessageDialog(panel, str,
                    GoogleTranslate.translate(spracheBeienFieldStr, "Warning"),
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
                writeMessage(clientSockets.get(socketInt), str);
                System.out.println("WriteClient");
            } else {
                String str = benutzer.getText().concat(": ".concat(send.getText()));
                writeMessage(clientSockets.get(socketInt), str);
                System.out.println("WriteName");
            }
            send.setText("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dataSendClickedHandler(ActionEvent actionEvent) throws Exception {
        writeMessage(clientSockets.get(socketInt), "DOWNLOAD_FILE");
        Path pfad = Paths.get(JOptionPane.showInputDialog(GoogleTranslate.translate
                (spracheBeienFieldStr,"Pfad eingeben:")));
        Path fileName = pfad.getFileName();

        writeMessage(clientSockets.get(socketInt), String.valueOf(fileName));

        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pfad)));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            String message = String.join("#/noeiariga/" , records);
            writeMessage(clientSockets.get(socketInt), message);
            reader.close();
        }
        catch (Exception e) {
            String str = GoogleTranslate.translate(spracheBeienFieldStr,
                    "Exception beim lesen: ");
            clientTextArea.get(areaInt).appendText(str + pfad);
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
        Socket socket = clientSockets.get(clientSockets.size()-1);
        TextArea textArea = clientTextArea.get(clientTextArea.size()-1);
        while(true){
            try {
                String read = readMessage(socket);
                System.out.println(socket + " bekommen: " + read);
                switch (read){
                    case "DOWNLOAD_FILE":

                        Path fileName = Paths.get(readMessage(socket));
                        Path pfad = Paths.get("C:\\Users\\Daniel Nagler\\Google Drive\\TFO 4BT\\Systeme_Netze\\Server\\Client\\ServerClienFX\\src\\sample\\data\\empfangeneDateien", String.valueOf(fileName));
                        System.out.println(pfad);
                        String str = readMessage(socket);
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
                        if(read.contains(":")){
                            String[] tmpString = read.split(":");
                            textArea.appendText("\n".concat(tmpString[0]));
                            textArea.appendText(": ".concat(GoogleTranslate.translate(spracheNachFieldStr, tmpString[1])));
                        } else {
                            textArea.appendText("\n".concat(GoogleTranslate.translate(spracheNachFieldStr, read)));
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
