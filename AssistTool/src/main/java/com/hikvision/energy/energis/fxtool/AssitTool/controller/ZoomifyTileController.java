package com.hikvision.energy.energis.fxtool.AssitTool.controller;

import com.hikvision.energy.energis.fxtool.AssitTool.Application;
import com.hikvision.energy.energis.fxtool.AssitTool.service.impl.ZoomifyService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;

@FXMLController
public class ZoomifyTileController {

    private static final Logger log = LoggerFactory.getLogger(ZoomifyTileController.class);

    @Autowired
    private ZoomifyService zoomifyService;

    @Autowired
    private MainController mainController;

    @FXML
    private TextField imgPathTxtField;

    @FXML
    private TextField tilePathTxtField;

    /**
     *
     */
    public void imagePathSetBtn_clickHandler() {
        openImageFileChooser();
    }

    public void tilePathSetBtn_clickHandler() {
        openTileDirectChooser();
    }

    public void backBtn_clickHandler() {
        back();
    }

    /**
     *
     */
    public void processTileBtn_clickHandler() {
        String imgPath = imgPathTxtField.getText();
        String tilePath = tilePathTxtField.getText();

        if (!StringUtils.isEmpty(imgPath) && !StringUtils.isEmpty(tilePath)) {
            zoomifyService.executeTile(imgPath, tilePath);
        }else{
            log.info("img path or folder path is empty");
        }

    }

    /**
     *
     */
    private void openImageFileChooser() {

        FileChooser fileChooser = new FileChooser();
        configureImageFileChooser(fileChooser);

        File file = fileChooser.showOpenDialog(Application.getStage());

        if (file != null) {
            if (imgPathTxtField != null) {
                imgPathTxtField.setText(file.getPath());
            }
        }
    }

    /**
     *
     */
    private void openTileDirectChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        configureTileDirectChooser(directoryChooser);
        File file = directoryChooser.showDialog(Application.getStage());

        if (file != null) {
            if (tilePathTxtField != null) {
                tilePathTxtField.setText(file.getPath());
            }
        }

    }

    /**
     *
     * @param fileChooser
     */
    private void configureImageFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("选择要切片的图片(*.jpg,*.png)");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    /**
     *
     */
    private void configureTileDirectChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("选择切片存放的路径");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    private void back() {
        mainController.back();
    }

}
