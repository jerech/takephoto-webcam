/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amta.takephoto;

import java.awt.image.BufferedImage;

/**
 *
 * @author Carlos F Arboleda G
 */
public class Picture {
    private String base64;
    private String pathToSave;
    private String format;
    private int width;
    private int height;

    /**
     * @return the base64
     */
    public String getBase64() {
        return base64;
    }

    
    
    
    

    /**
     * @param base64 the base64 to set
     */
    public void setBase64(String base64) {
        this.base64 = base64;
    }

    /**
     * @return the pathToSave
     */
    public String getPathToSave() {
        return pathToSave;
    }

    /**
     * @param pathToSave the pathToSave to set
     */
    public void setPathToSave(String pathToSave) {
        this.pathToSave = pathToSave;
    }
    
    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
