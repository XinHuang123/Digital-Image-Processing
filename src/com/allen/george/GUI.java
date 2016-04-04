package com.allen.george;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.allen.george.enhancement.ImageEnhancer;
import com.allen.george.enhancement.ImageEnhancerUtils;

public class GUI extends JFrame implements ActionListener {
	
	
	private static final long serialVersionUID = 1L;	
	
	//Action performered event handler
	//When the user clicks a button on the GUI different events can be fired
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("Exit")) { //EXIT BUTTON
			System.exit(0);
		} else if (e.getActionCommand().equals("Load Image")) { //LOAD IMAGE BUTTON
			loadImage();			
		} else if (e.getActionCommand().equals("Help")) { // HELP BUTTON
			showHelp();
		} else if (e.getActionCommand().equals("Spatial Domain")) { //SPATIAL DOMAIN BUTTON
			enhanceImageSpatial();
		} else if (e.getActionCommand().equals("Frequency Domain")) { //FREQUENCY DOMAIN BUTTON
			enhanceImageFrequency();
		} else if (e.getActionCommand().equals("Add Colour")) { //ADD COLOUR BUTTON
			addColor();
		}else if (e.getActionCommand().equals("Clear All")) { //CLEAR ALL BUTTON
			clear();
		} else if (e.getActionCommand().equals("Clear Result")) { //CLEAR RESULT BUTTON
			clearResult();
		}else if (e.getActionCommand().equals("Sharpen")) { //SHARPEN BUTTON
			sharpen();
		}else if (e.getActionCommand().equals("About")) { //ABOUT BUTTON
			showAbout();
		}
		
	}
	
	//Loads an image from a JFileChooser
	//Uses this image to create and object of the ImageEnhancer class
	private void loadImage(){
		JFileChooser chooser = new JFileChooser(); //the JFileChooser
		chooser.setCurrentDirectory(new java.io.File(".")); //Set to look in the project root directory
		chooser.setDialogTitle("Load and Image");		//Set title of the File Chooser
		chooser.setAcceptAllFileFilterUsed(false); 
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {		//When the user clicks the load/open button			
		  image = new ImageEnhancer(chooser.getSelectedFile().getAbsolutePath()); //Create the image enhancer with the image
		  originalImageLabel.setIcon(image.getImageIconForJLabel());		//draw the image in the label
		  originalImageLabel.setText(""); //remove the text from the label
		} 
	}
	
	//Show the help text in a JOptionPane
	private void showHelp(){
		String helpText = "To load an image please press 'File' then 'Load'.\nTo enhance the image press 'Enhance' then choose an option from the list. \nTo Exit the program press 'File' then 'Exit'"; //THE HELP TEXT
		JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE); //Showing the text in an JOptionPane
	}
	
	//Show the about text in a JOptionPane
	private void showAbout(){
		String aboutText = "Image Enhancer v1\n\nCreated by George Allen\n"; //THE ABOUT TEXT
		JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE); //Showing the text
	}
	
	//Enhance and show the image in the spatial domain
	//Makes calls to "spatialDomain" method in ImageEnhancer in order to enhance
	//Then shows the result in the JLabel
	//Then calculates the average mean square error
	private void enhanceImageSpatial(){
		if(image != null){ //If we have loaded an image
			//image.spatialDomain(3, 3, ImageEnhancerUtils.MODE); //Enhance in the spatial domain using the Mode Filter	
			image.spatialDomainEnhancement(ImageEnhancerUtils.MEAN); //Enhance in the spatial domain using the Mean Filter
			image.spatialDomainEnhancement(ImageEnhancerUtils.MEDIAN); //Enhance in the spatial domain using the Median Filter			
			resultImageLabel.setIcon(image.getResultIconForJLabel()); //Set the image in the JLabel
			resultImageLabel.setText(""); //remove the text
			//Calculate the average mean square error
			Mat or = Highgui.imread("Z:/Users/George/Dropbox/Work/Part 3/IA/Image Analysis/swanOriginal.bmp", 0); 
			System.out.println(ImageEnhancerUtils.calculateAverageMeanSquareError(or, or));
			System.out.println(ImageEnhancerUtils.calculateAverageMeanSquareError(or, image.getResult()));
		} else {
			JOptionPane.showMessageDialog(this, "Please load an image", "Error", JOptionPane.ERROR_MESSAGE); //Show an error if the image had not been loaded
		}
	}
	
	//Enhance and show the iamge in the freequency domain
	//Make calls to "frequencyDomain" method in ImageEnhancer
	//Show the result in JLabel
	//Calculates the avergae mean square error
	private void enhanceImageFrequency(){
		if(image != null){		//If we have loaded an image	
			image.frequencyDomainEnhancement(72, ImageEnhancerUtils.LOW_PASS);		//enhance in the frequency domain
			resultImageLabel.setIcon(image.getResultIconForJLabel()); //show result in the JLabel
			resultImageLabel.setText(""); //clear the text of the JLabel
			//Calculate the average mean square error
			Mat or = Highgui.imread("Z:/Users/George/Dropbox/Work/Part 3/IA/Image Analysis/swanOriginal.bmp", 0);
			System.out.println(ImageEnhancerUtils.calculateAverageMeanSquareError(or, or));
			System.out.println(ImageEnhancerUtils.calculateAverageMeanSquareError(or, image.getResult()));
		} else {
			JOptionPane.showMessageDialog(this, "Please load an image", "Error", JOptionPane.ERROR_MESSAGE);//Show an error if the image had not been loaded
		}
	}
	
	//Add colour to the enhanced image 
	//Makes calls to "addColour" method in ImageEnhancer
	//Shows the result in the JLabel
	private void addColor(){
		if(image != null){ //If we have loaded an image
			image.findContoursAndAddColour(ImageEnhancerUtils.RGB); //add colour to the image
			resultImageLabel.setIcon(image.getColorIconForJLabel()); //show result in the jlabel
			resultImageLabel.setText(""); //clear the text of the jlabel
		} else {
			JOptionPane.showMessageDialog(this, "Please load an image", "Error", JOptionPane.ERROR_MESSAGE);//Show an error if the image had not been loaded
		}
	}
	
	//Clear the original and result
	private void clear(){
		originalImageLabel.setIcon(null);
		resultImageLabel.setIcon(null);
		originalImageLabel.setText("Load an image");
		resultImageLabel.setText("The result");
		image = null;
	}
	
	//Clear the result
	private void clearResult(){
		if(image == null) return;
		resultImageLabel.setIcon(null);		
		resultImageLabel.setText("The result");
		image.resetResult();
	}
	
	//Sharpen the image by getting a value from the user and calling "sharpen" from imageEnhancer
	private void sharpen(){
		if(image == null){ //if we havent laoded an image
			JOptionPane.showMessageDialog(this, "Please load an image", "Error", JOptionPane.ERROR_MESSAGE);//Show an error if the image had not been loaded
			return;
		}
		float value = 0.0f;
		try{
			value = Float.valueOf(JOptionPane.showInputDialog(this, "Please enter the amount", "Sharpen", JOptionPane.PLAIN_MESSAGE)); //get the value from the user
			if(value < 0.0f){
				JOptionPane.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
				value = 0.0f;
			}
		} catch (Exception e){
			JOptionPane.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
			value = 0.0f;
		}
		if(value != 0.0f && image != null){ //if we have a value and a image we can sharpen
			image.sharpenEnhancement(value); //sharpen the iamge
			resultImageLabel.setIcon(image.getResultIconForJLabel()); //show the result
			resultImageLabel.setText(""); //clear the text
		} 
	}
	
	//CONSTRUCTOR
	public GUI(){
				
		setTitle("Image Enhancer v1"); //set the title
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//set the default exit operation
	
		setBounds(100, 100, 800, 600); //set the bounds of the gui
		
		//ADD THE MENUS AND CONTROLS
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		loadImageMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
		fileMenu.add(loadImageMenuItem);
		fileMenu.add(exitMenuItem);
		menuBar.add(enhanceMenu);
		spatialMenuItem.addActionListener(this);
		frequencyMenuItem.addActionListener(this);
		addColorMenuItem.addActionListener(this);
		clearMenuItem.addActionListener(this);
		clearResultMenuItem.addActionListener(this);
		sharpenMenuItem.addActionListener(this);
		enhanceMenu.add(spatialMenuItem);
		enhanceMenu.add(frequencyMenuItem);
		enhanceMenu.add(addColorMenuItem);
		enhanceMenu.add(sharpenMenuItem);
		enhanceMenu.add(clearMenuItem);
		enhanceMenu.add(clearResultMenuItem);
		menuBar.add(helpMenu);
		helpMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
		
		
		//SET UP THE PANELS AND LABELES	
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		mainPanel.setBorder(padding);
		setContentPane(mainPanel);		
		originalImageLabel.setHorizontalAlignment(JLabel.CENTER);
		resultImageLabel.setHorizontalAlignment(JLabel.CENTER);
		mainPanel.add(originalImageLabel);
		mainPanel.add(resultImageLabel);	
	}
	
	//MENUS
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu enhanceMenu = new JMenu("Enhance");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem loadImageMenuItem = new JMenuItem("Load Image");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	private JMenuItem spatialMenuItem = new JMenuItem("Spatial Domain");
	private JMenuItem frequencyMenuItem = new JMenuItem("Frequency Domain");
	private JMenuItem addColorMenuItem = new JMenuItem("Add Colour");
	private JMenuItem sharpenMenuItem = new JMenuItem("Sharpen");
	private JMenuItem clearMenuItem = new JMenuItem("Clear All");
	private JMenuItem clearResultMenuItem = new JMenuItem("Clear Result");
	private JMenuItem helpMenuItem = new JMenuItem("Help");
	private JMenuItem aboutMenuItem = new JMenuItem("About");
	
	//CONTAINERS
	private JPanel mainPanel = new JPanel(new GridLayout(1, 1));
	
	//BUTTONS AND LABELS
	private JLabel originalImageLabel = new JLabel("Load an Image");
	private JLabel resultImageLabel = new JLabel("The result");	
	
	//THE IMAGE
	private ImageEnhancer image;


}
