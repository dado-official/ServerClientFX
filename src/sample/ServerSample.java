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
import java.util.*;

public class ServerSample{

    public static ServerSocket ss;
    public static Socket s;
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

    public String bedienSprache = "en";
    public String nachSprache;


    //Diese Methode stellt die Sprache der Buttons und der TexFelder ein
    public void buttonSprache() throws IOException {
        System.out.println("joooo " + bedienSprache);

        startButtonClicked.setText(GoogleTranslate.translate(bedienSprache, "Start"));
        sendFile.setText(GoogleTranslate.translate(bedienSprache, "send File"));
        stopButton.setText(GoogleTranslate.translate(bedienSprache, "Disconnect"));
        send.setPromptText(GoogleTranslate.translate(bedienSprache, "Nachricht eingeben"));
        max_clients.setPromptText(GoogleTranslate.translate(bedienSprache, "Maximal Clients"));
        sendButton.setText(GoogleTranslate.translate(bedienSprache, "send"));
    }

    //Diese Methode initialisiert den Server, wenn schon einstellungen gamacht wurden werden sie importiert
    public void initialize() throws IOException {


        File log = new File("src/sample/data/ServerSettings/serverSettings.txt");
        if(log.length() != 0){
            //Einstellungen importieren
            BufferedReader br = new BufferedReader(new FileReader(log));
            String[] lines = new String[4];
            String[] data = new String[4];
            for (int i = 0; i < 4; i++) {
                lines[i] = br.readLine();
                String[] tmpdata = lines[i].split(":");
                data[i] = tmpdata[1];
            }
            br.close();
            max_clients.setText(data[0]);
            port.setText(data[1]);
            bedienSprache = data[2];
            nachSprache = data[3];
        }


        textArea = new TextArea();
        borderPane.setCenter(textArea);

        textArea.setPrefHeight(borderPane.getCenter().getLayoutY());
        textArea.setPrefWidth(borderPane.getCenter().getLayoutX());
        textArea.setEditable(false);

        textArea.setText(GoogleTranslate.translate(bedienSprache, "Warte auf Eingabe ... "));
        buttonSprache();
    }

