import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import Interfaces.*;
import Enumerations.*;
import Exceptions.*;
import Helpers.*;
import MoneySlots.*;
import SnackVendingMachine.*;


class test {

	private static VendingMachine snackVendingMachine;
	
	@BeforeClass
	public static void setUp(){
		snackVendingMachine = VendingMachineFactory.createVendingMachine(); 
	} 
	
	@AfterClass 
	public static void tear(){ 
		
	}

	
	
	/*
	 * 
	Snack Vending Machine will be initialized at first with the following:
HALF_DOLLAR->5
DIME->5
FIFTY_DOLLAR->1
GOLDEN_DOLLAR->5
TWENTY_CENT->5
TWENTY_DOLLAR->1


ALSO: 


Muffins,3	Muffins,3	Muffins,3	Muffins,3	Muffins,3
	Papadum,3	Papadum,3	Papadum,3	Papadum,3	Papadum,3
	Peanuts,3	Peanuts,3	Peanuts,3	Peanuts,3	Peanuts,3
	TeaCake,3	TeaCake,3	TeaCake,3	TeaCake,3	TeaCake,3
	Pakoda,3	Pakoda,3	Pakoda,3	Pakoda,3	Pakoda,3
	
	
	 * 
	 * 
	 * */
	 
	
	
	
	//Inserting fifty dollar note and buying TEACAKE
	@Test
    public void buySnackWithNoChange() {
		
		snackVendingMachine = VendingMachineFactory.createVendingMachine(); 
		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
		
		((SnackVendingMachine)snackVendingMachine).printStats();
		Item item2 = Item.TEACAKE;
		double price = snackVendingMachine.selectItemAndGetPrice(item2);
		
		//should be 50!
		assertEquals(price, 50);
		
		
		Bucket<Item, List<Payable>> bucket = snackVendingMachine.collectItemAndChange();

		List<Payable> change=bucket.getSecond();
		String res="";
		for(Payable c:change){
			res += c.getDenomination()+",";
			System.out.print(c.getDenomination()+",");
			
		}
		
		//should be 50.0,50.0,20.0,
		assertEquals(res, "");
		
	}
	
	
	
	//Inserting five 50$ notes + Buying PAKODA + change of two 50$ & one 20$: 
	@Test
    public void buySnackWithChange() {
		snackVendingMachine = VendingMachineFactory.createVendingMachine(); 
		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);

		((SnackVendingMachine)snackVendingMachine).printStats();
		Item item2 = Item.PAKODA;
		double price = snackVendingMachine.selectItemAndGetPrice(item2);
		
		//should be 80!
		assertEquals(price, 80);
		
		
		Bucket<Item, List<Payable>> bucket = snackVendingMachine.collectItemAndChange();

		List<Payable> change=bucket.getSecond();
		String res="";
		for(Payable c:change){
			res += c.getDenomination()+",";
			System.out.print(c.getDenomination()+",");
			
		}
		
		//should be 50.0,50.0,20.0,
		assertEquals(res, "50.0,50.0,20.0,");
	}
	
	
	
	//Throwing NotFullPaidException
	@Test
	public void NotFullPaidExceptionScenario ()
	{
		
		Assertions.assertThrows(NotFullPaidException.class, new Executable() {

	        @Override
	        public void execute() throws Throwable {

	        	snackVendingMachine = VendingMachineFactory.createVendingMachine(); 
	    		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
	    	
	    		((SnackVendingMachine)snackVendingMachine).printStats();
	    		Item item2 = Item.PAKODA;
	    		double price = snackVendingMachine.selectItemAndGetPrice(item2);
	    		
	    	
	    		Bucket<Item, List<Payable>> bucket = snackVendingMachine.collectItemAndChange();

	  
	    		//Buying again
	    		snackVendingMachine.insertMoney(Note.FIFTY_DOLLAR);
	    		((SnackVendingMachine)snackVendingMachine).printStats();
	    		Item item3 = Item.PAKODA;
	    		double price2 = snackVendingMachine.selectItemAndGetPrice(item2);
	    		
	    	
	    		Bucket<Item, List<Payable>> bucket2 = snackVendingMachine.collectItemAndChange();

	        }
	    });
		
		
		
	}
	
	
	

	
	
}
