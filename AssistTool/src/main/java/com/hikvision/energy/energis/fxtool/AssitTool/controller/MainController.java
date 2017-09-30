package com.hikvision.energy.energis.fxtool.AssitTool.controller;

import com.hikvision.energy.energis.fxtool.AssitTool.view.ExportView;
import com.hikvision.energy.energis.fxtool.AssitTool.view.ZoomifyView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by GOT.hodor on 2017/9/27.
 */

@FXMLController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private GridPane mainPane;

    @Autowired
    private ZoomifyView zoomifyView;

    @Autowired
    private ExportView exportView;

    /**
     *
     */
    public void zoomifyImgView_clickHandler() {
        log.info("zoomify click");
        showZoomifyView();
    }

    /**
     *
     */
    public void exportImgView_clickHandler() {
        log.info("export click");
        showExportView();
    }

    /**
     *
     */
    public void showZoomifyView() {
        mainPane.getChildren().clear();

        mainPane.getChildren().add(zoomifyView.getView());
    }

    /**
     *
     */
    public void showExportView() {
        mainPane.getChildren().clear();

        mainPane.getChildren().add(exportView.getView());
    }

}
