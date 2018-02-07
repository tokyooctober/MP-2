package samples.testbed;

import java.util.ArrayList;
import java.util.HashMap;

public class MPDay {
	
	private String _contract;
	// 1 is timeperiod, 2 is trade price, 3 is the list matching the trade price.
	private HashMap<String, HashMap<String, ArrayList<String>>> _TPOs;
	private String _dayHigh;
	private String _dayLow;
	private String _dayOpen;
	private String _dayClose;
	
	
	@SuppressWarnings("unused")
	private MPDay()
	{	
		
	}

	public MPDay(String contract)
	{
		this();
		_contract = contract;
		_TPOs = new HashMap<String, HashMap<String, ArrayList<String>>>(100);
	}

	public void add(String timestamp, double open, double high, double low, double close) 
	{
		double tick = 1.0;
		double price = high;
		HashMap<String, ArrayList<String>> oneTPO;
		String letter = determineLetter(timestamp);

		oneTPO = _TPOs.get(timestamp);
		if (oneTPO == null)	oneTPO = new HashMap<String, ArrayList<String>>(100);

		while(price>=low) {
			
			String price1= Double.toString(price);
			ArrayList<String> list = oneTPO.get(price1);
			list.add(letter);
			
			price -= tick;
		}
		_TPOs.put(timestamp, oneTPO);
	}

	private String determineLetter(String timestamp) {
	
		return "A";
	}
	

}
