package Extractor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {
	private JSONParser parser;
	private LinkedList<InstrumentMaker> makerList;

	public JSONReader() {
		parser = new JSONParser();
		makerList = new LinkedList<InstrumentMaker>();
	}

	public void parse(String filename) throws FileNotFoundException, IOException, ParseException {

		JSONObject jObj = (JSONObject) parser.parse(new FileReader(filename));
		// JSONObject jObj = (JSONObject) obj;
		for (Object key : jObj.keySet()) {
			JSONObject inner = (JSONObject) jObj.get(key);
			String jobs = inner.get("musicalJobs").toString();
			if (jobs.toLowerCase().contains("instrumentenbauer")) {
				if (inner.toString().contains("Musical Instrument Museums Online")) {
					InstrumentMaker current = getInstrumentMaker(inner, key.toString());
					JSONArray links = (JSONArray) inner.get("links");
					String website = getWebsite(links);
					current.setWebsite(website);
					// System.out.println(website);
					makerList.add(current);
				}
			}
		}
	}

	public LinkedList<InstrumentMaker> getMakers() {
		return makerList;
	}

	private InstrumentMaker getInstrumentMaker(JSONObject inner, String id) {
		// System.out.println(id + "\t" + inner.get("defaultSurName") + ", " +
		// inner.get("defaultPreName"));
		InstrumentMaker maker = new InstrumentMaker(id);
		maker.setPreName(inner.get("defaultPreName").toString());
		maker.setSurName(inner.get("defaultSurName").toString());
		return maker;
	}

	private String getWebsite(JSONArray links) {
		String website;
		for (Object entry : links) {
			if (entry.toString().contains("Archive, Museen, Sammlungen, Nachlässe")) {
				JSONObject mim = (JSONObject) ((JSONArray) entry).get(1);
				JSONArray a_mim = (JSONArray) mim.get("Musical Instrument Museums Online");
				JSONObject o_mim = (JSONObject) a_mim.get(0);
				JSONArray target = (JSONArray) o_mim.get("target");
				website = target.get(0).toString();
				return website;
			}
		}
		return null;

	}

}
