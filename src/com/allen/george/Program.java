package com.allen.george;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.opencv.core.Core;

public class Program {

	//Entry point into the program
	//Creates a new GUI and sets the look and feel to the system look and feel.
	public static void main(String[] args) {
		System.loadLibrary("opencv_java2410");
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //cross platform look and feel					
				} catch (Exception e) {
					e.printStackTrace();
				} 
				GUI g = new GUI(); //new gui
				g.setVisible(true); //make the gui appear on screen
				g.setResizable(false); //do not allow the resizing of the gui
			}
		});
	}

}
