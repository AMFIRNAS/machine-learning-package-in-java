/**
 * Define Operations on Double array or primitive
 * 
 * @author jun
 *
 */
public class DoubleOperation {

	/**
	 * generate identity matrix
	 * 
	 * @param dimension
	 * @return
	 */
	public static double[][] generateIdentityMatrix(int dimension) {
		double[][] identityMatrix = new double[dimension][dimension];
		
		for (int row = 0; row < dimension; row++) {
			identityMatrix[row][row] = 1.0;
		}		
		
		return identityMatrix;
	}
	
	/**
	 * one double array times the other array
	 * 
	 * @param vector1
	 * @param vector2
	 * @return return the sum
	 */
	public static double time(double[] vector1, double[] vector2) {
		assert vector1.length == vector2.length;
		double sum = 0;
		for (int i = 0; i < vector1.length; i++) {
			sum += vector1[i] * vector2[i];
		}
		
		return sum;
	}
	
	/**
	 * the transpose of a column vector times a matrix
	 * 
	 * @param vector1
	 * @param matrix
	 * @return the vector with the same row and column as vector1
	 */
	public static double[] timeMatrix(double[] vector1, double[][] matrix) {
		int dimension = vector1.length;
		double[] vector = new double[dimension];
		
		for (int column = 0; column < dimension; column++) {
			
			// get the ith column of the matrix
			double[] columnVector = new double[dimension];
			for (int row = 0; row < dimension; row++) {
				columnVector[row] = matrix[row][column];
			}
			
			// time the two vector
			vector[column] = time(vector1, columnVector);
		}
		
		return vector;
	}
	
	public static double[][] time(double[][] matrix1, double[][] matrix2) {
		int dimension = matrix1.length;
		double[][] matrix = new double[dimension][dimension];
		
		for (int row1 = 0; row1 < dimension; row1++) {
			double[] rowVector = new double[dimension];
			for (int column1 = 0; column1 < dimension; column1++) {
				rowVector[column1] = matrix1[row1][column1];
			}
			
			for (int column2 = 0; column2 < dimension; column2++) {
				double[] columnVector = new double[dimension];
				for (int row2 = 0; row2 < dimension; row2++) {
					columnVector[row2] = matrix2[row2][column2];
				}
				
				matrix[row1][column2] = time(rowVector, columnVector);
			}
			
		}
	
		return matrix;
	}
	
	/**
	 * one double array times a contant value
	 * 
	 * @param vector
	 * @param constant
	 * @return
	 */
	public static double[] time(double[] vector, double constant) {
		int length = vector.length;
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = vector[i] * constant;
		}
		return result;
	}
	
	/**
	 * \sum_{t - 1} \cdot \x_{t}
	 * 
	 * @param matrix
	 * @param vector1
	 * @return
	 */
	public static double[] matrixTime(double[][] matrix, double[] vector1) {
		int dimension = vector1.length;
		double[] vector = new double[dimension];
		
		for (int row = 0; row < dimension; row++) {
			double[] rowVector = matrix[row];
			vector[row] = time(rowVector, vector1);
		}
		
		return vector;
	}
	
	/**
	 * x_{t}^{T} \cdot \sum_{t - 1} \cdot x_{t} defined in the algorithm
	 * 
	 * @param vector
	 * @param matrix
	 * @return
	 */
	public static double transformation(double[] vector, double[][] matrix) {
		double[] matrixTransformation = timeMatrix(vector, matrix);
		double value = time(vector, matrixTransformation);
		return value;
	}
	
	/**
	 * whether this vector is zero vector
	 * 
	 * @param vector
	 * @return
	 */
	public static boolean isZeroVector(double[] vector) {
		boolean zeroVector = true;
		
		for (double element : vector) {
			if (element != 0.0) {
				zeroVector = false;
				break;
			}
		}
		
		return zeroVector;
	}
	
	/**
	 * add two double array
	 * 
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double[] add(double[] vector1, double[] vector2) {
		double[] result = new double[vector1.length];
		assert vector1.length == vector2.length;
		for (int i = 0; i < vector1.length; i++) {
			result[i] = vector1[i] + vector2[i];
		}
		
		return result;
	}
	
	/**
	 * one double array minus the other array
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	public static double[][] matrixMinus(double[][] matrix1, double[][] matrix2) {
		int rows = matrix1.length;
		int columns = matrix1[0].length;
		
		double[][] matrix = new double[rows][columns];
		
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = matrix1[row][column] - matrix2[row][column];
			}
		}
		
		return matrix;
	}
	
	/**
	 * a matrix times a constant
	 * 
	 * @param matrix
	 * @param constant
	 * @return
	 */
	public static double[][] time(double[][] matrix, double constant) {
		int rows = matrix.length;
		int columns = matrix[0].length;
		
		double[][] result = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				result[row][column] = matrix[row][column] * constant;
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param vector
	 * @return
	 */
	public static String printArray(double[] vector) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < vector.length; i++){
			double value = vector[i];
			
			if (i != vector.length - 1) {
				sb.append(value + ", ");
			} else {
				sb.append(value);
			}
		}
		
		return sb.toString().trim();
	}
	
	/**
	 * vector product,
	 * 
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double[][] vectorProduct(double[] vector1, double[] vector2) {
		int dimension = vector1.length;
		double[][] vector = new double[dimension][dimension];
		
		for (int row = 0; row < dimension; row++) {
			double value1 = vector1[row];
			for (int column = 0; column < dimension; column++) {
				double value2 = vector2[column];
				vector[row][column] = value1 * value2;
			}
		}
		
		return vector;
	}
}
