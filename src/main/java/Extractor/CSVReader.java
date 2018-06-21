package Extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CSVReader {
	private HashMap<String, String[]> locations;

	public void readFile(String path) {
		String csvFile = path;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		locations = new HashMap<>();

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] owner = line.split(cvsSplitBy);
				if (owner.length > 2) {
					locations.put(owner[0].substring(1).trim(), new String[] { owner[2], owner[3] });
					// System.out.println("owner: " +
					// owner[0].substring(1).trim() + " , latitude: " + owner[2]
					// + ", longitude: " + owner[3]);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public HashMap<String, String[]> getLocations() {
		return locations;
	}

}
