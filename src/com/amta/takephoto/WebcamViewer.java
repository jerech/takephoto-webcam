/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amta.takephoto;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import com.amta.takephoto.util.Utilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author jeremias
 */
public class WebcamViewer extends JFrame implements  Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener {

	private static final long serialVersionUID = 1L;

	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private WebcamPicker picker = null;
        private BufferedImage image;
        private javax.swing.JButton btnTakePicture = null;
        private javax.swing.JButton btnSavePicture = null;
        //private javax.swing.JButton btnStopPlayCamera = null;
        private boolean cameraOpen=true;
        private boolean frameCreated=false;
        //private static final String PATH = "/home/jeremias/Escritorio/img/";
        
        
        private Builder builder;
        private static WebcamViewer instance;
     
    private WebcamViewer() {
    }

    private WebcamViewer(Builder aThis) {
        this.builder = aThis;
    }

        
        public interface OnCompleteListener {
            void onComplete(Picture picture);
            void onError(Exception error);
            void onClose();
        }

	@Override
	public void run() {

		Webcam.addDiscoveryListener(this);

                frameCreated = true;
		setTitle(builder.title);
             
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		addWindowListener(this);

		picker = new WebcamPicker();
		picker.addItemListener(this);

		webcam = picker.getSelectedWebcam();

		if (webcam == null) {
			System.out.println("No webcams found...");
			System.exit(1);
		}

		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.addWebcamListener(WebcamViewer.this);

                if(panel==null){
                    panel = new WebcamPanel(webcam, false);
                }
		panel.setFPSDisplayed(true);
                
                btnTakePicture = new javax.swing.JButton("Left");
                btnTakePicture.setText("Tomar foto");
                
                btnTakePicture.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnTakePictureActionPerformed(evt);
                    }
                });
                
                btnSavePicture = new javax.swing.JButton("Right");
                btnSavePicture.setText("Guardar");
                
                btnSavePicture.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        
                        btnSaveActionPerformed(evt);
                    }
                });
                
                /*btnStopPlayCamera = new javax.swing.JButton("Left");
                btnStopPlayCamera.setText("Cerrar Camara");
                
                
                btnStopPlayCamera.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if(cameraOpen){
                            cameraOpen=false;
                            webcam.close();
                            btnStopPlayCamera.setText("Abrir Camara");
                        }else{
                            cameraOpen=true;
                            openCamera();
                            btnStopPlayCamera.setText("Cerrar Camara");
                        }
                    }
                });*/
                
                JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
                btnPnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                //btnPnl.add(btnStopPlayCamera);
                btnPnl.add(btnTakePicture);
                btnPnl.add(btnSavePicture);


		add(picker, BorderLayout.NORTH);
                add(btnPnl, BorderLayout.SOUTH);
                //add(btnSavePicture, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);

		pack();
		setVisible(true);

		Thread t = new Thread() {

			@Override
			public void run() {
				panel.start();
			}
		};
		t.setName("example-starter");
		t.setDaemon(true);
		t.setUncaughtExceptionHandler(this);
		t.start();
                
                
	}
        
        private void btnTakePictureActionPerformed(java.awt.event.ActionEvent evt){
            image = webcam.getImage();
            
        }
        
        private void btnSaveActionPerformed(java.awt.event.ActionEvent evt){
            if(image!=null){
                try {
                    
                    Picture picture = new Picture();
                    Dimension dimen = webcam.getViewSize();
                    String base64 = Utilities.imgToBase64String(image, builder.imageFormat);
                    picture.setBase64(base64);
                    picture.setFormat(builder.imageFormat);
                    picture.setWidth((int) dimen.getWidth());
                    picture.setHeight((int) dimen.getHeight());
                    
                    
                System.out.println("Saving Picture..");
                
                
                //picture.setWidth((int) set_width);
                //picture.setHeight((int) set_height);
                    if(builder.onCompleteListener!=null){
                        builder.onCompleteListener.onComplete(picture);
                    }
                    
                    //Hay que cerrar la app?
                    
                    if(builder.closeAfterTakePicture){
                        webcam.close();
                        //btnStopPlayCamera.setText("Abrir Camara");
                        
                        webcam.removeWebcamListener(WebcamViewer.this); // ADD THIS LINE
              
                        panel.stop();
                        setVisible(false);
                        dispose(); 
                        
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(WebcamViewer.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Error al guardar imagen.");
                    
                    if(builder.onCompleteListener!=null){
                        builder.onCompleteListener.onError(ex);
                    }
                }
            }else{
                JOptionPane.showMessageDialog(this, "Antes de guardar debe tomar una foto.");
            }
            
            image = null;
            
            
        }

	public void display(){
            /*if(frameCreated){
                instance.setVisible(true);
                instance.openCamera();
            }else{*/
           
             SwingUtilities.invokeLater(instance);
            //}
            
        }
        
        public static class Builder {
        
            private OnCompleteListener onCompleteListener;
            private String imageFormat = "PNG";
            private boolean closeAfterTakePicture = false;
            private String title = "Java Webcam"; 
            
            
            public Builder setCloseAfterTakePicture(boolean closeAfterTakePicture){
                this.closeAfterTakePicture = closeAfterTakePicture;
                return this;
            }
            
            public Builder setOnCompleteListener(OnCompleteListener onCompleteListener){
                this.onCompleteListener = onCompleteListener;
                return this;
            }
            
            public Builder setImageFormat(String imageFormat){
                this.imageFormat = imageFormat;
                return this;
            }
            
            public Builder setTitle(String title){
                this.title = title;
                return this;
            }
            
            
            
            
            public WebcamViewer build() {
                if(instance==null){
                    instance = new WebcamViewer(this);
                }
                return instance;
            }
        }
        
     
        

	@Override
	public void webcamOpen(WebcamEvent we) {
		System.out.println("webcam open");
	}

	@Override
	public void webcamClosed(WebcamEvent we) {
		System.out.println("webcam closed");
                
                
	}

	@Override
	public void webcamDisposed(WebcamEvent we) {
		System.out.println("webcam disposed");
	}

	@Override
	public void webcamImageObtained(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
            if(webcam.isOpen()){
               webcam.close(); 
            }
		
            //btnStopPlayCamera.setText("Abrir Camara");

            webcam.removeWebcamListener(WebcamViewer.this); // ADD THIS LINE

            panel.stop();
            setVisible(false);
            dispose(); 
	}

	@Override
	public void windowClosing(WindowEvent e) {
            if(builder.onCompleteListener!=null){
                    builder.onCompleteListener.onClose();
             }
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println("webcam viewer resumed");
		panel.resume();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println("webcam viewer paused");
		panel.pause();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println(String.format("Exception in thread %s", t.getName()));
		e.printStackTrace();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != webcam) {
			if (webcam != null) {

				panel.stop();

				remove(panel);

				webcam.removeWebcamListener(this);
				webcam.close();

				webcam = (Webcam) e.getItem();
				webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.addWebcamListener(this);

				System.out.println("selected " + webcam.getName());

				panel = new WebcamPanel(webcam, false);
				panel.setFPSDisplayed(true);

				add(panel, BorderLayout.CENTER);
				pack();

				Thread t = new Thread() {

					@Override
					public void run() {
						panel.start();
					}
				};
				t.setName("example-stoper");
				t.setDaemon(true);
				t.setUncaughtExceptionHandler(this);
				t.start();
			}
		}
	}
        
        public void openCamera(){
            panel.stop();

            remove(panel);

            webcam.removeWebcamListener(this);
            webcam.close();

            //webcam = (Webcam) e.getItem();
            //webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.addWebcamListener(this);

            System.out.println("selected " + webcam.getName());

            panel = new WebcamPanel(webcam, false);
            panel.setFPSDisplayed(true);

            add(panel, BorderLayout.CENTER);
            pack();

            Thread t = new Thread() {

                    @Override
                    public void run() {
                            panel.start();
                    }
            };
            t.setName("example-stoper");
            t.setDaemon(true);
            t.setUncaughtExceptionHandler(this);
            t.start();
        }

	@Override
	public void webcamFound(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.addItem(event.getWebcam());
		}
	}

	@Override
	public void webcamGone(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.removeItem(event.getWebcam());
		}
	}
        
        
        
        
        public void onCompleteListener(OnCompleteListener onCompleteListener) {
            builder.onCompleteListener = onCompleteListener;
        }
}