package com.hikvision.energy.energis.fxtool.AssitTool.controller;

import com.hikvision.energy.energis.fxtool.AssitTool.Application;
import com.hikvision.energy.energis.fxtool.AssitTool.service.impl.ExportService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/9/27.
 */

@FXMLController
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private ExportService exportService;

    @FXML
    private TextField pgGisVerTxtField;

    @FXML
    private TextField dbNameTxtField;

    @FXML
    private TextField ipTxtField;

    @FXML
    private TextField portTxtField;

    @FXML
    private TextField userTxtField;

    @FXML
    private TextField pwdTxtField;

    @FXML
    private TextField exportPathTxtField;

    @FXML
    private TextField exportFileNameTxtField;

    @FXML
    private Label msgLbl;

    /**
     *
     */
    public void connBtn_clickHandler() {
        connect();
    }

    /**
     *
     */
    public void exportBtn_clickHandler() {
        export();
    }

    /**
     *
     */
    public void disposeBtn_clickHandler() {
        dispose();
    }

    /**
     *
     */
    public void exportPathBtn_clickHandler() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择切片存放的路径");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = directoryChooser.showDialog(Application.getStage());

        if (file != null) {
            if (exportPathTxtField != null) {
                exportPathTxtField.setText(file.getPath());
            }
        }
    }

    /**
     *
     */
    private void connect() {
        Map<String, Object> params = new HashMap();

        params.put( "dbtype", "postgis");
        params.put( "host", ipTxtField.getText());
        params.put( "port", Integer.parseInt(portTxtField.getText()));
        params.put( "schema", "public");
        params.put( "database", dbNameTxtField.getText());
        params.put( "user", userTxtField.getText());
        params.put( "passwd", pwdTxtField.getText());

        /*
        params.put( "dbtype", "postgis");
        params.put( "host", "127.0.0.1");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "gis_db");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");
        */

        boolean connState = exportService.connect(dbNameTxtField.getText(), params);

        if (connState) {
            msgLbl.setText("connect success");
        }else{
            msgLbl.setText("connect failed");
        }
    }

    private void export() {
        try{
            String dbName = dbNameTxtField.getText();
            String path = exportPathTxtField.getText();
            String fileName = exportFileNameTxtField.getText();
            String gisVer = pgGisVerTxtField.getText();
            if (StringUtils.isEmpty(path)) {
                msgLbl.setText("export path not set");
                return;
            }
            exportService.export(dbName, gisVer, path, fileName);
        }catch (IOException e) {
            log.error("export sql data error", e);
        }catch (SQLException e){
            log.error("export sql data error", e);
        }
    }


    private void dispose() {
        String dbName = dbNameTxtField.getText();
        exportService.dispose(dbName);
        msgLbl.setText("disconnect");
    }



}
