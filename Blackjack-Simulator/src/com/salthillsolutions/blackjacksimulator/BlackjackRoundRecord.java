package com.salthillsolutions.blackjacksimulator;

import java.util.ArrayList;

/* Data is collected for each round.  The BlackjackSimulator then
 * writes the information from an object of this class into a CSV
 * file, which can be read by the user after the simulation is finished. 
 */

public class BlackjackRoundRecord {

	private int roundNum;		// Round number
	private int shuffleNum;		// Increments by one when deck is shuffled
	private int cardsLeft;		// Cards left in the deck
	private int deckCount;		// Card count at the beginning of the round
	private int handsInRound;	// 1, unless the hand is split
	
	// The playerCards, playerScores, playerMods, playerWinnings members
	// are Strings since the information is split by hand with "/" after
	// each hand, including the last (or only) hand
	private String playerCards;	// Each card of the player's hand (no suits)
	private String dealerCards; // Each card of the dealer's hand (no suits)
	private String playerScores;
	private String dealerScore;
	// For playerMods: W=win, L=loss, P=push, S=split, D=double, B=blackjack
	private String playerMods;	
	private String playerWinnings;
	private String playerBets;
	
	public BlackjackRoundRecord (int roundNum, int shuffleNum, int cardsLeft, 
			int deckCount ) {
		// Preliminary information that is captured as the object
		// is created, before the round starts

		// These values are straight ints
		this.roundNum = roundNum+1;	// Make sure the first round is #1
		this.shuffleNum = shuffleNum;
		this.cardsLeft = cardsLeft;
		this.deckCount = deckCount;
	}
	
	public void processRoundResults (ArrayList<BlackjackHand> playerHands, 
			BlackjackHand dealerHand ) {
		// This information is captured after the round is played
		
		// Record the number of hands played in this round
		this.handsInRound = playerHands.size();
		
		// Initialize the fields that will collect information from each hand
		String cardList = "", scoreList = "", modList = "", 
				winningsList = "", betList = "";
		
		// Go through this loop for each player hand.  Each hand ends with
		// a "/" divider, even if only a single hand was played
		for (BlackjackHand hand : playerHands) {
			// Get the value of each card played as one character (10 = "T")
			// Suits are ignored since they are irrelevant
			for (int i = 0; i < hand.getCardCount(); i++ ) {
				Card card = hand.getCard(i);
				cardList = cardList + card.getValueAsOneChar();
			}
			cardList = cardList + "/";
			// Get the blackjack score of that hand
			scoreList = scoreList + hand.getBlackjackScore() + "/";
			// Generate the playerMods in a few steps.  First find 
			// whether the hand was a win/loss/push, and whether there
			// was a blackjack
			String handResult = handOutcome(hand.getBlackjackScore(), 
					dealerHand.getBlackjackScore(), hand.getCardCount(),
					dealerHand.getCardCount(), hand.isSplitFlag());
			// Add flags if the hand was insured, doubled or split
			// Insurance will only show up in the first hand, in case of
			// a split
			if (hand.isInsuranceFlag() && hand.equals(playerHands.get(0))) { 
				handResult = handResult + "I"; }
			if (hand.isDoubleFlag() ) { handResult = handResult + "D"; }
			if (hand.isSplitFlag() ) { handResult = handResult + "S"; }
			// Now add all of the mod info into the modList
			modList = modList + handResult + "/";
			// Find the total winnings for that hand
			winningsList = winningsList + 
					(int) (getWinnings(handResult)*hand.getBet()) + "/";
			betList = betList + 
					(int) (getTotalBet(handResult)*hand.getBet()) + "/";
			
		}
		// All player hands have been assessed; put into the fields
		// for this round's object
		this.playerCards = cardList;
		this.playerScores = scoreList;
		this.playerMods = modList;
		this.playerWinnings = winningsList;
		this.playerBets = betList;
		
		// For the dealer, get the value of each card played
		// as one character (10 = "T").  The only dealer modifier
		// would be a blackjack, but that's already accounted for
		// in the playerMods field
		cardList = "";
		for (int i = 0; i < dealerHand.getCardCount(); i++ ) {
				Card card = dealerHand.getCard(i);
				cardList = cardList + card.getValueAsOneChar();
		}
		// Update the dealer fields.  The score is also obtained.
		// There is no "/" divider used, since we know the dealer
		// can only have one hand per round
		this.dealerCards = cardList;
		this.dealerScore = String.valueOf(dealerHand.getBlackjackScore());
	}
	
