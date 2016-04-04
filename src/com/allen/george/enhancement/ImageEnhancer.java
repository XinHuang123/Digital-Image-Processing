package com.allen.george.enhancement;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


//The Image Enhancer Class
//Allows for image enhancement in the frequency and spatial domains to remove noise
//Also implements segmentation methods to add colour to images
public class ImageEnhancer {

	private Mat img; //The original input image
	private Mat res; //The resultant image from enhancement
	private Mat resColor; //The resultant image from adding colour
	
	//Getter for the orgininal image
	public Mat getOriginal(){
		return this.img;
	}
	
	//Getter for the resultant image
	public Mat getResult(){
		return this.res;
	}

	//Constructor
	//Takes in a string "path" of where to load the original
	public ImageEnhancer(String path) {
		img = Highgui.imread(path, 0); //load the orginal from the file using OpenCV HighGUI
		res = img.clone(); //init the resultant image by cloning the original. This gives it the same image size, image type and image data
	}

	//Method to reset the resultant image
	//Reverts the result back to the original image
	public void resetResult() {
		res = img.clone(); //Done by cloning the original.  This gives it the same image size, image type and image data
	}
		
	//Converts an image so it can be shown in a JLabel
	//Returns a new ImageIcon 
	//Takes in the matrix image, for conversion
	//Code based on from the following example: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
	private ImageIcon getImageIconForJLabel(Mat image) {	
		//First we must determine the type of image the matrix is. Colour or Greyscale
		 int type = BufferedImage.TYPE_BYTE_GRAY;
	     if ( image.channels() > 1 ) {
	          type = BufferedImage.TYPE_3BYTE_BGR;
	     }
	     //find the size of the image
	     int bufferSize = image.channels()*image.cols()*image.rows();
	     byte [] b = new byte[bufferSize]; //create a new byte array to store the pixels
	     image.get(0,0,b); // get all the pixels
	     BufferedImage bufferedImage = new BufferedImage(image.cols(),image.rows(), type); //create the image
	     //copy the pixels into the image
	     final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData(); 
	     System.arraycopy(b, 0, targetPixels, 0, b.length);  
	     
	     ImageIcon result = new ImageIcon(bufferedImage); //create the icon from the image
	      
	     return result; //return the icon
	}

	//Wrapper method for getting the original image as an image icon
	public ImageIcon getImageIconForJLabel() {
		return getImageIconForJLabel(img); //return the original as an image icon
	}
	
	//Wrapper method for getting the resultant image as an image icon
	public ImageIcon getResultIconForJLabel() {
		return getImageIconForJLabel(res);//return the result as an image icon
	}

	//Wrapper method for getting the colour image as an image icon
	public ImageIcon getColorIconForJLabel() {
		return getImageIconForJLabel(resColor);//return the colour result as an image icon
	}

	//Spatial Domain enhancement method
	//Takes in the spatial int, meaning the type (Median, Mean, Mode) etc
	//Places the result in the result matrix
	public void spatialDomainEnhancement(int spatialInt) {		
		int width = 3, height = 3;
		
		// loop through each pixel of the image
		for (int y = 0; y < res.height(); y++) {
			for (int x = 0; x < res.width(); x++) {
				// check the mask doesnt go out of the image bounds
				if (x + width > res.width() || y + height > res.height()) {
					continue; // we want to skip these values
				}
				// create the mask image with the defined width and height
				Mat maskSubMat = res.submat(new Rect(x, y, width, height));
				if (spatialInt == ImageEnhancerUtils.MEAN) { //IF WE ARE DOING AN MEAN MASK			
					maskSubMat.put(height / 2, width / 2, ImageEnhancerUtils.getMeanValue(maskSubMat)); //put the mean value in the mask
				} else if (spatialInt == ImageEnhancerUtils.MEDIAN) { //IF WE ARE DOING A MEDIAN MASK				
					maskSubMat.put(height / 2, width / 2,  ImageEnhancerUtils.getMedianValue(maskSubMat)); //put the median value in the mask
				} else if (spatialInt == ImageEnhancerUtils.MODE){
					maskSubMat.put(height / 2, width / 2, ImageEnhancerUtils.getModeValue(maskSubMat));
				}
			}
		}
	}


	//Method to sharpen the resultant image by a certain value
	//Takes in a value to sharpen the image by
	//Code based on the following example: http://www.tutorialspoint.com/java_dip/enhancing_image_sharpness.htm
	public void sharpenEnhancement(float value) {
		Mat r = res.clone(); //create a copy of the resultant image
		Imgproc.GaussianBlur(res, r, new Size(0, 0), value); //blur the image using a GaussianBlur by the value specified.
		Core.addWeighted(res, 1.5, r, -0.5, 0, res); //calculate the weighted sum of the resultant image and the blurred image
	}

