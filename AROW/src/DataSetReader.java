import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * Read Textual File and return the dataset
 * 
 * @author jun
 *
 */
public class DataSetReader {

	private final String mFilePath;

	/**
	 * File path
	 * 
	 * @param filePath
	 */
	public DataSetReader(String filePath) {
		mFilePath = filePath;
	}
	
	/**
	 * read data and transform into 
	 * 
	 * @param separator
	 * @param whether label is in the first column(true) or label is in the last column(false)
	 * @param noLabel: structured Perceptron, there is no label in the data file, in this case, then default label is 1
	 * @param biasFeature : add bias feature 1.0
	 * 
	 * @return
	 */
	public List<double[]> readData(String separator, boolean reverse, boolean noLabel, boolean biasFeature) {
		List<double[]> dataset = new ArrayList<double[]>();
		
		// open a BufferedReader and read the text from the file
		try {
			BufferedReader br = new BufferedReader(new FileReader(mFilePath));
			String currentLine;
			
			while ((currentLine = br.readLine()) != null) {
				String[] record = currentLine.split(separator);
				double[] data = null;
				
				int length = record.length;
				if (biasFeature) {
					length += 1;
				}
				data = new double[length];
				
				if (biasFeature) {
					data[0] = 1.0;
				}
				
				if (!noLabel && reverse) {
					data[data.length - 1] = Double.parseDouble(record[0]);
					
					for (int index = 1; index < record.length; index++) {
						if (biasFeature) {
							data[index] = Double.parseDouble(record[index]);
						} else {
							data[index - 1] = Double.parseDouble(record[index]);
						}
					}
					
				} else {
					
					for (int index = 0; index < record.length; index++) {
						if (biasFeature) {
							data[index + 1] = Double.parseDouble(record[index]);
						} else {
							data[index] = Double.parseDouble(record[index]);
						}
					}
					
				}
				
				dataset.add(data);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataset;
	}

}
