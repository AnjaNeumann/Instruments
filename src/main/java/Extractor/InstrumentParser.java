package Extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class InstrumentParser {
	private String url;
	private String eid;
	private JSONObject params;
	private String postUrl;

	public InstrumentParser(String url) {
		this.url = url;
		setEId();
		this.postUrl = "http://www.mimo-db.eu/MIMO/infodoc/ged/";
	}

	private void setEId() {
		String[] parts = url.split("=");
		this.eid = parts[parts.length - 1];
	}

	public void setParams() throws Exception {
		String firstGet = getHTML();
		final Pattern pattern = Pattern.compile("c.storeId = 'searchStore'(.+?)POST'");
		final Matcher matcher = pattern.matcher(firstGet);
		matcher.find();
		String[] result = matcher.group(0).split(";");
		// for (int i = 0; i < result.length; i++) {
		// System.out.println(result[i]);
		// }
		JSONParser parser = new JSONParser();
		String[] values = result[1].split("=", 2);

		String val = values[1].replaceAll(" ", "").replaceAll("\"", "\\\\\"").replaceAll("\'", "\"")
				.replace("sort", "\"sort\"").replaceAll("dir", "\"dir\"").replaceAll(" ", "");
		// System.out.println(val);

		// String test =
		// "{\"values\":{\"authoritySearchId\":\"\",\"authoritySearchIndex\":\"\",\"eid\":\"I_IFD_AUTPP_0003664\"\"form\":0,\"searchExpression\":\"enumlinks[type:src,destUid:61435]=\\\"\\\"\",\"searchExpressionLabel\":\"\",\"searchId\":\"\",\"searchType\":\"documentLinks\"},\"limit\":\"80\",\"start\":\"0\",\"sort\":\"\",\"dir\":\"ASC\",\"searchExpressionLabel\":\"\"}";
		// System.out.println(test);
		JSONObject json = (JSONObject) parser.parse(val);
		json.put("limit", "80");
		json.put("start", "0");

		JSONObject jValues = (JSONObject) json.get("values");
		jValues.put("eid", eid);
		jValues.put("searchExpressionLabel", "");
		jValues.put("searchType", "documentLinks");
		jValues.put("authoritySearchId", "");
		jValues.put("authoritySearchIndex", "");
		params = json;
		// System.out.println(json);
		// System.out.println(val2);

	}

	public String getPost(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(this.params.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			if ((line = rd.readLine()) != null) {
				// System.out.println(line);
				result = line;
			}
			// handle response here...
		} catch (Exception ex) {
			// handle exception here
			ex.printStackTrace();
		} finally {
			httpClient.close();
		}
		return result;
	}

	public String getHTML() throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(this.url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

}