	//Method to enhance the image in the frequency domain
	//Takes in a radius and a pass type. These are used to determine the size of the filter to generate and where to put the 0's and 1's
	//Should result in an enhanced image with no noise
	//Code based on the following example: http://docs.opencv.org/doc/tutorials/core/discrete_fourier_transform/discrete_fourier_transform.html
	public void frequencyDomainEnhancement(int radius, int passType) {
		//first we must convert the result to float values
		res.convertTo(res, CvType.CV_32F);

		//now we need to generate a circle filer of the correct radius that uses the pass type we specified			
		//Init the matrix to hold the filter 
		//Same size as the resultant matrix but a type of CV_32F
		Mat circleFilter = Mat.zeros(res.size(), CvType.CV_32F);
				
		double[] zero = new double[] { 0 };
		double[] one = new double[] { 1 };
				

		//loop through the pixels of the circle filter
		for (int y = 0; y < circleFilter.rows(); y++) {
			for (int x = 0; x < circleFilter.cols(); x++) {				
				if ((Math.pow((circleFilter.cols() / 2) - x, 2) + Math.pow((circleFilter.rows() / 2) - y,  2)) <= Math.pow(radius, 2)) { //we are inside the radius of the circle
					if (passType == ImageEnhancerUtils.LOW_PASS) { // WE ARE DOING A LOW PASS
						// we are in the circle so we put a 1 in the filter						
						circleFilter.put(y, x, one); //put the values in the circle filter
					} else if(passType == ImageEnhancerUtils.HIGH_PASS) { // WE ARE DOING A HIGH PASS
						// we are in the circle so we want to put a 0 in the
						// filer
						circleFilter.put(y, x, zero); //put the values in the circle filter
					}

				} else { // We are outside the radius of the circle
					if (passType == ImageEnhancerUtils.LOW_PASS) { //  WE ARE DOING ALOW PASS
						// we are not in the circle so we want to put a 0 in the
						// filter						
						circleFilter.put(y, x, zero); //put the values in the circle filter
					} else if(passType == ImageEnhancerUtils.HIGH_PASS){ //WE ARE DOING A HIGH PASS
						// we are not in the circle so we want to put a 1						
						circleFilter.put(y, x, one); //put the values in the circle filter
					}
				}
			}
		}

				
		// rearrange the quadrants of the image so that the origin is at the center
		int cx = circleFilter.cols() / 2;
		int cy = circleFilter.rows() / 2;

		Mat q0 = new Mat(circleFilter, new Rect(0, 0, cx, cy)); // top left
		Mat q1 = new Mat(circleFilter, new Rect(cx, 0, cx, cy)); // top right
		Mat q2 = new Mat(circleFilter, new Rect(0, cy, cx, cy)); // bottom left
		Mat q3 = new Mat(circleFilter, new Rect(cx, cy, cx, cy)); // bottom right
		Mat tmp = new Mat(q0.size(), q0.type());

		// swap the quadrants top left and bottom right
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);

