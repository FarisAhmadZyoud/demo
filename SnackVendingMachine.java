package SnackVendingMachine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Enumerations.Coin;
import Enumerations.Item;
import Enumerations.Note;
import Exceptions.NotFullPaidException;
import Exceptions.NotSufficientChangeException;
import Exceptions.SoldOutException;
import Helpers.Bucket;
import Helpers.Inventory;
import Helpers.Keypad;
import Interfaces.Payable;
import Interfaces.VendingMachine;
import MoneySlots.Card;
import MoneySlots.CardSlot;
import MoneySlots.CoinSlot;
import MoneySlots.NoteSlot;

/**
 * 
 * 
 * 
 */

public class SnackVendingMachine implements VendingMachine {
	private Inventory<Payable> cashInventory = new Inventory<Payable>();
	private double totalSales;
	private Item currentItem;
	private double currentBalance;//in cents
	private SnackSlot[][] snacksSlots;
	private Keypad keypad;
	private CardSlot cardSlot;
	private NoteSlot noteSlot;
	private CoinSlot coinSlot;
	
	
	public Inventory<Payable> getCashInventory() {
		return cashInventory;
	}

	public void setCashInventory(Inventory<Payable> cashInventory) {
		this.cashInventory = cashInventory;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public SnackSlot[][] getSnacksSlots() {
		return snacksSlots;
	}

	public void setSnacksSlots(SnackSlot[][] snacksSlots) {
		this.snacksSlots = snacksSlots;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public SnackVendingMachine() {
		initialize();
	}

	public void initialize() {
		
		coinSlot = new CoinSlot();
		cardSlot = new CardSlot();
		noteSlot = new NoteSlot();
		
		
	
		for (Coin c : Coin.values()) {
			cashInventory.put(c, 5);
		}
		for (Note n : Note.values()) {
			cashInventory.put(n, 1);
		}
	

		
		//Initializing SnacksSlots
		Item item;
		snacksSlots = new SnackSlot[5][5];
		for(int i=0;i<5;i++) {
			if(i==0)
				item = Item.MUFFINS;
			else if(i==1)
				item = Item.PAPADUM;
			else if(i==2)
				item = Item.PEANUTS;
			else if(i==3)
				item = Item.TEACAKE;
			else
				item = Item.PAKODA;
				
			for(int j=0;j<5;j++) {
				snacksSlots[i][j] = new SnackSlot(item, 3);
			}
		}
		
		keypad = new Keypad();
	
	}
	
	public boolean hasItem(Item item)
	{
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				Item selectedItem = snacksSlots[i][j].getItem();
				if(selectedItem == item &&  snacksSlots[i][j].getQuantity() >0)
					return true;
			}
		}
		return false;
	}
	
