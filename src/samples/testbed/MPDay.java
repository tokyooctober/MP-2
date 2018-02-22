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
	
	private String[] bracketCode = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", 
							  "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x"
							};
	
	private String[][] TPO = new String[24][60];
	
	private MPDay()
	{	
		int x=0;
		for(int h=0;h<24;h++)
		{
			for (int m=0;m<=29;m++)
			{
				TPO[h][m] = bracketCode[x];
			}
			x++;
			for (int m=30;m<=59;m++)
			{
				TPO[h][m] = bracketCode[x];
			}
			x++;
		}
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
		ArrayList<String> row ;
		
		if (!ok) { 
			_dayHigh=high;
			_dayLow = low;
			ok=true;
		}
		
		String letter = determineLetter(timestamp);
		if (high > _dayHigh) _dayHigh = high;
		if (low < _dayLow)	_dayLow=low;
		
		while(price>=low)
		{	
			
			String price1= Double.toString(price);
			row = _priceRows.getOrDefault(price1, new ArrayList<String>(10));
			row.add(letter);			
			_priceRows.put(price1, row);
			//System.out.println(_date+" "+timestamp+":"+price+" ("+row.size()+")");
			price -= _tick;
		}
		//print();
		//System.out.println("----------------");
	}
	
	public void print()
	{
			System.out.println("Data high:"+_dayHigh);
			System.out.println("Data low:"+_dayLow);
			double price1 = _dayHigh;
			while (price1 > _dayLow)
			{
				String px = Double.toString(price1);
				ArrayList<String> row = _priceRows.get(px);
				//System.out.print(" "+px+" ("+row.size()+") ");
				System.out.print(" "+px+":");
				for (String x : row )
				{
					System.out.print(x);
				}
				price1 -= _tick;
				System.out.println();
			}
	}

	private String determineLetter(String timestamp) 
	{
		String[] working = timestamp.split(":");
		int h = Integer.parseInt(working[0]);
		int m = Integer.parseInt(working[1]);
		String tpo = TPO[h][m];
		
		_letterMap.put(timestamp, tpo);
		return tpo;
	}

	private static synchronized MPDay getDayMP(String contract, String date)
	{
		if (MPDay._globalmp == null)
			MPDay._globalmp = new HashMap<String, HashMap<String, MPDay>>(5);
		
		HashMap<String, MPDay> mpDays = MPDay._globalmp.get(contract);;
		if (mpDays == null)
		{
			mpDays = new HashMap<String, MPDay>(31) ;
			_globalmp.put(contract, mpDays);
		}
		MPDay mpDay = mpDays.get(date);
		if (mpDay == null)
		{
			mpDay = new MPDay(contract, date);
			mpDays.put(date,  mpDay);	
		}
		
		
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
