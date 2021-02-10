package scot.ianmacdonald.cakemgr.restclient.model;

public class Cake {
	
	private String title;
	private String description;
	private String image;
	
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

}
