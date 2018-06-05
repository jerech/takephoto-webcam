/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amta.takephoto;

import com.amta.takephoto.util.Utilities;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author jeremias
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    private static final String PATH = "C:\\Users\\Pictures";
    //private static final String PATH = "/home/jeremias/Escritorio/img/";
    private static final String format = "PNG"; 
    private static final String title = "Java Webcam";
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        WebcamViewer.Builder builder = new WebcamViewer.Builder()
                .setImageFormat(format)
                .setCloseAfterTakePicture(true)
                .setTitle(title)
                .setOnCompleteListener(new WebcamViewer.OnCompleteListener() {
            @Override
            public void onComplete(Picture picture) {
                    System.out.println(picture.getFormat());
                    BufferedImage bytes = null;
                    try {
                        bytes = Utilities.base64ToImg(picture.getBase64(), picture.getFormat());
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        
                        
                    
                        String fileName = PATH+"img_"+format.format(new Date())+"."+picture.getFormat();
                        Utilities.createDirectory(PATH, fileName);
                        ImageIO.write(bytes, picture.getFormat(), new File(fileName));
                        
                        

                    }catch(Exception e){
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                    }

            }

            @Override
            public void onError(Exception error) {
                    System.err.println(error.toString());
                    Logger.getLogger(WebcamViewer.class.getName()).log(Level.SEVERE, null, error);
                    JOptionPane.showMessageDialog(null, "Error taking picture " + error.getMessage(), "Print", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void onClose() {
                
            }
        });
        
        WebcamViewer webcam = builder.build();
        webcam.display();
    }
    
    
    
}
