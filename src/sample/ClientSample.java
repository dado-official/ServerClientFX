package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public void initialize(){
        textArea = new TextArea();
        borderPane.setCenter(textArea);

        textArea.setPrefHeight(borderPane.getCenter().getLayoutY());
        textArea.setPrefWidth(borderPane.getCenter().getLayoutX());
        textArea.setEditable(false);
    }


    public void connectClickedHandler(ActionEvent actionEvent) {
        try{
            s=new Socket(address.getText(),Integer.parseInt(port.getText()));

            ListenToServerRunnable.main(null);

        }catch(Exception e){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Adresse oder Port ungültig", "Warning",
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
        Path pfad = Paths.get(JOptionPane.showInputDialog("Pfad eingeben:"));
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
            textArea.appendText("Exception beim lesen: " + pfad);
            e.printStackTrace();
        }
    }


    public void settingsHandler(ActionEvent actionEvent) {
        Parent root;
        Scene settingsscene = new Scene(root);
        
               Parent root=FXMLLoader.load(getClass().getResource("/sample/fxml/samplemenu.fxml"));
                       Scene scene=new Scene(root, 800, 500);
    }                  scene.getStylesheets().add(getClass().getResource("/sample/fxml/Pingpong.css").toExternalForm());
}                      root.requestFocus();
                       Main.menuStage2.setScene(scene);
                       Main.menuStage2.show();


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
                                System.out.println("File created: " + file.getName());
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                for (String s: line) {
                                    writer.write(s);
                                    writer.write("\n");
                                }
                                writer.close();
                            } else {
                                System.out.println("File already exists.");
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                for (String s: line) {
                                    writer.write(s);
                                    writer.write("\n");
                                }
                                writer.close();
                            }
                        } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                        break;

                    default:
                        /*
                        System.out.println(read);
                        String sp = "en";
                        String sp2 = sprache.getText();
                        System.out.println(" String = " + sp + " " + Arrays.toString(sp.getBytes()));
                        System.out.println(" Testfe = " + sp2 + " " + Arrays.toString(sp2.getBytes()));
                        textArea.appendText("\n".concat(GoogleTranslate.translate(sp,read))); //wirft NullPointerEx.

                         */
                        textArea.appendText("\n".concat(GoogleTranslate.translate("en",read)));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
