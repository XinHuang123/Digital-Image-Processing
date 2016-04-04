package com.allen.george.enhancement;

import java.util.Arrays;

import org.opencv.core.Mat;

//A helper class for the ImageEnhancer
//Holds variables needed to determine which filter type will be used in the spatial or frequency domain
//Holds variables needed to determine which colour space to use
//Holds methods for calculating the mean square error and finding the Min/Max of a set of three values
//Holds methods for calculating the mean, median and mode of a matrix
public class ImageEnhancerUtils {	

	//FINAL STATIC INTEGERS FOR DIFFERENT TYPES OF ENHANCMENTS
	public static final int MEDIAN = 0; //MEDIAN FILTER INT
	public static final int MEAN = 1; //MEAN FILTER INT
	public static final int MODE = 2; //MODE FILTER INT
	public static final int LOW_PASS = 0; //LOW PASS FILTER INT
	public static final int HIGH_PASS = 1; //HIGH PASS FILTER INT
	public static final int BAND_PASS = 2;	//BAND PASS FILTER INT
	public static final int RGB = 0; //RGB COLOUR SPACE INT
	public static final int HSV = 1; //HSV COLOUR SPACE INT
	
	//Method for calculating the average mean square error
	//Takes in the orgininal and result matrices
	//returns the total error has a double value
	//Based on code from the following example: http://stackoverflow.com/questions/9525249/calculating-mse-mean-squared-error
	public static double calculateAverageMeanSquareError(Mat img, Mat res){
		double totalError = 0.0f; //variable to hold the total error
		//loop through the whole image pixels
		for(int y = 0; y < img.rows(); y ++){
			for(int x = 0; x < img.cols(); x ++){
				double[] imgPixels = img.get(y, x); //get the original pixels
				double[] resPixels = res.get(y,x); //get the new "enhanced" pixels
				totalError += Math.pow(imgPixels[0] - resPixels[0], 2);		//add the error between the pixels to the power of 2								
			}
		}
		
		double width = img.width();
		double height = img.height();
		double averageError = totalError / (width * height); //calculate the average error
		
		return averageError; //return total average error
	}
	
	//find the minimum of three doubles
	//return the smallest value
	public static double getMin(double a, double b, double c){
		double[] values = new double[] {a, b, c}; //double array of all three values
		double minValue = values[0]; //init a minimum value
		for(int i = 0; i < values.length; i ++){ //loop through the values
			if(values[i] < minValue){ //if the current value is less than the current minimum value
				minValue = values[i]; //we have found a new minimum value, so we set it as the smallest
			}
		}
		
		return minValue; //return the smallest value
	}
	
	//find the maximum of three doubles
	//return the biggest value
	public static double getMax(double a, double b, double c){
		double[] values = new double[] {a, b, c};//double array of all three values
		double maxValue = values[0];//init a maximum value
		for(int i = 0; i < values.length; i ++){//loop through the values
			if(values[i] > maxValue){//if the current value is greater than the current maximum value
				maxValue = values[i];//we have found a new maximum value, so we set it as the biggest
			}
		}
		
		return maxValue;//return the biggest value
	}
	
	//A method to get the median value of a matrix
	//Takes in a matrix as an argument
	//Returns the median value as a double array
	//Based on code from the following example: http://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers
	public static double[] getMedianValue(Mat maskSubMat) {
		double[] medianArray = new double[maskSubMat.width() * maskSubMat.height()]; // create an array to store the result
																					
		// loop through the masks pixels
		for (int y = 0; y < maskSubMat.height(); y++) {
			for (int x = 0; x < maskSubMat.width(); x++) {
				medianArray[x + y * maskSubMat.width()] = maskSubMat.get(y, x)[0]; //add the mask data to the median array
			}
		}

		Arrays.sort(medianArray); // first sort the array
		int middleValue = medianArray.length / 2;		
		if (!isEven(medianArray.length)) { // if the array length is odd
			// we simple return the middle value
			return  new double[] { medianArray[middleValue] };
		} else { // the array length is even
			//we find the middle value by getting the middle value minus 1 and adding it to the middle value. All divided by two
			return new double[] { (medianArray[middleValue - 1] + medianArray[middleValue])	/ 2 };	
		}

	}
		
	//A method to get the mode value of a matrix
	//takes in a matrix as an argument
	//returns the mode value as a double array
	//Based on code from the following example: http://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers
	public static double[] getModeValue(Mat maskSubMat){
		double[] modeArray = new double[maskSubMat.width() * maskSubMat.height()]; // create and array to store the result
			
		//loop through the mask pixels
		for(int y = 0; y < maskSubMat.height(); y ++){
			for(int x = 0; x < maskSubMat.width(); x ++){
				modeArray[x + y * maskSubMat.width()] = maskSubMat.get(y, x)[0]; //add the mask data to the mode array
			}
		}
			
		double maxValue = 0; //variable to store the mode value
		int maxCount = 0; //the number of times the mode value appeared
			
		for(int i = 0; i < modeArray.length; i ++){ //loop through the array
			int count = 0; //set the count to zero
			for(int j = 0; j < modeArray.length; j++){ //loop through the array again
				//if the same number appears again
				if(modeArray[j] == modeArray[i]){
					count ++; //increment the count
				}					
			}
			if(count > maxCount){ //if the count is greater than the maxCount
				maxCount = count; //the maxCount = count
				maxValue = modeArray[i]; //set the mode value
			}
		}
			
		return new double[] { maxValue };	//return the mode value as a double array	
			
	}
		
	//method to check if an integer is even or not
	//takes in an integer
	//returns a boolean
	public static boolean isEven(int value){
		if(value % 2 == 0){ //there is no remainder so it is even
			return true; 
		} else { //there is a remainder so it is false
			return false;
		}
	}
		

	//A method to get the mean value of a matrix
	//Takes in a matrix as an argument
	//Returns the mean value as a double array
	//Based on code from the following example: http://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers
	public static double[] getMeanValue(Mat maskSubMat) {
		double total = 0; //double to store the running total
		
		//loop through the pixels in the mask
		for (int y = 0; y < maskSubMat.height(); y++) {
			for (int x = 0; x < maskSubMat.width(); x++) {
				//add the current pixel value to the total
				total += maskSubMat.get(y, x)[0];
			}
		}
		//divide the total by the total size of the matrix
		double res = (total / (maskSubMat.width() * maskSubMat.height()));
			
		//return the result
		return new double[] { res }; 
	}	
		
	
}