		// swap the quadrants top right with bottom left
		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);

		Core.normalize(circleFilter, circleFilter, 0, 1, Core.NORM_MINMAX); // normalise the filter		

		//Now we want to perform the forwards DFT on the image
		Core.dft(res, res, Core.DFT_SCALE | Core.DFT_COMPLEX_OUTPUT, 0);

		//Next we apply the filter to the image
		ArrayList<Mat> planes = new ArrayList<Mat>();
		Core.split(res, planes); //divide the multichannel array res into single channel arrays stored in planes
		Mat inputArrayX = planes.get(0); //get the first element in planes 
		Mat inputArrayY = planes.get(1); //get the second element in planes 
		Mat magnitude = new Mat(inputArrayX.size(), inputArrayX.type()); //create a matrix to hold the magnitude
		Mat tempAngle = Mat.zeros(inputArrayX.size(), inputArrayX.type()); //create a matrix to hold the angle. This is temporary as we don't need to use it
		
		//Now we need to calculate the magnitude	  
		Core.cartToPolar(inputArrayX, inputArrayY, magnitude, tempAngle);
		//Next we want to multiply the magnitude by the filter	
		Core.multiply(magnitude, circleFilter, magnitude);
		//Now we need to reverse the magnitude to get the result of the multiplication
		Core.polarToCart(magnitude, tempAngle, inputArrayX, inputArrayY);		
		
		//Now set the two planes back in the plane array
		planes.set(0, inputArrayX);
		planes.set(1, inputArrayY);
		Core.merge(planes, res); //next merge the planes back into the multichannel res array

		// Finally we want to apply an inverse DFT on the image
		Core.dft(res, res, Core.DFT_INVERSE | Core.DFT_REAL_OUTPUT, 0);		
		
		//THIS SHOULD NOW RESULT IN AN ENHANCED IMAGE!		
	}

	//Method to add colour to the resultant image
	//Takes in an integer which specifies what colour space we want to use
	//Based on code from the following example: http://www.codeproject.com/Articles/751744/Image-Segmentation-using-Unsupervised-Watershed-Al
	public void findContoursAndAddColour(int colourType) {
		Mat color = new Mat(res.size(), CvType.CV_32F); //create a matrix to store the coloured image		
		Imgproc.cvtColor(res, color, Imgproc.COLOR_GRAY2RGB); //convert the grayscale result to colour
		resColor = res.clone(); //clone the result into the colour result matrix

		Mat ret = new Mat();

		//FIND THE CONTOURS
		resColor.convertTo(resColor, CvType.CV_8UC1); //convert the image to a matrix of unsigned integers
		Imgproc.blur(resColor, resColor, new Size(3, 3)); //blur the image by a scale of 3, 3
		
		Imgproc.threshold(resColor, ret, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU); //calculate an otsu threshold of the image
		 
		Imgproc.morphologyEx(ret, ret, Imgproc.MORPH_OPEN, Mat.ones(9, 9, CvType.CV_8SC1), new Point(4, 4), 1); //calculate an morphology of the image
		
		Mat dt = new Mat(ret.rows(), ret.cols(), CvType.CV_32FC1); 
		Imgproc.distanceTransform(ret, dt, Imgproc.CV_DIST_L2, 3); //calculate the distance transformed

		Core.normalize(dt, dt, 0.0, 1, Core.NORM_MINMAX); //normalize the image to 0 - 1
		
		Imgproc.threshold(dt, dt, 0.1, 1, Imgproc.THRESH_BINARY); //threshold the image

		Core.normalize(dt, dt, 0.0, 255.0, Core.NORM_MINMAX); //normalize the image back to 0 - 255

		dt.convertTo(dt, CvType.CV_8UC1); //convert the distance transformed to a matrix of unsigned integers
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //init a list of points for the contours
		Mat h = new Mat(); //create the hierarchy

		Imgproc.findContours(dt, contours, h, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE); //find the contours	
		
		
		//ADD THE COLOUR
		resColor = color.clone(); //clone the color matrix into the colour result matrix
		resColor.convertTo(resColor, CvType.CV_32FC3); //convert the colour result matrix to a three channel float matrix
		
		int r = 0, g = 1, b = 2; //variables for accessing the oldRGB array
		
		Point grass = new Point(454,13);
		
		for(MatOfPoint contour : contours){		
			MatOfPoint2f c = new MatOfPoint2f();//create a new contour made up of points
			c.fromList(contour.toList());//add the pixels to the contour
			
			//check if the point is inside the contour, this checks for grass
			if (Imgproc.pointPolygonTest(c, grass, false) > 0) {	//the point is inside	
				//loop through the image pixels
				for (int y = 0; y < color.rows(); y++){ 
					for (int x = 0; x < color.cols(); x++){
						Point xy = new Point(x, y);						
						if (Imgproc.pointPolygonTest(c, xy, false) > 0){ //if the xy point is in the contour assign it the colour, THIS IS THE BACKGROUND							
							double[] oldRGB = resColor.get(y, x); //get the old colour
							//do some maths to allow the old colour to be seen through the new colour
							double[] newRGB = new double[] {oldRGB[r] + ((23 - oldRGB[r]) * 0.6f), oldRGB[g] + ((200 - oldRGB[g]) * 0.6f), oldRGB[b] + ((23 - oldRGB[b]) * 0.6f)}; //create the array of rgb values (GREEN FOR THE GRASS)		
							color.put(y, x, newRGB); ///put the new rgb values into the image												
						} else { //the xy point is outside the contour. THIS IS THE FOREGROUND							
							double[] oldRGB = resColor.get(y, x); //get the old colour
							//do some maths to allow the old colour to be seen through the new colour
							double[] newRGB = new double[] {oldRGB[r] + ((255 - oldRGB[r]) * 0.6f), oldRGB[g] + ((255 - oldRGB[g]) * 0.6f), oldRGB[b] + ((255 - oldRGB[b]) * 0.6f)}; //create the array of rgb values (WHITE FOR THE SWAN)
							color.put(y, x, newRGB); //put the new rgb values into the image
						}
					}
				}					
			}
		}
		
		//CONVERT THE COLOUR
		//Next we want to convert between the chosen colour spaces
		if(colourType == ImageEnhancerUtils.HSV){//HSV
			//Convert to RGB TO HSV
			Imgproc.cvtColor(color, color, Imgproc.COLOR_RGB2HSV_FULL);			
			resColor = color;			
		} else {//RGB		
			//Since the values are set in RGB there is no conversion needed.
			resColor = color;	
		}			
	}



}
