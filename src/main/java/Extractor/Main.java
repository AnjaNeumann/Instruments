package Extractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	public static void main(String[] args) throws Exception {
		LinkedList<InstrumentMaker> makerList = new LinkedList();
		JSONReader jr = new JSONReader();
		for (char letter = 'a'; letter <= 'z'; letter++) {
			jr.parse("/home/anja/Dokumente/SS18/VTA/CDVo2018MIMUL/Personen/" + letter + ".json");
		}
		makerList = jr.getMakers();
		//
		writeHeader("InstrumentData");

		for (InstrumentMaker im : makerList) {
			String ws = im.getWebsite();

			InstrumentParser ip = new InstrumentParser(ws);
			ip.setParams();
			String post = ip.getPost(
					"http://www.mimo-db.eu/MIMO/infodoc/ged/DocumentManagementService.svc/SearchDocumentLinks");
			// System.out.println(post);
			JSONParser parser = new JSONParser();
			JSONObject result = (JSONObject) parser.parse(post);
			JSONArray results = (JSONArray) ((JSONObject) result.get("d")).get("Results");
			// System.out.println(results.size());
			// if (results.size() == 0) {
			// System.out.println(ws);
			// }
			for (Object data : results) {
				JSONObject infos = (JSONObject) data;
				JSONObject props = (JSONObject) infos.get("Properties");
				String title = props.get("title").toString();
				String owner = props.get("owner").toString();
				String date = props.get("eventDate").toString();
				Instrument instrument = new Instrument(title, owner, date);
				im.addInstrument(instrument);
				// System.out.println(title + ", " + owner + ", " + date);
			}
			im.write2File("InstrumentData");
		}
		System.out.println("done");

	}

	public static void writeHeader(String filename) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			String header = "Surname\t Prename\t Title\t Owner\t Date\n";
			bw.write(header);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
