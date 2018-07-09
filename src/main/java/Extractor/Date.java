package Extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Date {
	private String date;
	private String time = "";

	public Date(String date) {
		this.date = date;
		extractTime();
	}

	private void extractTime() {
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher = pattern.matcher(date);
		if (matcher.find()) {
			time = matcher.group(0);
		} else {
			pattern = Pattern.compile("\\d{2}");
			matcher = pattern.matcher(date);
			if (matcher.find()) {
				int t = Integer.parseInt(matcher.group(0));
				time = Integer.toString((t - 1) * 100 + 50);
			}
		}
	}

	@Override
	public String toString() {
		return time;
	}
}
