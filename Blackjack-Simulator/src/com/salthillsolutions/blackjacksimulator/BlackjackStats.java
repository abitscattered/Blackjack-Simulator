package com.salthillsolutions.blackjacksimulator;

/* Compiles the wins/losses/pushes in the following circumstances: 
 * Net: based on the total player winnings of all hands in a round
 * Overall: each hand of a round is counted separately
 * Bj: a hand is won/lost/pushed on a blackjack
 * Double: a player hand that was doubled down was won/lost/pushed
 * Split: each split hand was won/lost/pushed
 * Insurance: insurance bet was won or lost - can't be pushed 
 */

public class BlackjackStats {

	// Compile the wins, losses and pushes in the following circumstances:

	
	private int wNet, lNet, pNet;
	private int wOverall, lOverall, pOverall;
	private int wBj, lBj, pBj;
	private int wDouble, lDouble, pDouble;
	private int wSplit, lSplit, pSplit;
	private int wInsurance, lInsurance;
	private int totalWinnings, totalBet;

	
	public int getTotalWinnings() {
		return totalWinnings;
	}
	
	public int getTotalPlayed() {
		return totalBet;
	}

	public void updateWinnings(int roundWinnings, int roundBet) {
		// Declare a net win, loss or push based on the total winnings for all
		// hands in the round (positive = win, negative = loss, zero = push)
		if (roundWinnings > 0) {
			this.wNet++;
		} else if (roundWinnings < 0) {
			this.lNet++;
		} else {
			this.pNet++;
		}
		this.totalWinnings += roundWinnings;
		
		// This is the total money put on the table in this round
		this.totalBet += roundBet;
	}
	
	// Next 3 functions records wins/losses/pushes where someone 
	// drew a blackjack.  Note that blackjack cannot come from a split
	public void winBj() {
		this.wOverall++;
		this.wBj++;
	}
	
	public void lossBj() {
		this.lOverall++;
		this.lBj++;
	}
	
	public void pushBj() {
		this.pOverall++;
		this.pBj++;
	}
	
	// Next 3 functions records wins/losses/pushes where the player
	// doubled down their hand.  The split flag is true if the hand
	// was split before the double down.
	public void winDouble(boolean split) {
		this.wOverall++;
		this.wDouble++;
		if (split) { this.wSplit++; }
	}
	
	public void lossDouble(boolean split) {
		this.lOverall++;
		this.lDouble++;
		if (split) { this.lSplit++; }
	}
	
	public void pushDouble(boolean split) {
		this.pOverall++;
		this.pDouble++;
		if (split) { this.pSplit++; }
	}
	
	// Next 3 functions records wins/losses/pushes where the player
	// did not get a blackjack or double down.
	// The split flag is true if the hand was split.
	public void win(boolean split) {
		this.wOverall++;
		if (split) { this.wSplit++; }
	}
	
	public void loss(boolean split) {
		this.lOverall++;
		if (split) { this.lSplit++; }
	}
	
	public void push(boolean split) {
		this.pOverall++;
		if (split) { this.pSplit++; }
	}
	
	// Next 2 function records wins/losses in insurance bets
	public void winInsurance() {
		this.wInsurance++;
	}
	
	public void lossInsurance() {
		this.lInsurance++;
	}
	

	public String toString() {
		// Return a String with all of the stats, in several lines
		String money = "Total balance: " + totalWinnings + 
				"  Total bet: " + totalBet + "\n";
		String net = "Net: W " + wNet + "  L " + lNet + 
				"  P " + pNet + "\n";
		String overall = "Overall: W " + wOverall + "  L " + lOverall + 
				"  P " + pOverall + "\n";
		String bj = "Blackjacks: W " + wBj + "  L " + lBj +	"  P " + pBj + "\n";
		String dbl = "Doubles: W " + wDouble + "  L " + lDouble + 
				"  P " + pDouble + "\n";
		String split = "Splits: W " + wSplit + "  L " + lSplit + 
				"  P " + pSplit + "\n";
		// Only show insurance if it was used at least once
		String insurance = "";
		if ((wInsurance + lInsurance) > 0) {
			insurance = "Insurance: W " + wInsurance + "  L " + lInsurance + 
					"\n";
		}
		
		return money + net + overall + bj + dbl + split + insurance;
	}

}