	public void dispense(Item item)
	{
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				Item selectedItem = snacksSlots[i][j].getItem();
				int quantitySelected = snacksSlots[i][j].getQuantity();
				if(selectedItem == item &&  quantitySelected >0) {
					snacksSlots[i][j].dispense();
					break;
				}
					
			}
		}
		
	}
	
	public void clearSnacksSlots()
	{
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				snacksSlots[i][j].clear();
			}
		}
		
	}

	@Override
	public double selectItemAndGetPrice(Item item) {
		if (hasItem(item)) {
			currentItem = item;
			return currentItem.getPrice();
		}
		throw new SoldOutException("Sold Out, Please buy another item");
	}

	

	

	
	@Override
	public void insertMoney(Payable money) {

		if(money instanceof Coin)
			coinSlot.insertMoney((Coin)money, this);
		else if(money instanceof Note)
			noteSlot.insertMoney((Note)money, this);
		else if(money instanceof Card)
			cardSlot.insertMoney((Card)money, this);
	}
	
	
	
	@Override
	public Bucket<Item, List<Payable>> collectItemAndChange() {
		Item item = collectItem();
		totalSales = totalSales + currentItem.getPrice();

		List<Payable> change = collectChange();

		return new Bucket<Item, List<Payable>>(item, change);
	}

	public Item collectItem() throws NotSufficientChangeException,
            NotFullPaidException{
        if(isFullPaid()){
            if(hasSufficientChange()){
                dispense(currentItem);
                return currentItem;
            }           
            throw new NotSufficientChangeException("Not Sufficient change in Inventory ");                                                    
           
        }
        double remainingBalance = currentItem.getPrice() * 100 - currentBalance;
        throw new NotFullPaidException("Price not full paid, remaining in cents : ", 
                                          remainingBalance);
    }

	public List<Payable> collectChange() {
		double changeAmount = currentBalance - currentItem.getPrice() * 100;
		List<Payable> change = getChange(changeAmount);
		updateCashInventory(change);
		currentBalance = 0;
		currentItem = null;
		return change;
	}

	@Override
	public List<Payable> refund() {
		List<Payable> refund = getChange(currentBalance);
		updateCashInventory(refund);
		currentBalance = 0;
		currentItem = null;
		return refund;
	}

	public boolean isFullPaid() {
		if (currentBalance >= currentItem.getPrice() * 100) {
			return true;
		}
		return false;
	}

	public List<Payable> getChange(double changeAmount) throws NotSufficientChangeException{
        List<Payable> changes = Collections.EMPTY_LIST;
       
        if(changeAmount > 0){
            changes = new ArrayList<Payable>();
            double balance = changeAmount;
            while(balance > 0){
            	
            	if(balance >= Note.FIFTY_DOLLAR.getDenomination() * 100
                        && cashInventory.hasItem(Note.FIFTY_DOLLAR)){
                changes.add(Note.FIFTY_DOLLAR);
                balance = balance - Note.FIFTY_DOLLAR.getDenomination() * 100;
                continue;
               
            	}
            	else if(balance >= Note.TWENTY_DOLLAR.getDenomination() 
                            && cashInventory.hasItem(Note.TWENTY_DOLLAR)){
                    changes.add(Note.TWENTY_DOLLAR);
                    balance = balance - Note.TWENTY_DOLLAR.getDenomination() * 100;
                    continue;
                    
                }else if(balance >= Coin.GOLDEN_DOLLAR.getDenomination() 
                                 && cashInventory.hasItem(Coin.GOLDEN_DOLLAR)) {
                    changes.add(Coin.GOLDEN_DOLLAR);
                    balance = balance - Coin.GOLDEN_DOLLAR.getDenomination() * 100;
                    continue;
                   
                }else if(balance >= Coin.HALF_DOLLAR.getDenomination() 
                                 && cashInventory.hasItem(Coin.HALF_DOLLAR)) {
                    changes.add(Coin.HALF_DOLLAR);
                    balance = balance - Coin.HALF_DOLLAR.getDenomination();
                    continue;
                   
                }
                else if(balance >= Coin.TWENTY_CENT.getDenomination() 
                        && cashInventory.hasItem(Coin.TWENTY_CENT)) {
		           changes.add(Coin.TWENTY_CENT);
		           balance = balance - Coin.TWENTY_CENT.getDenomination();
		           continue;
		          
                }
		        else if(balance >= Coin.DIME.getDenomination() 
		                        && cashInventory.hasItem(Coin.DIME)) {
		           changes.add(Coin.DIME);
		           balance = balance - Coin.DIME.getDenomination();
		           continue;
		          
		         }else{
                    throw new NotSufficientChangeException("Not Sufficient Change Please try another product");
                                      
                }
            }
        }
       
        return changes;
    }

	@Override
	public void reset() {
		cashInventory.clear();
		clearSnacksSlots();
		totalSales = 0;
		currentItem = null;
		currentBalance = 0;
	}

	public void printStats() {
		System.out.println("Total Sales : " + totalSales);
		
		//printing Snacks Slots
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				System.out.print("\t" + snacksSlots[i][j]);
			}
			System.out.println("");
		}
		System.out.println("Current Cash Inventory : " + cashInventory);
	}

	public boolean hasSufficientChange() {
		return hasSufficientChangeForAmount(currentBalance - currentItem.getPrice()*100);
	}

	public boolean hasSufficientChangeForAmount(double d) {
		boolean hasChange = true;
		try {
			getChange(d);
		} catch (NotSufficientChangeException nsce) {
			return hasChange = false;
		}

		return hasChange;
	}

	public void updateCashInventory(List<Payable> refund) {
		for (Payable c : refund) {
			cashInventory.deduct(c);
		}
	}

	public double getTotalSales() {
		return totalSales;
	}

}
