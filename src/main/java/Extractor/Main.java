package Extractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application implements MapComponentInitializedListener {

	GoogleMapView mapView;
	GoogleMap map;
	private final ComboBox<String> comboBox = new ComboBox<String>();
	private static HashMap<String, InstrumentMaker> imMap = new HashMap<>();
	private static LinkedList<String> options = new LinkedList<String>();
	static HashMap<String, String[]> locations;
	private static LinkedList<Marker> marker = new LinkedList<>();

	public static void main(String[] args) throws Exception {
		CSVReader cr = new CSVReader();
		cr.readFile("Orte.csv");
		locations = cr.getLocations();
		LinkedList<InstrumentMaker> makerList = new LinkedList<InstrumentMaker>();
		JSONReader jr = new JSONReader();
		for (char letter = 'a'; letter <= 'z'; letter++) {
			jr.parse("Personen/" + letter + ".json");
		}
		makerList = jr.getMakers();
		//
		// writeHeader("InstrumentData");

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
				String title = props.get("title").toString().replace("\n", "").replace("\"", "").trim();
				String owner = props.get("owner").toString().replace("\n", "").replace("\"", "").trim();
				String date = props.get("eventDate").toString().trim();
				Instrument instrument = new Instrument(title, owner, date);
				im.addInstrument(instrument);
				// System.out.println(title + ", " + owner + ", " + date);
			}
			// im.write2File("InstrumentData");
			// im.write2JSONFile("instrumentJSONdata", locations);
			/*
			 * String option = im.getSurName() + ", " + im.getPreName();
			 * imMap.put(option, im); options.add(option);
			 */

		}
		System.out.println("done");
		// launch(args);

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

	@Override
	public void start(Stage stage) throws Exception {
		// Create the JavaFX component and set this as a listener so we know
		// when
		comboBox.getItems().addAll(options);
		comboBox.setOnAction((event) -> {
			for (Marker m : marker) {
				map.removeMarker(m);
			}
			marker = new LinkedList<>();
			map.setZoom(map.getZoom() + 1);
			String selectedPerson = comboBox.getSelectionModel().getSelectedItem();
			System.out.println("ComboBox Action (selected: " + selectedPerson + ")");
			InstrumentMaker im = imMap.get(selectedPerson);
			for (Instrument i : im.getInstruments()) {
				String[] coords = locations.get(i.getOwner());
				// System.out.println(i.getTitle() + ":\t " + coords[0] + ", " +
				// coords[1]);
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLong(Float.parseFloat(coords[0]), Float.parseFloat(coords[1])))
						.visible(Boolean.TRUE).title(i.getTitle());
				marker.add(new Marker(markerOptions));
				map.addMarker(marker.getLast());
				// add Marker
			}
		});

		// ObservableList<String> options =
		// FXCollections.observableArrayList("Option 1", "Option 2", "Option
		// 3");
		// final ComboBox comboBox = new ComboBox(options);
		BorderPane bp = new BorderPane();
		ToolBar tb = new ToolBar();
		tb.getItems().add(comboBox);
		bp.setTop(tb);

		// the map has been initialized, at which point we can then begin
		// manipulating it.
		mapView = new GoogleMapView();
		mapView.addMapInializedListener(this);
		bp.setCenter(mapView);

		Scene scene = new Scene(bp);
		// Group root = (Group) scene.getRoot();
		// root.getChildren().add(comboBox);

		stage.setTitle("JavaFX and Google Maps");
		stage.setScene(scene);
		stage.show();

	}

	@Override
	public void mapInitialized() {
		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(new LatLong(55.953251, -3.188267)).overviewMapControl(false).panControl(false)
				.rotateControl(false).scaleControl(false).streetViewControl(false).zoomControl(false).zoom(5);

		map = mapView.createMap(mapOptions);

		// // Add a marker to the map
		// MarkerOptions markerOptions = new MarkerOptions();
		// markerOptions.position(new LatLong(55.953251,
		// -3.188267)).visible(Boolean.TRUE)
		// .title("University of Edinburgh");
		// Marker marker1 = new Marker(markerOptions);
		// map.addMarker(marker1);
		// marker.add(marker1);
		// // Add a marker to the map
		// MarkerOptions markerOptions2 = new MarkerOptions();
		// markerOptions2.position(new LatLong(51.336918,
		// 12.38785)).visible(Boolean.TRUE)
		// .title("Museum für Musikinstrumente");
		// Marker marker2 = new Marker(markerOptions2);
		// map.addMarker(marker2);
		// marker.add(marker2);
		//
		// MarkerOptions markerOptions3 = new MarkerOptions();
		// markerOptions3.position(new LatLong(49.4484222,
		// 11.0751827)).visible(Boolean.TRUE)
		// .title("Germanisches Nationalmuseum");
		// Marker marker3 = new Marker(markerOptions3);
		// map.addMarker(marker3);
		// marker.add(marker3);

	}

}