	public String handOutcome (int playerHand, int dealerHand, int playerCards,
			int dealerCards, boolean splitFlag) {
		// Determine whether the hand was a Win, Loss or Push
		// Also determine whether a blackjack was obtained by the player,
		// dealer or both
		
		if (playerHand > 21) {
			return "L";
		} else if (dealerHand > 21) {
			return "W";
		} else if (playerHand == 21 && dealerHand == 21 && playerCards == 2 
				&& dealerCards == 2 && splitFlag == false) {
			return "PB";
		} else if (playerHand == 21 && playerCards == 2 && splitFlag == false) {
			return "WB";
		} else if (dealerHand == 21 && dealerCards == 2) {
			return "LB";
		} else if (playerHand > dealerHand) {
			return "W";
		} else if (playerHand < dealerHand) {
			return "L";
		} else {
			return "P";
		}
	}
	
	public float getWinnings (String handResult) {
		// Once the handResult is generated (these are the characters that
		// go into the playerMods field), we can use them to determine the
		// winnings for that hand.  The returned value is the multiplier of
		// the original bet that is won/lost by the player
		// The returned value is a float since a blackjack by the player
		// pays 1.5 times the original bet
		// Insurance results in a loss of 1/2 bet except in cases of 
		// LBI (loss to a blackjack), insurance pays to make return of 0
		// PBI (push to a blackjack), insurance pays to make return of 1
		// 
		
		switch (handResult) {
		case "W":
		case "WS":
		case "WBI":
			return 1f;
		case "WI":
		case "WIS":	
			return 0.5f;
		case "WB":
			return 1.5f;
		case "WD":
		case "WDS":
			return 2f;
		case "WID":
		case "WIDS":
			return 1.5f;
		case "L":
		case "LS":
		case "LB":
			return -1f;
		case "LI":
		case "LIS":	
			return -1.5f;
		case "LBI":
			return 0f;
		case "LD":
		case "LDS":
			return -2f;
		case "LID":
		case "LIDS":
			return -2.5f;
		case "PBI":
			return 1f;
		case "PI":
		case "PIS":
		case "PIDS":	
			return -0.5f;
		default:	// All non-insured pushes return 0, no money won or lost
			return 0f;
		}		
	}
	
	public float getTotalBet (String handResult) {
		// The total amount bet in that hand.  D adds one betUnit, I adds
		// half a betUnit.  Split doesn't add because each split hand
		// will account for their own betUnit.
		// WBI is put at 1f because the player simply gets even money, no
		// more money is risked.
		// 
		
		switch (handResult) {
		case "W":
		case "WB":
		case "WBI":
		case "L":
		case "LB":	
		case "P":
		case "PB":
		case "WS":
		case "LS":
		case "PS":	
			return 1f;
		case "WI":
		case "LI":
		case "LBI":
		case "PI":
		case "PBI":
		case "WIS":
		case "LIS":
		case "PIS":
			return 1.5f;
		case "WD":
		case "LD":	
		case "PD":
		case "WDS":
		case "LDS":
		case "PDS":	 
			return 2f;
		case "WID":
		case "LID":
		case "LBID":
		case "PID":
		case "PBID":
		case "WIDS":
		case "LIDS":
		case "PIDS":
			return 2.5f;
		default:
			return 1f;
		}		
	}
	
	public String csvRound() {
		
		String csvRound = "";
		
		if (Settings.FLATCSV) {
			// Return the data in CSV format, in a single line
			// for the entire round.  In player fields, each hand is separated
			// by a "/" divider
			// Output: roundNum,shuffleNum,cardsLeft,deckCount,handsInRound,
			// "playerCards","dealerCards","playerScore","dealerScore",
			// "playerMods","playerWinnings"
			
			csvRound = roundNum + "," + shuffleNum + "," + 
					cardsLeft + "," + deckCount + "," + handsInRound + ",\"" +
					playerCards + "\",\"" + dealerCards + "\",\"" + 
					playerScores + "\",\"" + dealerScore + "\",\"" + 
					playerMods + "\",\"" + playerWinnings + "\",\"" + 
					playerBets + "\"\n";
			return csvRound;
		} else {
			// Returns the data in CSV format, with a separate
			// line for each hand in the round. 
			// Output: roundNum,shuffleNum,cardsLeft,deckCount,handNum,
			// "playerCards","dealerCards",playerScore,dealerScore,
			// "playerMods",playerWinnings
			
			// Each of these arrays will contain information for one hand
			String[] eachHand = playerCards.split("/");
			String[] eachScore = playerScores.split("/");
			String[] eachMod = playerMods.split("/");
			String[] eachWinnings = playerWinnings.split("/");
			String[] eachBet = playerBets.split("/");
			
			for (int i = 0; i < handsInRound; i++) {
				String thisHand = roundNum + "," + shuffleNum + "," + 
						cardsLeft + "," + deckCount + "," + (i+1) + ",\"" +
						eachHand[i] + "\",\"" + dealerCards + "\"," + 
						eachScore[i] + "," + dealerScore + ",\"" + 
						eachMod[i] + "\"," + eachWinnings[i] + "," +
						eachBet[i];
				csvRound = csvRound + thisHand + "\n";
			}
		}
				
		return csvRound;
		
	}
	
}
