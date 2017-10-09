package com.hikvision.energy.energis.fxtool.AssitTool.controller;

import com.hikvision.energy.energis.fxtool.AssitTool.view.ExportView;
import com.hikvision.energy.energis.fxtool.AssitTool.view.MenuView;
import com.hikvision.energy.energis.fxtool.AssitTool.view.ZoomifyView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by GOT.hodor on 2017/10/9.
 */

@FXMLController
public class MainController {

    @Autowired
    private MenuView menuView;

    @Autowired
    private ExportView exportView;

    @Autowired
    private ZoomifyView zoomifyView;

    @FXML
    private Pane mainPane;

    /**
     * initialize
     */
    @FXML
    public void initialize() {
        loadMenu();
    }

    /**
     * back to menu
     */
    public void back() {
        loadMenu();
    }

    /**
     * load 【menu】
     */
    private void loadMenu() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(menuView.getView());
    }

    /**
     * load 【zoomify】
     */
    public void showZoomifyView() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(zoomifyView.getView());
    }

    /**
     * load 【export sql】
     */
    public void showExportView() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(exportView.getView());
    }


}