    //Eingaben überprüfen und dann Server Starten
    public void startClickedHandler(ActionEvent actionEvent) throws IOException {
        try{
            if(Integer.parseInt(port.getText()) < 1024 || Integer.parseInt(port.getText()) >  49151 ){
                int tmpportInt = 0;
                while(tmpportInt <= 1023){
                    final JPanel panel = new JPanel();
                    String str = GoogleTranslate.translate(bedienSprache, "Port ungültig");
                    str.replace(GoogleTranslate.translate(bedienSprache, "Port"), "Port");
                    JOptionPane.showMessageDialog(panel, str,
                            GoogleTranslate.translate(bedienSprache, "Warning"),
                            JOptionPane.WARNING_MESSAGE);
                    String str2 = GoogleTranslate.translate(bedienSprache, "Bitte Port eingeben");
                    str.replace(GoogleTranslate.translate(bedienSprache, "Port"), "Port");
                    tmpportInt = Integer.parseInt(JOptionPane.showInputDialog(str2));
                }
                port.setText(String.valueOf(tmpportInt));
            }
            if (Integer.parseInt(max_clients.getText()) <= 0){
                int tmpMaxClient = 0;
                while(tmpMaxClient <= 0){
                    final JPanel panel = new JPanel();
                    String str = GoogleTranslate.translate(bedienSprache, "max. Clients ungültig");
                    str.replace(GoogleTranslate.translate(bedienSprache, "Clients"), "Clients");
                    JOptionPane.showMessageDialog(panel, str,
                            GoogleTranslate.translate(bedienSprache, "Warning"),
                            JOptionPane.WARNING_MESSAGE);
                    String str2 = GoogleTranslate.translate(bedienSprache, "Bitte max. Clients eingeben");
                    str.replace(GoogleTranslate.translate(bedienSprache, "Clients"), "Clients");
                    tmpMaxClient = Integer.parseInt(JOptionPane.showInputDialog(str2));
                }
            }
            ss =new ServerSocket(Integer.parseInt(port.getText()));
            textArea.setText(GoogleTranslate.translate(bedienSprache, "Server eingeschaltet\n"));
            String[] args = new String[1];
            args[0] = max_clients.getText();

            RunnableWaitForClientsToJoin.main(args);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //Nachricht senden
    public static void writeMessage(Socket s, String nachricht) throws Exception {
        PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
        printWriter.println(nachricht);
    }

    //Auf Nachricht warten und dann lesen
    public static String readMessage(Socket s) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return  bufferedReader.readLine();
    }


    //Methode um Server zu stoppen (noch nicht fertig)
    public void stopClickedHandler(ActionEvent actionEvent) throws IOException {
        ss.close();
    }

    //Methode um eine Nachricht die im TextFelt eingegeben wurde zu senden
    public void sendClickedHandler(ActionEvent actionEvent) {
        try{
            textArea.appendText("\nServer: ".concat(GoogleTranslate.translate(
                    nachSprache, send.getText())));
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


    //Methode um eine Datei zu senden
    public void dataSendClickedHandler(ActionEvent actionEvent) throws Exception {

        for (Socket socket: socketArrayList) {
            writeMessage(socket, "DOWNLOAD_FILE");
        }

        Path pfad = Paths.get(JOptionPane.showInputDialog(GoogleTranslate.translate
                (nachSprache, "Pfad eingeben:")));
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
            textArea.appendText(GoogleTranslate.translate(bedienSprache, "Exception beim lesen: ") + pfad);
            e.printStackTrace();
        }
    }

    //Ruft das Einstellungsfenster auf
    public void settingsHandler(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("/sample/fxml/serverSettingsSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.serverStage.setScene(scene);
        MenuSample.serverStage.show();
    }
}

//Dieser Thread wartet darauf, dass sich neue Clients Verbinden
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
            try {
                textArea.appendText(GoogleTranslate.translate(bedienSprache, "\n".concat(NEW_CLIENT_MESSAGE))
                        .concat(String.valueOf(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            RunnableServerListenToClient.main(stringI);
            clientsConnected++;
        }
    }
}

//Auf Nchricht vom Client warten und sie dann an allen weiterschicken
class RunnableServerListenToClient extends ServerSample implements java.lang.Runnable {

    public static void main(String[] args) {

        RunnableServerListenToClient listenToServer = new RunnableServerListenToClient();
        Thread threadReadClient = new Thread(listenToServer);
        threadReadClient.start();
    }

    @Override
    public void run() {
        Socket socket = socketArrayList.get(socketArrayList.size()-1);
        while (true){
            try {
                String read = readMessage(socket);

                switch (read){
                    //datei empfangen und weiterschicken
                    case "DOWNLOAD_FILE":
                        Path fileName = Paths.get(readMessage(socket));
                        Path pfad = Paths.get("src/sample/data/ServerSettings/" + fileName);
                        System.out.println(pfad);
                        String str = readMessage(socket);
                        String[] line = str.split("#/noeiariga/");

                        try {
                            File file = new File(String.valueOf(pfad));

                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                            for (String s: line) {
                                writer.write(s);
                                writer.write("\n");
                            }
                            writer.close();

                        } catch (IOException e) {
                            textArea.appendText(GoogleTranslate.translate(bedienSprache, "Fehler beim Empfangen der Datei."));
                        }

                        for (Socket so: socketArrayList) {
                            writeMessage(so, "DOWNLOAD_FILE");
                        }
                        Path pfadSenden = Paths.get(pfad.toString());
                        for (Socket so: socketArrayList) {
                            writeMessage(so, String.valueOf(fileName));
                        }

                        List<String> recordsSenden = new ArrayList<String>();
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(pfadSenden)));
                            String lineSenden;
                            while ((lineSenden = reader.readLine()) != null) {
                                recordsSenden.add(lineSenden);
                            }
                            String message = String.join("#/noeiariga/" , recordsSenden);

                            for (Socket so: socketArrayList) {
                                writeMessage(so, message);
                            }
                            reader.close();
                            textArea.appendText("\n" + String.valueOf(pfadSenden.getFileName()));
                            textArea.appendText(GoogleTranslate.translate(bedienSprache, " wurde gesendet"));
                        }
                        catch (Exception e) {
                            textArea.appendText("\n" + GoogleTranslate.translate(bedienSprache, "Exception beim lesen: ") + pfadSenden);
                            e.printStackTrace();
                        }


                        break;
                    default:
                        //Normale nachricht empfangen und an allen weiterschicken
                        if(read.contains(":")){
                            String[] tmpString = read.split(":");
                            textArea.appendText("\n".concat(tmpString[0]));
                            textArea.appendText(": ".concat(GoogleTranslate.translate(nachSprache, tmpString[1])));
                        } else {
                            textArea.appendText("\n".concat(GoogleTranslate.translate(nachSprache, read)));
                        }
                        for (Socket so: socketArrayList) {
                            System.out.println("Server schreibt" + so);
                            writeMessage(so, read);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

