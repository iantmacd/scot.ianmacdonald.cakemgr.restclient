package scot.ianmacdonald.cakemgr.restclient.model;

import java.util.Objects;

public class Cake {
	
	private String title;
	private String description;
	private String image;
	
	public Cake() {
	}
	
	public Cake(String title, String description, String image) {
		super();
		this.title = title;
		this.description = description;
		this.image = image;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public String getImage() {
		return this.image;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, image, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Cake)) {
			return false;
		}
		Cake other = (Cake) obj;
		return Objects.equals(description, other.description) && Objects.equals(image, other.image)
				&& Objects.equals(title, other.title);
	}
	
}
