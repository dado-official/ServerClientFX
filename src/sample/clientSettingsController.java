package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.*;

public class clientSettingsController {

    private String spracheBeienFieldStr = "en";
    private String spracheNachFieldStr = null;
    private String benutzername;
    private String ipAdress;
    private int portInt;

    public TextField benutzernameField;
    public TextField ipAdresseField;
    public TextField portField;
    public TextField spracheBedienFeld;
    public TextField spracheNachFeld;

    private String HELP_TEXT = "1) Benutzername: Der Benutzername kann auch auf der Startseite " +
            "eingegeben werden, da wird es aber nach jeder Verbindung gelöscht. " +
            "Wenn Sie es in den Einstellungen festlegen, wird es für alle nächsten Verbindungen gespeichert." +
            "\n2) IP-Adresse: Bei der IP-Adresse gilt das Gleiche wie beim Benutzername." +
            "\n3) Port: Bei der Porteingabe gilt das Gleiche wie bei der IP-Adresseneingabe." +
            "\n4) Sprache der Bedienelemente: In diesem Feld kann man die gewünschte Sprache der Bedienelemente " +
            "eingeben. Die Sprache sollte als ISO-639-1 Code angegeben werden (die Codes kann man auf " +
            "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes) finden" +
            "\n5) Sprache Der Nachrichten: In diesem feld kann man die gewünschte Sprache der Nachrichten eingeben. " +
            "Man sollte sie wie im Punkt 4 erklärt eingeben.";
    private String HELP = "Help";
    private String EINGBEDSPRACHEUNG = "Eingegebene Bedienelementsprache ungültig";
    private String EINGNACHSPRACHEUNG = "Eingegebene Nachrichtensprache ungültig";
    private String WARNING = "Warning";
    private String UNGUELTIG = "ungültig";
    private String PORT_UNGUELTIG = "Port" + UNGUELTIG;

    public MenuItem uebersichtAnzeigen;
    public Label benutzerna;
    public Label ip;
    public Label spracheBe;
    public Button save;
    public Label infoUnten;
    public Label spracheNa;
    public Menu help;

    public void showHelpClicked(ActionEvent actionEvent) throws IOException {
        final JPanel panel = new JPanel();
        String str = GoogleTranslate.translate(spracheBeienFieldStr, HELP_TEXT);

        str.replace(GoogleTranslate.translate(spracheBeienFieldStr, "Port"), "Port");
        str.replace(GoogleTranslate.translate(spracheBeienFieldStr,
                "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes"), "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes");

        JOptionPane.showMessageDialog(panel, str, GoogleTranslate.translate(spracheBeienFieldStr, HELP), JOptionPane.INFORMATION_MESSAGE);
    }

    public void initialize() throws IOException {
        changeLang();
    }

    public void changeLang() throws IOException {
        HELP_TEXT = GoogleTranslate.translate(spracheBeienFieldStr, HELP_TEXT);
        HELP_TEXT.replace(GoogleTranslate.translate(spracheBeienFieldStr, "Port"), "Port");
        HELP_TEXT.replace(GoogleTranslate.translate(spracheBeienFieldStr,
                "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes"), "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes");
        HELP = GoogleTranslate.translate(spracheBeienFieldStr, HELP);
        EINGBEDSPRACHEUNG = GoogleTranslate.translate(spracheBeienFieldStr, EINGBEDSPRACHEUNG);
        EINGNACHSPRACHEUNG = GoogleTranslate.translate(spracheBeienFieldStr, EINGNACHSPRACHEUNG);
        WARNING = GoogleTranslate.translate(spracheBeienFieldStr, WARNING);
        UNGUELTIG = GoogleTranslate.translate(spracheBeienFieldStr, UNGUELTIG);

        help.setText(GoogleTranslate.translate(spracheBeienFieldStr, help.getText()));
        uebersichtAnzeigen.setText(GoogleTranslate.translate(spracheBeienFieldStr, uebersichtAnzeigen.getText()));
        benutzerna.setText(GoogleTranslate.translate(spracheBeienFieldStr, benutzerna.getText()));
        ip.setText(GoogleTranslate.translate(spracheBeienFieldStr, ip.getText()));
        ip.setText(ip.getText().replace(GoogleTranslate.translate(spracheBeienFieldStr, "IP"), "IP"));
        spracheBe.setText(GoogleTranslate.translate(spracheBeienFieldStr, spracheBe.getText()));
        spracheBe.setText(spracheBe.getText().replace(GoogleTranslate.translate(spracheBeienFieldStr, "ISO 639-1 Code"),
                "ISO 639-1 code"));
        spracheNa.setText(GoogleTranslate.translate(spracheBeienFieldStr, spracheNa.getText()));
        spracheNa.setText(spracheNa.getText().replace(GoogleTranslate.translate(spracheBeienFieldStr, "ISO 639-1 Code"),
                "ISO 639-1 code"));
        spracheNachFeld.setPromptText(GoogleTranslate.translate(spracheBeienFieldStr, spracheNachFeld.getPromptText()));
        spracheBedienFeld.setPromptText(GoogleTranslate.translate(spracheBeienFieldStr, spracheBedienFeld.getPromptText()));
        save.setText(GoogleTranslate.translate(spracheBeienFieldStr, save.getText()));
        infoUnten.setText(GoogleTranslate.translate(spracheBeienFieldStr, infoUnten.getText()));
        infoUnten.setText(infoUnten.getText().replace(GoogleTranslate.translate(spracheBeienFieldStr, "null"), "null"));
    }

    public void einstellungenSpeichernClicked(ActionEvent actionEvent) throws IOException {
        benutzername = benutzernameField.getText();
        ipAdress = ipAdresseField.getText();

        File f = new File("src/sample/data/iso639.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String str = br.readLine();
        if(!str.contains(spracheBedienFeld.getText())){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, EINGBEDSPRACHEUNG,
                    WARNING, JOptionPane.WARNING_MESSAGE);
        } else {
            spracheBeienFieldStr = spracheBedienFeld.getText();
            changeLang();
        }

        if(!str.contains(spracheNachFeld.getText())) {
            if(spracheNachFeld.getText().equals("null")){
                //null speichern
            } else {
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, EINGNACHSPRACHEUNG,
                        WARNING, JOptionPane.WARNING_MESSAGE);
            }
        } else {
            spracheNachFieldStr = spracheNachFeld.getText();
        }

        if(!portField.getText().equals("")){
            if(Integer.parseInt(portField.getText()) < 1024 || Integer.parseInt(portField.getText()) >  49151 ){
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, PORT_UNGUELTIG,
                        WARNING, JOptionPane.WARNING_MESSAGE);
            } else {
                portInt = Integer.parseInt(portField.getText());
            }
        }
        String filename = benutzername + "Settings.txt";
        File logFile = new File("src/sample/data/ClientSettings/" + filename);
        BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
        bw.write("benutzername:" + benutzername + "\n");
        bw.write("ip:" + ipAdress + "\n");
        bw.write("port:" + portInt + "\n");
        bw.write("besprache:" + spracheBeienFieldStr + "\n");
        bw.write("nasprache:" + spracheNachFieldStr + "\n");
        bw.close();
    }


    public void zuruckClicked(ActionEvent actionEvent) throws IOException {
        MenuSample.benuUebergabe = benutzername;
        Parent root= FXMLLoader.load(getClass().getResource("/sample/fxml/ClientSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.clientStage.setScene(scene);
        MenuSample.clientStage.show();
    }
}
