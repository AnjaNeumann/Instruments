package Extractor;

import java.util.Objects;

public class Instrument {
	private String title;
	private String date;
	private String owner;

	public Instrument(String title, String owner, String eventDate) {
		this.title = title;
		this.owner = owner;
		this.date = eventDate;
	}

	public String getTitle() {
		return title;
	}

	public String getOwner() {
		return owner;
	}

	public String getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, date, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (obj == this) return true;
		else if (obj.getClass().equals(this.getClass())) {
			Instrument other = (Instrument) obj;
			return Objects.equals(title, other.title) && Objects.equals(owner, other.owner)
					&& Objects.equals(date, other.date);
		} else return false;
	}
}
