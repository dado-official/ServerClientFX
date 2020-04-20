package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ServerSample {

    public static ServerSocket ss;
    public static Socket s;
    public static Thread[] threadReadClient;
    public static int clientsConnected = 0;
    public static TextArea textArea;
    public static ArrayList<Socket> socketArrayList = new ArrayList<>();

    public static final String NEW_CLIENT_MESSAGE = "\nNeuer Client Verbunden: ";

    public TextField port;
    public TextField max_clients;
    public BorderPane borderPane;
    public TextField send;
    public Button startButtonClicked;
    public Button sendFile;
    public Button stopButton;
    public Button sendButton;

    public void buttonSprache() throws IOException {
        startButtonClicked.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Start"));
        sendFile.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "send File"));
        stopButton.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Disconnect"));
        send.setPromptText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Nachricht eingeben"));
        max_clients.setPromptText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Maximale Clients"));
        sendButton.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "senden"));
        if (serverSettingsController.portInt != 0) {
            port.setText(String.valueOf(clientSettingsController.portInt));
        }
        if (serverSettingsController.anzahlClientsInt != 0) {
            max_clients.setText(String.valueOf(serverSettingsController.anzahlClientsInt));
        }
    }

    public void initialize() throws IOException {

        textArea = new TextArea();
        borderPane.setCenter(textArea);

        textArea.setPrefHeight(borderPane.getCenter().getLayoutY());
        textArea.setPrefWidth(borderPane.getCenter().getLayoutX());
        textArea.setEditable(false);

        textArea.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Warte auf Eingabe ... "));
        buttonSprache();
    }

    public void startClickedHandler(ActionEvent actionEvent) throws IOException {
        try{
            if(Integer.parseInt(port.getText()) < 1024 || Integer.parseInt(port.getText()) >  49151 ){
                int tmpportInt = 0;
                while(tmpportInt <= 1023){
                    final JPanel panel = new JPanel();
                    String str = GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Port ungültig");
                    str.replace(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Port"), "Port");
                    JOptionPane.showMessageDialog(panel, str,
                            GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Warning"),
                            JOptionPane.WARNING_MESSAGE);
                    String str2 = GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Bitte Port eingeben");
                    str.replace(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Port"), "Port");
                    tmpportInt = Integer.parseInt(JOptionPane.showInputDialog(str2));
                }
                port.setText(String.valueOf(tmpportInt));
            }
            if (Integer.parseInt(max_clients.getText()) <= 0){
                int tmpMaxClient = 0;
                while(tmpMaxClient <= 0){
                    final JPanel panel = new JPanel();
                    String str = GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "max. Clients ungültig");
                    str.replace(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Clients"), "Clients");
                    JOptionPane.showMessageDialog(panel, str,
                            GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Warning"),
                            JOptionPane.WARNING_MESSAGE);
                    String str2 = GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Bitte max. Clients eingeben");
                    str.replace(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Clients"), "Clients");
                    tmpMaxClient = Integer.parseInt(JOptionPane.showInputDialog(str2));
                }
            }
            threadReadClient = new Thread[Integer.parseInt(max_clients.getText())];
            ss =new ServerSocket(Integer.parseInt(port.getText()));
            textArea.setText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Server eingeschaltet\n"));
            String[] args = new String[1];
            args[0] = max_clients.getText();

            RunnableWaitForClientsToJoin.main(args);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void writeMessage(Socket s, String nachricht) throws Exception {
        PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
        printWriter.println(nachricht);
    }

    public static String readMessage(Socket s) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return  bufferedReader.readLine();
    }


    public void stopClickedHandler(ActionEvent actionEvent) throws IOException {
        for (Thread t: threadReadClient) {
            t.interrupt();
        }
        ss.close();

    }

    public void sendClickedHandler(ActionEvent actionEvent) {
        try{
            textArea.appendText("\nServer: ".concat(GoogleTranslate.translate(
                    serverSettingsController.spracheNachFieldStr, send.getText())));
            for (Socket socket: socketArrayList) {
                String str = "Server: ".concat(send.getText());
                writeMessage(socket, str);
                System.out.println("Sende an: " + socket.toString());
            }
            send.setText("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void dataSendClickedHandler(ActionEvent actionEvent) throws Exception {

        for (Socket socket: socketArrayList) {
            writeMessage(socket, "DOWNLOAD_FILE");
        }

        Path pfad = Paths.get(JOptionPane.showInputDialog(GoogleTranslate.translate
                (serverSettingsController.spracheBeienFieldStr, "Pfad eingeben:")));
        Path fileName = pfad.getFileName();

        for (Socket socket: socketArrayList) {
            writeMessage(socket, String.valueOf(fileName));
        }

        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pfad)));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            String message = String.join("#/noeiariga/" , records);

            for (Socket socket: socketArrayList) {
                writeMessage(socket, message);
            }
            reader.close();
        }
        catch (Exception e) {
            textArea.appendText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Exception beim lesen: ") + pfad);
            e.printStackTrace();
        }
    }

    public void settingsHandler(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("/sample/fxml/serverSettingsSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.serverStage.setScene(scene);
        MenuSample.serverStage.show();
    }
}


class RunnableWaitForClientsToJoin extends ServerSample implements java.lang.Runnable{

    public static String[] stringII;
    public static void main(String[] args) {

        stringII = new String[1];
        stringII[0] = args[0];
        RunnableWaitForClientsToJoin waitForClientsToJoin = new RunnableWaitForClientsToJoin();
        Thread waitThread = new Thread(waitForClientsToJoin);
        waitThread.start();
    }

    @Override
    public void run() {
        System.out.println(textArea.getText());
        for (int i = 0; i < Integer.parseInt(stringII[0]); i++) {
            try {
                s = ss.accept();
                socketArrayList.add(s);
                writeMessage(s, "Erfolgreich mit Server verbunden.\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] stringI = new String[1];
            stringI[0] = Integer.toString(i);
            textArea.appendText(NEW_CLIENT_MESSAGE.concat(String.valueOf(i)));
            RunnableServerListenToClient.main(stringI);
            clientsConnected++;
        }
    }
}

//Auf Nchricht vom Client warten
class RunnableServerListenToClient extends ServerSample implements java.lang.Runnable {
    public static String[] stringI;
    public static void main(String[] args) {
        stringI = new String[1];
        stringI[0] = args[0];
        RunnableServerListenToClient listenToServer = new RunnableServerListenToClient();
        threadReadClient[Integer.parseInt(stringI[0])] = new Thread(listenToServer);
        threadReadClient[Integer.parseInt(stringI[0])].start();
    }

    @Override
    public void run() {
        while (true){
            try {
                String read = readMessage(socketArrayList.get(Integer.parseInt(stringI[0])));

                switch (read){
                    case "DOWNLOAD_FILE":

                        Path fileName = Paths.get(readMessage(socketArrayList.get(Integer.parseInt(stringI[0]))));
                        Path pfad = Paths.get("C:\\Users\\Daniel Nagler\\Google Drive\\TFO 4BT\\Systeme_Netze\\Server\\Client\\ServerClienFX\\src\\sample\\data\\empfangeneDateien", String.valueOf(fileName));
                        System.out.println(pfad);
                        String str = readMessage(socketArrayList.get(Integer.parseInt(stringI[0])));
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

                        for (Socket socket: socketArrayList) {
                            writeMessage(socket, "DOWNLOAD_FILE");
                        }

                        Path pfadSenden = Paths.get(pfad.toString());
                        Path fileNameSenden = pfadSenden.getFileName();

                        for (Socket socket: socketArrayList) {
                            writeMessage(socket, String.valueOf(fileName));
                        }

                        List<String> recordsSenden = new ArrayList<String>();
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pfadSenden)));
                            String lineSenden;
                            while ((lineSenden = reader.readLine()) != null) {
                                recordsSenden.add(lineSenden);
                            }
                            String message = String.join("#/noeiariga/" , recordsSenden);

                            for (Socket socket: socketArrayList) {
                                writeMessage(socket, message);
                            }
                            reader.close();
                        }
                        catch (Exception e) {
                            textArea.appendText(GoogleTranslate.translate(serverSettingsController.spracheBeienFieldStr, "Exception beim lesen: ") + pfadSenden);
                            e.printStackTrace();
                        }


                        break;
                    default:
                        textArea.appendText("\n".concat(GoogleTranslate.translate(serverSettingsController.spracheNachFieldStr, read)));
                        for (Socket socket: socketArrayList) {
                            System.out.println("Server schreibt" + socket);
                            writeMessage(socket, read);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

