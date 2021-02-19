package scot.ianmacdonald.cakemgr.restclient.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Cake {
	
	@NotNull
	@Size(min=2, max=50)
	private String title;
	
	@NotNull
	@Size(min=5, max=100)
	private String description;
	
	@NotNull
	@Size(min=10, max=300)
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Cake other = (Cake) obj;
		return Objects.equals(title, other.title) && Objects.equals(description, other.description)
				&& Objects.equals(image, other.image);
	}
	
}
