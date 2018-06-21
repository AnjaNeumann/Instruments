package Extractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class InstrumentMaker {
	private String id;
	private String surName;
	private String preName;
	private String website;
	private LinkedList<Instrument> instruments;

	public InstrumentMaker(String id) {
		this.id = id;
		instruments = new LinkedList<Instrument>();
	}

	public String getID() {
		return id;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getPreName() {
		return preName;
	}

	public void setPreName(String preName) {
		this.preName = preName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void addInstrument(Instrument instrument) {
		instruments.add(instrument);
	}

	public void write2File(String filename) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename, true);
			bw = new BufferedWriter(fw);

			for (Instrument instrument : instruments) {
				String towrite = surName + "\t " + preName + "\t " + instrument.getTitle() + "\t "
						+ instrument.getOwner() + "\t " + instrument.getDate() + "\n";
				bw.write(towrite);
			}

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

	public LinkedList<Instrument> getInstruments() {
		return instruments;
	}
}
