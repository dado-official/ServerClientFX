package sample;

import javafx.event.ActionEvent;
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
    public TextField sprache;

    public void initialize() throws IOException {

        textArea = new TextArea();
        borderPane.setCenter(textArea);

        textArea.setPrefHeight(borderPane.getCenter().getLayoutY());
        textArea.setPrefWidth(borderPane.getCenter().getLayoutX());
        textArea.setEditable(false);

        textArea.setText("Warte auf Eingabe ... ");
    }

    public void startClickedHandler(ActionEvent actionEvent) throws IOException {
        try{
            if(Integer.parseInt(port.getText()) < 1024 || Integer.parseInt(port.getText()) >  49151 ){
                int tmpportInt = 0;
                while(tmpportInt <= 1023){
                    final JPanel panel = new JPanel();
                    JOptionPane.showMessageDialog(panel, "Port ungültig", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    tmpportInt = Integer.parseInt(JOptionPane.showInputDialog("Bitte Port eingeben"));
                }
                port.setText(String.valueOf(tmpportInt));
            }
            if (Integer.parseInt(max_clients.getText()) <= 0){
                int tmpMaxClient = 0;
                while(tmpMaxClient <= 0){
                    final JPanel panel = new JPanel();
                    JOptionPane.showMessageDialog(panel, "max. Clients ungültig", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    tmpMaxClient = Integer.parseInt(JOptionPane.showInputDialog("Bitte max. Clients eingeben"));
                }
            }
            threadReadClient = new Thread[Integer.parseInt(max_clients.getText())];
            ss =new ServerSocket(Integer.parseInt(port.getText()));
            textArea.setText("Server ein\n");
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

    public static void writeData(Socket s2, byte[] bytes) throws Exception {
        PrintWriter printWriter = new PrintWriter(s2.getOutputStream(), true);
        printWriter.println(Arrays.toString(bytes));
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
            textArea.appendText("\nServer: ".concat(GoogleTranslate.translate(sprache.getText() + "", send.getText())));
            for (Socket socket: socketArrayList) {
                String str = "Server: ".concat(send.getText());
                writeMessage(socket, str);
                System.out.println(socket.toString());
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
                        textArea.appendText("\n".concat(GoogleTranslate.translate(sprache.getText() +"",read)));
                        //textArea.appendText("\n".concat(GoogleTranslate.translate("en",read)));
                        sendeAnAlle(read);
                        break;
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendeAnAlle(String str) throws Exception {

        for (Socket socket: socketArrayList) {              //funktioniert nicht richtig
            writeMessage(socket, str);
            System.out.println(socket.toString() + str);
        }
    }
}

