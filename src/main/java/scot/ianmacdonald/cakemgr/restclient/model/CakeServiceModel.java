package scot.ianmacdonald.cakemgr.restclient.model;

import java.util.List;

public class CakeServiceModel {

	private List<Cake> cakeList;
	
	private CakeServiceError cakeServiceError;
	
	public CakeServiceModel(List<Cake> cakeList, CakeServiceError cakeServiceError) {
		
		super();
		this.cakeList = cakeList;
		this.cakeServiceError = cakeServiceError;
	}

	public List<Cake> getCakes() {
		
		return cakeList;
	}

	public CakeServiceError getCakeServiceError() {
		
		return cakeServiceError;
	}
}
