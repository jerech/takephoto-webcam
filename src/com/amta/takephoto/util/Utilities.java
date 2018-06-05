/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amta.takephoto.util;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Utilities {
    
    private static File file;

    public static String imgToBase64String(final RenderedImage img, final String formatName) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(img, formatName, os);
        return new String(Base64Coder.encode(os.toByteArray()));
    }
    
    public static BufferedImage base64ToImg(final String base64, final String formatName) throws IOException {
        byte[] bytes = Base64Coder.decode(base64);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        return img;
    }
    
    /** 
     * @author
     * @param bytes Resive bites de una imagen.
     * @param formatName formato de la imagen.
     * @param name nombre del archivo
     * @param route ruta del archivo
     * @param nameType: Constante se debe enviar 1: para que el nombre quede con los milisegundos de la fecha al inicio del nombre
     *                                           2: para que los milisegundos queden al final del nombre
     *                                           3: para que la iamgen quede con el nombre que se le asigna en el parametro
     * @param canChooseFileSave : Si es true el usuario puede seleccionar la ruta donde desea guardar la imagen. sino se usa la ruta que se pasa por default.
     * @return true = si se guardo exitosamente la imagen o false si no se guardo exitosamente la imagen
     * @throws java.io.IOException 
     * 
     */
   
    public static boolean SaveImg(final BufferedImage bytes, String route, String name, final String formatName, final int nameType, final boolean canChooseFileSave) throws IOException{
          
        long currentTimeMillis = System.currentTimeMillis();
        String image_name;
       
        switch (nameType) {
            case Constants.TYPE_NAME_DATE:
                image_name = name + "_" + currentTimeMillis;
            break;
            
            case Constants.TYPE_DATE_NAME:
                image_name = currentTimeMillis + "_" + name;
            break; 
                        
            default:
                image_name = name;
        }
        
        if(canChooseFileSave){           
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(image_name));
            int rVal = chooser.showSaveDialog(chooser);
            if (rVal == JFileChooser.APPROVE_OPTION) {
               image_name = chooser.getSelectedFile().getName();
               route = chooser.getCurrentDirectory().toString();               
            }
            else{
                return false;
            }
        }

        file = new File(route + "/" + image_name + "." + formatName);
        
        boolean response = ImageIO.write(bytes, formatName, file);
               
        if(!response){
            throw new IOException("Ocurrio un error al guardar la imagen");
        }      
        return response;
            // TODO add your handling code here:
    }
    
    public static String getLastFileName(){
        return file.getAbsolutePath();
    }
              
    public static void openFile(final String filename) throws IOException {
        if (filename.length() < 1) {
            throw new IOException("No se ingreso la ruta de la carpeta");
        }

        final boolean browseSupported = Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        if (!browseSupported) {
            throw new IOException("Explorador no soportado");
        }

        final File file = new File(filename);
        if (file.exists()) {
            Desktop.getDesktop().browse(file.toURI());
        } else {
            throw new IOException("Carpeta no existe");
        }
    }
    
    public static void createDirectory(String path, String fileName){

           

            File file  = new File(String.valueOf(fileName));

            File directory = new File(String.valueOf(path));

             if(!directory.exists()){

                directory.mkdir();
                if(!file.exists()){
                    file.getParentFile().mkdir();
                }
            }
          
        }
}
