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
    public TextField real_ft;
    @FXML
    public TextField real_in;
    @FXML
    public TextField real_in_numerator;
    @FXML
    public TextField real_in_denominator;
    @FXML
    public TextField scale;
    @FXML
    public TextField scale_in;
    @FXML
    public TextField scale_mm;
    @FXML
    public TextField real_mm;
    @FXML
    public TextField fields[];
    @FXML
    public Text warn;

    private MeasurementModel model;

    public double parseField(TextField field) {
        String strFt = field.getCharacters().toString();
        System.out.format("strFt = %s", strFt);
        if (strFt.isEmpty()) return 0;
        try {
            return Double.parseDouble(strFt);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public void recalcByRealImperial() {
        double parsedFields[] = new double[4];
        double parsed;
        for (int i = 0; i < 4; ++i) {
            parsed = parseField(fields[i]);
            if (parsed < 0) {
                warn.setText("Invalid input in imperial field #"+i); // TODO: Highlight the field that is wrong
                return;
            }
            parsedFields[i] = parsed;
        }
        warn.setText("");
        
        System.out.format("parsedFields[0] = %f", parsedFields[0]);
        model.updateByRealImperial(parsedFields[0], parsedFields[1],
            parsedFields[2], parsedFields[3]);
        redrawRealMetric();
        redrawScaleImperial();
        redrawScaleMetric();
    }
    
    public void recalcByScale() {
        double parsed = parseField(scale);
        if (parsed < 0) {
            warn.setText("Invalid input in scale field");
            return;
        }
        model.updateByScale(parsed);
        redrawExceptScale();
    }
    
    public void recalcByRealMetric() {
        double parsed = parseField(real_mm);
        if (parsed < 0) {
            warn.setText("Invalid input in real mm field"); 
            return;
        }
        model.updateByRealMetric(parsed);
        redrawRealImp();
        redrawScaleImperial();
        redrawScaleMetric();
    }
    
    public void recalcByScaleImperial() {
        double parsed = parseField(scale_in);
        if (parsed < 0) {
            warn.setText("Invalid input in scale in. field"); 
            return;
        }
        model.updateByScaleImperial(parsed);
        redrawRealImp();
        redrawRealMetric();
        redrawScaleMetric();
    }
    
    public void recalcByScaleMetric() {
        double parsed = parseField(scale_mm);
        if (parsed < 0) {
            warn.setText("Invalid input in scale mm field"); 
            return;
        }
        model.updateByScaleMetric(parsed);
        redrawRealImp();
        redrawRealMetric();
        redrawScaleImperial();
    }
    
    private String renderValue(double v) {
        // Zero values should be omitted
        if (v == 0) {
            return "";
        }
        // Numbers with 1.000 (3 decimal places of zeroes) should be presented as integers.
        long l = Math.round(v*1000);
        if ((l % 1000) == 0) {
            return String.format("%d", l/1000);
        }
        // All others should be represented to 3 decimal places.
        return String.format("%.3f", v);
    }
    
    private void redrawExceptScale() {
        redrawRealImp();
        redrawRealMetric();
        redrawScaleImperial();
        redrawScaleMetric();
    }
    
    private void redrawRealImp() {
        real_ft.setText(renderValue(model.real_ft));
        real_in.setText(renderValue(model.real_in));
        real_in_denominator.setText(renderValue(model.real_in_denominator));
        real_in_numerator.setText(renderValue(model.real_in_numerator));
    }
    private void redrawRealMetric() {
        real_mm.setText(renderValue(model.real_mm));
    }
    private void redrawScaleImperial() {
        scale_in.setText(renderValue(model.scale_in));
    }
    private void redrawScaleMetric() {
        scale_mm.setText(renderValue(model.scale_mm));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new MeasurementModel();
        this.controller = this;
        this.fields = new TextField[8];
        fields[0] = real_ft;
        fields[1] = real_in;
        fields[2] = real_in_numerator;
        fields[3] = real_in_denominator;
        fields[4] = scale;
        fields[5] = real_mm;
        fields[6] = scale_in;
        fields[7] = scale_mm;
    }
}