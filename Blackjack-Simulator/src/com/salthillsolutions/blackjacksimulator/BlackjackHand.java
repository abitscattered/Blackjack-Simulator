package com.salthillsolutions.blackjacksimulator;

/* These methods are specific for blackjack hands.
 */

public class BlackjackHand extends Hand {
	
	// The bet for that hand 
	private int bet;
	
	// True if that hand was doubled down
	private boolean doubleFlag = false;	
	
	// True if the hand was split, or was created from a split
	private boolean splitFlag = false;  
	
	// True if insurance was taken on the hand
	private boolean insuranceFlag = false;
	
	
	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public boolean isDoubleFlag() {
		return doubleFlag;
	}

	public void setDoubleFlag(boolean doubleFlag) {
		this.doubleFlag = doubleFlag;
	}

	public boolean isSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(boolean splitFlag) {
		this.splitFlag = splitFlag;
	}
	
	public boolean isInsuranceFlag() {
		return insuranceFlag;
	}

	public void setInsuranceFlag(boolean insuranceFlag) {
		this.insuranceFlag = insuranceFlag;
	}

	public BlackjackHand(int bet) {
		// Constructor for the BlackjackHand.
		// No cards, bet as given, doubleFlag and splitFlag false
		this.clear();
		this.bet = bet;
	}

	public int getBlackjackScore() {
		// Calculate the blackjack score of the hand.
		int score = 0, aces = 0;
		
		// Cycle through all cards and add their score
		for (Card card : hand) {
			if (card.getValue() == 1) { 
				aces++;	// Keep track of aces, in case score > 21
			}
			score += card.getBlackjackValue();
		}
		
		// If the score is over 21 and there are aces,
		// change the value of an ace from 11 to 1 (subtract 10 from score)
		while (score > 21 && aces > 0) {
			aces--;
			score -= 10;
		}
		
		// Return the int of the score
		return score;
	}

	public boolean getSoft17() {
		// Verify if a two-card hand is a soft 17 (with an ace)
		if (hand.size() == 2 && getBlackjackScore() == 17 && isValue(1)) {
			return true;
		} else {
			return false;
		}
	}
}
