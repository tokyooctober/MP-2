package samples.testbed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import samples.testbed.contracts.ContractSamples;

public class MPDay {
	
	private String _contract;
	// 1 is trade price, 2 is the TPO list matching the trade price.
	private HashMap<String,  ArrayList<String>> _priceRows;
	
	private HashMap<String, String> _letterMap;
	private double _dayHigh;
	private double _dayLow;
//	private String _dayOpen;
//	private String _dayClose;
	private static HashMap<String, HashMap<String, MPDay>> _globalmp;
	private double _tick = 1.0;
	private String _date;
	private boolean ok=false;
	
	@SuppressWarnings("unused")
	private MPDay()
	{	
		
	}

	public MPDay(String contract, String date)
	{
		this();
		_contract = contract;
		_date = date;
		_priceRows = new HashMap<String, ArrayList<String>>(100);
		_letterMap = new HashMap<String, String>(100);
	}

	public void add(String timestamp, double open, double high, double low, double close) 
	{
		double price = high;
		if (!ok) { 
			_dayHigh=high;
			_dayLow = low;
			ok=true;
		}
		
		String letter = determineLetter(timestamp);
		if (high > _dayHigh) _dayHigh = high;
		if (low < _dayLow)	_dayLow=low;
		
		while(price>low)
		{	
			String price1= Double.toString(price);
			ArrayList<String> row = _priceRows.getOrDefault(price1, new ArrayList<String>(10));
						
			row.add(letter);			
			_priceRows.put(price1, row);
			price -= _tick;
		}
		 ;
	}
	
	public void print()
	{
			//System.out.println("Data high:"+_dayHigh);
			//System.out.println("Data low:"+_dayLow);
			double price1 = _dayHigh;
			while (price1 > _dayLow)
			{
				String px = Double.toString(price1);
				ArrayList<String> row = _priceRows.get(px);
				System.out.print(" "+px+" ");
				//if (row!=null)
				for (String x : row )
				{
					System.out.print(x);
				}
				price1 -= _tick;
			}
	}

	private String determineLetter(String timestamp) 
	{
		_letterMap.put(timestamp, "A");
		return "A";
	}

	private static synchronized MPDay getDayMP(String contract, String date)
	{
		if (MPDay._globalmp == null)
			MPDay._globalmp = new HashMap<String, HashMap<String, MPDay>>(5);
		
		HashMap<String, MPDay> mpDays = MPDay._globalmp.getOrDefault(contract, new HashMap<String, MPDay>(31) );;
		_globalmp.put(contract, mpDays);
		
		MPDay mpDay = mpDays.getOrDefault(date, new MPDay(contract, date));
		mpDays.put(date,  mpDay);
		
		return mpDay;
	}
	
	public static void MarketProfile_add(String contractId, String dateStr, double open, double high, double low, double close) 
	{
		String[] working = dateStr.split("\\s+");
		//System.out.println("work 0="+working[0]);
		//System.out.println("work 1="+working[1]);
		MPDay mpDay = MPDay.getDayMP(contractId, working[0]);
		mpDay.add(working[1], open, high, low, close);		
	}	
	
	public static void printall()
	{
		for (Map.Entry<String, HashMap<String, MPDay>> entry : _globalmp.entrySet()) {
		    String contract = entry.getKey();
		    HashMap<String, MPDay> mpDays = entry.getValue();
		    System.out.println(contract);
		    
		    for (Map.Entry<String, MPDay> e1 : mpDays.entrySet()) 
		    {
			    String date = e1.getKey();
			    MPDay mp = e1.getValue();
			    System.out.println("\n Date:"+date);
		    	mp.print();
		    }
		}	
	}

}
