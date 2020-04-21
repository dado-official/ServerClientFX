package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.*;

public class serverSettingsController {

    public String spracheBeienFieldStr;
    public String spracheNachFieldStr;
    public int anzahlClientsInt;
    public int portInt;

    public TextField portField;
    public TextField spracheBedienFeld;
    public TextField spracheNachFeld;
    public TextField anzahlClients;

    public void showHelpClicked(ActionEvent actionEvent) {
        final JPanel panel = new JPanel();
        JOptionPane.showMessageDialog(panel, "1) Benutzername: Der Benutzername kann auch auf der Startseite " +
                        "eingegeben werden, da wird es aber nach jeder Verbindung gelöscht. " +
                        "Wenn Sie es in den Einstellungen festlegen, wird es für alle nächsten Verbindungen gespeichert." +
                        "\n2) IP-Adresse: Bei der IP-Adresse gilt das Gleiche wie beim Benutzername." +
                        "\n3) Port: Bei der Porteingabe gilt das Gleiche wie bei der IP-Adresseneingabe." +
                        "\n4) Sprache der Bedienelemente: In diesem Feld kann man die gewünschte Sprache der Bedienelemente " +
                        "eingeben. Die Sprache sollte als ISO-639-1 Code angegeben werden (die Codes kann man auf " +
                        "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes) finden",
                "Hilfe", JOptionPane.INFORMATION_MESSAGE);
    }

    public void einstellungenSpeichernClicked(ActionEvent actionEvent) throws IOException {
        if(!anzahlClients.getText().equals("")){
            if(Integer.parseInt(anzahlClients.getText()) <= 0 || Integer.parseInt(anzahlClients.getText()) > 50){
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Eingegebene maximale Anzahl der Clients ungültig",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                anzahlClientsInt = Integer.parseInt(anzahlClients.getText());
            }
        }

        File f = new File("src/sample/data/iso639.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String str = br.readLine();
        if(!str.contains(spracheBedienFeld.getText())){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Eingegebene Bedienelementsprache ungültig",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            spracheBeienFieldStr = spracheBedienFeld.getText();
        }

        if(!str.contains(spracheNachFeld.getText())) {
            if(spracheNachFeld.getText().equals("null")){
                //null speichern
            } else {
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Eingegebene Nachrichtensprache ungültig",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            spracheNachFieldStr = spracheNachFeld.getText();
        }

        if(!portField.getText().equals("")){
            if(Integer.parseInt(portField.getText()) < 1024 || Integer.parseInt(portField.getText()) >  49151 ){
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Port ungültig",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                portInt = Integer.parseInt(portField.getText());
            }
        }
    }


    public void zuruckClicked(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("/sample/fxml/ServerSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.serverStage.setScene(scene);
        MenuSample.serverStage.show();
    }
}
