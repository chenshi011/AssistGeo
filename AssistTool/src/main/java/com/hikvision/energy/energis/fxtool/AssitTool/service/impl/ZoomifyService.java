package com.hikvision.energy.energis.fxtool.AssitTool.service.impl;

import com.hikvision.energy.energis.fxtool.AssitTool.service.IZoomifyService;
import com.hikvision.energy.energis.zoomifycutter.image.ImageCutter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by GOT.hodor on 2017/9/27.
 */
@Service
public class ZoomifyService implements IZoomifyService {

    private static Logger log = LoggerFactory.getLogger(ZoomifyService.class);

    public void executeTile(String imgPath, String folderPath) {
        if (StringUtils.isEmpty(imgPath) || StringUtils.isEmpty(folderPath)) {
            return;
        }

        log.info("check path");

        File file = new File(imgPath);

        if (file != null) {
            try{
                ImageCutter.cutImage(file, folderPath);
            }catch (IOException e) {
                log.error("cut image occured IO exception", e);
            }

        }

    }

}
