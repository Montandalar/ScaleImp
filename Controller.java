package org.imperial2metric;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Controller controller;
    @FXML
    public TextField ft;
    @FXML
    public TextField in;
    @FXML
    public TextField numerator;
    @FXML
    public TextField denominator;
    @FXML
    public TextField scale;
    @FXML
    public Text output;

    public TextField fields[];

    public double converted;
    public long whole;

    public double imperial2mm(double ft, double in, double a, double b) {
        double result;
        double FACTOR = 25.4;
        result = FACTOR*12*ft;
        result += FACTOR*in;
        double fraction = a/b;
        if (Double.isNaN(fraction) || Double.isInfinite(fraction)) {
            return result;
        }
        result += FACTOR*fraction;
        return result;
    }
    
    public double imperial2mm(double[] a) {
        return imperial2mm(a[0], a[1], a[2], a[3]);
    }

    public double parseField(TextField field) {
        String strFt = field.getCharacters().toString();
        if (strFt.isEmpty()) return 0;
        try {
            return Double.parseDouble(strFt);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public void recalc() {
        double parsedFields[] = new double[5];
        double parsed;
        for (int i = 0; i < 5; ++i) {
            parsed = parseField(fields[i]);
            if (parsed < 0) {
                output.setText("Invalid input.");
                System.out.println();
                return;
            }
            parsedFields[i] = parsed;
            System.out.print(String.format("%f, ", parsedFields[i]));
        }
        System.out.println();

        this.converted = imperial2mm(parsedFields) / parsedFields[4];
        this.whole = Math.round(converted);
        output.setText(String.format("= %f / %d mm", converted, whole));
    }
    
    public void toClipboardCommon(String in) {
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents(new StringSelection(in), null);
    }
    
    public void toClipboardExact() {
        toClipboardCommon(String.format("%f", this.converted));
    }
    
    public void toClipboard2DP() {
        toClipboardCommon(String.format("%.2f", this.converted));
    }
    
    public void toClipboardRounded() {
        toClipboardCommon(String.format("%d", this.whole));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.controller = this;
        this.fields = new TextField[5];
        fields[0] = ft;
        fields[1] = in;
        fields[2] = numerator;
        fields[3] = denominator;
        fields[4] = scale;
    }
}