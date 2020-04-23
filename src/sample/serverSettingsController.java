package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.*;

public class serverSettingsController {

    private String spracheBeienFieldStr = "en";
    private String spracheNachFieldStr = null;
    private int anzahlClientsInt;
    private int portInt;

    private String HELP_TEXT = "1) Maximale Anzahl an Clients: In diesem Feld gibt man die maximale " +
            "Anzahl der Cleints sie sich mit dem Server verbinden können." +
            "\n2)Port: In diesem TextFeld gibt man den gewünsten Port ein den das Programm verwenden soll." +
            "\n3) Sprache der Bedienelemente: In diesem Feld kann man die gewünschte Sprache der Bedienelemente " +
            "eingeben. Die Sprache sollte als ISO-639-1 Code angegeben werden (die Codes kann man auf " +
            "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes) finden" +
            "\n5) Sprache Der Nachrichten: In diesem feld kann man die gewünschte Sprache der Nachrichten eingeben. " +
            "Man sollte sie wie im Punkt 4 erklärt eingeben.";
    private String HELP = "Help";
    private String EINGMAXCLIENTS = "Eingegebene maximale Anzahl der Clients ungültig";
    private String EINGNACHSPRACHEUNG = "Eingegebene Nachrichtensprache ungültig";
    private String EINGBEDSPRACHEUNG = "Eingegebene Bediensprache ungültig";
    private String WARNING = "Warning";
    private String UNGUELTIG = "ungültig";
    private String PORT_UNGUELTIG = "Port" + UNGUELTIG;

    public TextField portField;
    public TextField spracheBedienFeld;
    public TextField spracheNachFeld;
    public TextField anzahlClients;
    public Menu help;
    public MenuItem uebersichtAnzeigen;
    public Label anzClientsLabel;
    public Label spracheBe;
    public Label spracheNa;
    public Button save;
    public Label infoUnten;

    //Hilfefenster wird angezeigt
    public void showHelpClicked(ActionEvent actionEvent) {
        final JPanel panel = new JPanel();
        JOptionPane.showMessageDialog(panel, HELP_TEXT,
                HELP, JOptionPane.INFORMATION_MESSAGE);
    }

    //Falls schon einstellungen gespeichert sind werden sie importiert
    public void initialize() throws IOException {
        File log = new File("src/sample/data/ServerSettings/serverSettings.txt");
        if(log.length() != 0){
            BufferedReader br = new BufferedReader(new FileReader(log));
            String[] lines = new String[4];
            String[] data = new String[4];
            for (int i = 0; i < 4; i++) {
                lines[i] = br.readLine();
                String[] tmpdata = lines[i].split(":");
                data[i] = tmpdata[1];
            }
            br.close();
            anzahlClients.setText(data[0]);
            portField.setText(data[1]);
            spracheBedienFeld.setText(data[2]);
            spracheNachFeld.setText(data[3]);
            spracheBeienFieldStr = data[2];
        }
        //Methode um die Sprache zu wechseln wird aufgerufen
        changeLang();
    }

    //Die Sprache aller Elemente wird getauscht
    public void changeLang() throws IOException {
        HELP_TEXT = GoogleTranslate.translate(spracheBeienFieldStr, HELP_TEXT);
        HELP_TEXT.replace(GoogleTranslate.translate(spracheBeienFieldStr, "Port"), "Port");
        HELP_TEXT.replace(GoogleTranslate.translate(spracheBeienFieldStr,
                "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes"),
                "https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes");
        HELP = GoogleTranslate.translate(spracheBeienFieldStr, HELP);
        EINGMAXCLIENTS = GoogleTranslate.translate(spracheBeienFieldStr, EINGMAXCLIENTS);
        EINGBEDSPRACHEUNG = GoogleTranslate.translate(spracheBeienFieldStr, EINGBEDSPRACHEUNG);
        EINGNACHSPRACHEUNG = GoogleTranslate.translate(spracheBeienFieldStr, EINGNACHSPRACHEUNG);
        WARNING = GoogleTranslate.translate(spracheBeienFieldStr, WARNING);
        UNGUELTIG = GoogleTranslate.translate(spracheBeienFieldStr, UNGUELTIG);

        help.setText(GoogleTranslate.translate(spracheBeienFieldStr, help.getText()));
        uebersichtAnzeigen.setText(GoogleTranslate.translate(spracheBeienFieldStr, uebersichtAnzeigen.getText()));
        anzClientsLabel.setText(GoogleTranslate.translate(spracheBeienFieldStr, anzClientsLabel.getText()));
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

    //Einstellungn werden in einer .txt Datei gespeichert
    public void einstellungenSpeichernClicked(ActionEvent actionEvent) throws IOException {
        if(!anzahlClients.getText().equals("")){
            if(Integer.parseInt(anzahlClients.getText()) <= 0 || Integer.parseInt(anzahlClients.getText()) > 50){
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, EINGMAXCLIENTS,
                        WARNING, JOptionPane.WARNING_MESSAGE);
            } else {
                anzahlClientsInt = Integer.parseInt(anzahlClients.getText());
            }
        }

        File f = new File("src/sample/data/iso639.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String str = br.readLine();
        if(!str.contains(spracheBedienFeld.getText())){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, EINGBEDSPRACHEUNG,
                    WARNING, JOptionPane.WARNING_MESSAGE);
        } else {
            setSpracheBeienFieldStr(spracheBedienFeld.getText());
            spracheBedienFeld.setText(spracheBeienFieldStr);
            System.out.println(spracheBeienFieldStr);
            changeLang();
        }

        if(!str.contains(spracheNachFeld.getText())) {
            if(spracheNachFeld.getText().equals("null")){
                spracheNachFieldStr = null;
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

        File logFile = new File("src/sample/data/ServerSettings/serverSettings.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
        bw.write("anzClient:" + anzahlClientsInt + "\n");
        bw.write("port:" + portInt + "\n");
        bw.write("besprache:" + spracheBeienFieldStr + "\n");
        bw.write("nasprache:" + spracheNachFieldStr + "\n");
        bw.close();
    }

    //Einstellungen verlassen
    public void zuruckClicked(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("/sample/fxml/ServerSample.fxml"));
        Scene scene=new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/fxml/Style.css").toExternalForm());
        root.requestFocus();
        MenuSample.serverStage.setScene(scene);
        MenuSample.serverStage.show();
    }

    public void setSpracheBeienFieldStr(String spracheBeienFieldStr) {
        this.spracheBeienFieldStr = spracheBeienFieldStr;
    }
}
