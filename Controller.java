package org.imperial2metric;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.Event;
import javafx.event.ActionEvent;

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
    @FXML
    public ToggleGroup sourceUnit;
    @FXML
    public RadioButton src_realft;
    @FXML
    public RadioButton src_realmm;
    @FXML
    public RadioButton src_scalein;
    @FXML
    public RadioButton src_scalemm;

    private MeasurementModel model;

    public void setSourceUnit(Event ev) {
        RadioButton source = (RadioButton) sourceUnit.getSelectedToggle();

        for (int i = 0; i < 8; ++i) {
            fields[i].setEditable(false);
        }
        scale.setEditable(true);

        if (source == src_realft) {
            System.out.println("Source unit set: realft");
            real_ft.setEditable(true);
            real_in.setEditable(true);
            real_in_numerator.setEditable(true);
            real_in_denominator.setEditable(true);
        } else if (source == src_realmm) {
            System.out.println("Source unit set: realmm");
            real_mm.setEditable(true);
        } else if (source == src_scalein) {
            System.out.println("Source unit set: scalein");
            scale_in.setEditable(true);
        } else if (source == src_scalemm) {
            System.out.println("Source unit set: scalemm");
            scale_mm.setEditable(true);
        } else {
            assert(false);
        }
    }

    public void radioButtonKeyPress(KeyEvent ke) {
        setSourceUnit(ke);
        KeyCode keyCode = ke.getCode();
        int increment = 0;

        if (!keyCode.isArrowKey()) {
            return;
        }

        System.out.println(keyCode);
        SourceUnit newUnit = SourceUnit.REAL_IMPERIAL;
        RadioButton source = (RadioButton) sourceUnit.getSelectedToggle();
        if (source == src_realft) {
            newUnit = SourceUnit.REAL_IMPERIAL;
        } else if (source == src_realmm) {
            newUnit = SourceUnit.REAL_METRIC;
        } else if (source == src_scalein) {
            newUnit = SourceUnit.SCALE_IMPERIAL;
        } else if (source == src_scalemm) {
            newUnit = SourceUnit.SCALE_METRIC;
        } else {
            assert(false);
        }

        if (keyCode == KeyCode.DOWN || keyCode == KeyCode.RIGHT) {
            newUnit = newUnit.next();
        } else if (keyCode == KeyCode.UP || keyCode == KeyCode.LEFT) {
            newUnit = newUnit.prev();
        }

        if (newUnit == SourceUnit.REAL_IMPERIAL) {
            src_realft.fire();
        } else if (newUnit == SourceUnit.REAL_METRIC) {
            src_realmm.fire();
        } else if (newUnit == SourceUnit.SCALE_IMPERIAL) {
            src_scalein.fire();
        } else if (newUnit == SourceUnit.SCALE_METRIC) {
            src_scalemm.fire();
        }
        // Do not consume! The onAction event of setSourceUnit should fire.
        //ke.consume();
    }

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
        if (!src_realft.isSelected()) {
            return;
        }
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
        Toggle source = sourceUnit.getSelectedToggle();
        SourceUnit unit;
        if (source == src_realft) {
            unit = SourceUnit.REAL_IMPERIAL;
        } else if (source == src_realmm) {
            unit = SourceUnit.REAL_METRIC;
        } else if (source == src_scalein) {
            unit = SourceUnit.SCALE_IMPERIAL;
        } else if (source == src_scalemm) {
            unit = SourceUnit.SCALE_METRIC;
        } else {
            unit = SourceUnit.INVAL;
            assert(false);
        }
        System.out.format("Update by scale: Source unit: %s\n", unit);
        model.updateByScale(parsed, unit);
        redrawExceptScale();
    }
    
    public void recalcByRealMetric() {
        if (!src_realmm.isSelected()) {
            return;
        }
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
        if (!src_scalein.isSelected()) {
            return;
        }
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
        if (!src_scalemm.isSelected()) {
            return;
        }
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
        src_realft.fire();
    }
}