import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of AROW algorithm
 * 
 * Figure 1 of the paper Adaptive Regularization of Weight Vectors
 * 
 * @author jun (xiejuncs@gmail.com)
 *
 */
public class AROWAlgorithm {

	// number of Epoch
	private final int mEpoch;
	
	// hyperparameter r
	private final double mHyperParameter;
	
	// dataset
	private final List<double[]> mDataset;
	
	// number of cross validation
	private final int mFold;
	
	// default label if there is no label
	private final double mDefaultLabel;
	
	// whether there is label for each instance
	private final boolean mNoLable;
	
	/**
	 * 
	 * @param epoch : number of epochs used to do training
	 * @param dataFilePath : data file path used to store the dataset
	 * @param separator : separator used to separate the feature
	 * @param hyperParameter : r used to denote the regularization term
	 * @param fold : how many fold
	 * @param biasFeature : whether incorporate the bias feature
	 * @param reverse : whether label is in the first column(true) or label is in the last column(false)
	 * @param noLabel: structured Perceptron, there is no label in the data file, in this case, then default label is 1
	 * 
	 */
	public AROWAlgorithm(int epoch, String dataFilePath, String separator, 
											double hyperParameter, int fold, boolean biasFeature, 
											boolean reverse, boolean noLabel, double defaultLabel) {
		mEpoch = epoch;
		DataSetReader reader = new DataSetReader(dataFilePath);
		mHyperParameter = hyperParameter;
		mDataset = reader.readData(separator, reverse, noLabel, biasFeature);
		assert mDataset != null;
		System.out.println("the number of records : " + mDataset.size());
		mFold = fold;
		mDefaultLabel = defaultLabel;
		mNoLable = noLabel;
	}
	
	/**
	 * split the dataset into number of fold
	 * 
	 * @param dataset
	 * @return
	 */
	private List<List<double[]>> splitDataset(List<double[]> dataset) {
		List<List<double[]>> folds = new ArrayList<List<double[]>>();
		int noOfInstance = dataset.size();
		int noOfFold = noOfInstance / mFold;
		
		int left = 0;
		int right = noOfFold;
		while (right <= noOfInstance) {
			List<double[]> fold = new ArrayList<double[]>();
			for (int index = left; index < right; index++) {
				fold.add(dataset.get(index));
			}
			
			folds.add(fold);
			left = right;
			
			if (right == noOfInstance) {
				break;
			}
			
			if (right + noOfFold <= noOfInstance) {
				right = right + noOfFold;
			} else {
				right = noOfInstance;
			}
		}
		
		return folds;
	}
	
	/**
	 * generat training set
	 * 
	 * @param folds
	 * @param testingIndex
	 * @return
	 */
	private List<double[]> generateTrainingSet(List<List<double[]>> folds, int testingIndex) {
		List<double[]> trainingSet = new ArrayList<double[]>();
		for (int index = 0; index < folds.size(); index++) {
			if (index != testingIndex) {
				List<double[]> fold = folds.get(index);
				
				for (double[] element : fold) {
					trainingSet.add(element);
				}
				
			}
		}
		
		return trainingSet;
	}
	
	/**
	 * train the model
	 * 
	 * @param trainingSet : the training set used to train the model
	 * @param featureDimension : define the feature dimension, if need to incorporate the bias feature, then 1 + feature.length; 
	 * 									 if not, then just the length of the feature
	 * @return
	 */
	public double[] train(List<double[]> trainingSet, int featureDimension) {
		double[] weight = new double[featureDimension];
		double[][] variance = DoubleOperation.generateIdentityMatrix(featureDimension);
		
		for (int epoch = 0; epoch < mEpoch; epoch++) {
			// random shuffle the dataset
			Collections.shuffle(trainingSet);
			
			for (double[] instance : trainingSet) {
				double label = getLabel(instance);
				double[] feature = getFeature(featureDimension, instance);
				
				double margin = DoubleOperation.time(weight, feature);
				if (margin * label < 1) {
					double beta = 1 / ( DoubleOperation.transformation(feature, variance) + mHyperParameter );
					double alpha = Math.max(0, beta * (1 - label * DoubleOperation.time(feature, weight)));
					double constant = alpha * label;
					double[] delta = DoubleOperation.time(DoubleOperation.matrixTime(variance, feature), constant) ;
					boolean zeroVector = DoubleOperation.isZeroVector(delta);
					
					// update the weight and variance
					if (!zeroVector) {
						weight = DoubleOperation.add(weight, delta);
						
						double[] sumX = DoubleOperation.matrixTime(variance, feature);
						double[][] sumXX = DoubleOperation.vectorProduct(sumX, feature);
						double[][] betaSumXX = DoubleOperation.time(sumXX, beta);
						double[][] betaSumXXSum = DoubleOperation.time(betaSumXX, variance);
						variance = DoubleOperation.matrixMinus(variance, betaSumXXSum);
					}
				}
			}
		}
		
		return weight;
	}
	
	/**
	 * generate features for each instance
	 * 
	 * @param featureDimension
	 * @param instance
	 * @return
	 */
	private double[] getFeature(int featureDimension, double[] instance) {
		double[] feature = new double[featureDimension];
		System.arraycopy(instance, 0, feature, 0, featureDimension);
		return feature;
	}
	
	/**
	 * return the label of instance, if there is no label for the instance, then according to the
	 * default label
	 * 
	 * @param instance
	 * @return
	 */
	private double getLabel(double[] instance) {
		double label = 0.0;
		
		if (mNoLable) {
			label = mDefaultLabel;
		} else {
			label = instance[instance.length - 1];
		}
		
		return label;
	}
	
	/**
	 * do cross validation and average the result across the folders
	 * 
	 * @return
	 */
	public double crossValidation() {
		int featureDimension = mDataset.get(0).length - 1;
		
		// if there is no label for the instance
		if (mNoLable) {
			featureDimension += 1;
		}
		double sum = 0.0;
		
		// shuffle the dataset and split the dataset into mFold
		Collections.shuffle(mDataset);
		List<List<double[]>> folds = splitDataset(mDataset);
		
		for (int index = 0; index < mFold; index++) {
			int testingIndex = mFold - 1 - index;
			List<double[]> trainingSet = generateTrainingSet(folds, testingIndex);
			System.out.println("no of training data instances : " + trainingSet.size());
			List<double[]> testingSet = folds.get(testingIndex);
			System.out.println("no of testing data instances : " + testingSet.size());
			
			double[] weight = train(trainingSet, featureDimension);
			System.out.println("the " + index + "th validation ");
			sum += evaluate(weight, testingSet, featureDimension);
		}
		
		return sum / mFold;
	}
	
	// evaluate the model
	public double evaluate(double[] weight, List<double[]> testingSet, int featureDimension) {
		double error = 0.0;
		System.out.println("the learned weight is : \n" + DoubleOperation.printArray(weight) + "\n");
		
		for (double[] instance : testingSet) {
			double label = getLabel(instance);
			double[] feature = getFeature(featureDimension, instance);
			
			double prediction = DoubleOperation.time(weight, feature);
			if (prediction * label <= 0) {
				error += 1;
			}
			
		}
		
		System.out.println("the number of misclassification : " + error);
		return error / testingSet.size();
	}
	
	/**
	 * experiment with the AROW algorithm with cross validation
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		AROWAlgorithm arow = new AROWAlgorithm(10, "data/iris-twoclass.csv", ",", 1.0, 10, true, true, false, 1.0);
		double error = arow.crossValidation();
		System.out.println("\nthe error of the algorithm :  " + error );
	}
}
