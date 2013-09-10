package com.salthillsolutions.blackjacksimulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* Blackjack-simulator, version 1.0
 * (C) 2013 Marc Leger
 * 
 * The main class for the blackjack simulator.  Note that settings
 * are found in the Settings class.
 */

public class BlackjackSimulator {
	
	// TODO: Surrender option is not available in this game!
		
	// betUnit is not set to 1 since it is an int, and a blackjack earns 1.5
	static int betUnit = 100;
	
	// These parameters are initialized here for the entire simulation
	static int roundNum = 0;
	static int shuffleNum = 0;
	static int cardsLeft = 0;
	static int deckCount = 0;
	static int totalWinnings = 0;
	static BlackjackStats stats = new BlackjackStats();
	
	// If a round is to begin with fewer cards in the deck than this number,
	// all cards are reshuffled and a new deck is started.
	static int cardsLeftToShuffle = 52*Settings.NUMBER_OF_DECKS *
			(100 - Settings.CUT_CARD_PERCENTAGE) / 100;

	// This is an instance for a single round; it is reset at the start of
	// each round, and its information is appended into a CSV text file 
	// after every round  
	static BlackjackRoundRecord roundRecord = null;


	public static void main(String [ ] args) {
		// The main function - entry point

		// Establish the csvFile to which the results are written, and
		// show it on screen to remind the user
		String csvFile = System.getProperty("user.dir") + "/" + 
				Settings.OUTPUT_FILE;
		System.out.println("Output file: " + csvFile);

		// Initialize the writer which will update the text file after
		// every round. Note that the file is overwritten at the start
		BufferedWriter csvWriter = null;
		try {
			csvWriter = new BufferedWriter(new FileWriter(csvFile));
		} catch (IOException e1) { e1.printStackTrace(); };

		// Initialize a new deck and give it a first shuffle
		Deck deck = new Deck();	// Not shuffled yet!
		deck.shuffle(); shuffleNum++;	// Shuffle it, increase counter by 1
		
		// This loop is repeated for every round
		for (roundNum = 0; roundNum < Settings.TOTAL_ROUNDS; roundNum++) {
			// At certain intervals, give us some statistics
			if ((roundNum % Settings.UPDATE_INTERVAL) == 0) { 
				updateProgress(roundNum); 
			}
			// This function plays the round, return the money won/lost in
			// that hand as an int
			int result = PlayHand(deck);
			// Now write the record of that round to the CSV text file
			try {
				csvWriter.write(roundRecord.csvRound());
			} catch (IOException e2) { e2.printStackTrace(); }
			// If we are playing manually, or it we requested details of
			// every hand, tell us the winnings for that round
			displayIf("This round: " + result +	", total: " + 
					stats.getTotalWinnings() + "\n\n");
		} // Repeat this loop to play more rounds
		
		// Now that we are done, close the CSV file writer 
		try {
			csvWriter.close();
		} catch (IOException e3) { e3.printStackTrace(); }
		
		// Tell us we are finished, and print the statistics for all rounds
		System.out.print("Finished! ");
		updateProgress(Settings.TOTAL_ROUNDS);
	}
	
	public static int PlayHand(Deck deck) {
		// This is where the game is played!
		
		// Note the function showStateOfRound(playerHands, dealerHand, boolean)
		// If the boolean is true, the dealer hole card is hidden
		// If the boolean is false, the dealer hole card is shown
		// If VERBOSE is false, showStateOfRound will display nothing
		
		// Check if there is enough cards to play this round. If not, shuffle
		if ( deck.cardsLeft() < cardsLeftToShuffle ) {
			deck.shuffle();	shuffleNum++;
		}
		
		// Start a new record for this round, and enter some
		// preliminary information
		roundRecord = new BlackjackRoundRecord(roundNum, shuffleNum,
				deck.cardsLeft(), deck.deckCount());
	
		// Initialize the winnings and the amount bet for this round
		int roundWinnings = 0, roundBet = 0;
		
		// The first cards are not dealt in the same sequence as they
		// would at the casino; (players-dealer down-players-dealer up)
		// Since cards are random, this makes no difference to outcomes
		
		// Initialize the dealer's hand, give them two cards
		// The second card in the hand will be hidden from players
		// until the dealer's turn to play (unless blackjack)
		BlackjackHand dealerHand = new BlackjackHand(0);
		dealerHand.addCard(deck.dealCard());
		dealerHand.addCard(deck.dealCard());
		
		// playerHands is an ArrayList with all hands for the player
		// Having an ArrayList allows for handling of splits
		// The first hand is index 0
		ArrayList<BlackjackHand> playerHands = new ArrayList<BlackjackHand>();
		// For each hand, set the bet and deal two cards
		for (int i = 0;i < Settings.HANDS_PER_ROUND;i++) {
			playerHands.add(new BlackjackHand(betUnit));
			roundBet += betUnit;
			playerHands.get(i).addCard(deck.dealCard());
			playerHands.get(i).addCard(deck.dealCard());
		}
		
		// If the dealer's up card is an ace, ask for insurance.
		// Blackjack rules allow an insurance bet of up to half the original
		// bet, but this assumes that insurance will be half of the bet.
		if (dealerHand.getCard(0).getValue() == 1) {
			// Assumed that the decision will be the same for all hands
			char nextMove = 'N';  // Initialize the nextMove variable
			// If Rules.SIMULATION is true, the computer determines
			// whether to take insurance.  Otherwise, the player does so.
			if (Settings.SIMULATION) {
				nextMove = BlackjackStrategy.insurance();
			} else {
				showStateOfRound(playerHands, dealerHand, true);
				nextMove = askPlayerInsurance();	
			}
			// If the choice is Yes, mark the insurance flag for each hand
			if (nextMove == 'Y') {
				for (int i = 0;i < Settings.HANDS_PER_ROUND;i++) {
					playerHands.get(i).setInsuranceFlag(true);
					roundBet += betUnit / 2;
				}
			}
		}
		
		// Check if the dealer has 21 - if so, we will not play out
		// this round.  All player hands lose, unless a hand also
		// had 21 in two cards, then it is a push (no money change).
		if (dealerHand.getBlackjackScore() == 21) {
			for (BlackjackHand hand : playerHands) {
				if (hand.getBlackjackScore() < 21) {
					// Dealer wins this hand
					stats.lossBj();
					if (hand.isInsuranceFlag()) {
						// Insurance was successful, no money change
						stats.winInsurance();
					} else {
						// No insurance, simply lose hand
						roundWinnings -= hand.getBet();
					}
				} else {
					// Both player and dealer got blackjacks
					stats.pushBj();
					if (hand.isInsuranceFlag()) {
						// Insurance was successful, player loses
						// winnings for their blackjack but wins
						// insurance, so roundWinnings = betUnit
						roundWinnings += hand.getBet();
						stats.winInsurance();
					} else {
						// No insurance, simply push hand
					}
				}
			}
			// Display the state of the round, process the results, and exit
			showStateOfRound(playerHands, dealerHand, false);
			roundRecord.processRoundResults(playerHands, dealerHand);
			stats.updateWinnings(roundWinnings, roundBet);
			return roundWinnings; // We are finished; exit this function now
		}
		
		// If insurance was played and we got here, insurance was lost
		// Lose half of betUnit per hand
		// While we are cycling through each hand, also check if all
		// player hands are 21 - if so, we don't need to play out this round
		boolean allBlackjacks = true;
		for (BlackjackHand hand : playerHands) {
			if (hand.getBlackjackScore() < 21) {
				// If at least one hand is not 21, we must play out the round
				allBlackjacks = false;
			}
			if (hand.isInsuranceFlag()) {
				// At this point, insurance is lost, and a separate
				// bet of half of the betUnit is lost.
				stats.lossInsurance();
				roundWinnings -= hand.getBet() / 2;
			}
		}
		
		// If all hands are blackjacks, we will not play this round
		if (allBlackjacks) {
			for (BlackjackHand hand : playerHands) {
				roundWinnings += hand.getBet() * 3 / 2;
				stats.winBj();
			}
			// Display the state of the round, process the results, and exit
			showStateOfRound(playerHands, dealerHand, false);
			roundRecord.processRoundResults(playerHands, dealerHand);
			stats.updateWinnings(roundWinnings, roundBet);
			return roundWinnings; // We are finished; exit the function now
		}
		
		
		// We play each player hand, one at a time
		// If a hand is a blackjack, they will get 1.5 * bet, 
		// but that will be handled after all hands have been played out
		
		int handNum = 0; // Counter of the hand being played
		boolean allBust = true;	// Keep track whether all hands have busted
		
		while ( handNum < playerHands.size() ) {
			// A while loop is used instead of a for loop since the size of the 
			// playerHands ArrayList can grow within the loop due to splits
			
			// Show the player the state of the hands; while in this loop,
			// the dealer's hole card is not shown (hence the true flag)
			showStateOfRound(playerHands, dealerHand, true);
			
			// The outer loop PlayerTurn is exited when no more cards 
			// can be played (after stand, double, split aces or bust)
			// Double can only be played when a hand has 2 cards
			// sPlit can only be played with a hand has 2 cards and a pair 
			PlayerTurn:
				while (playerHands.get(handNum).getBlackjackScore() < 21) {
					// If we have a pair of aces from a split hand, we
					// must stand, so this hand ends now
					if (playerHands.get(handNum).getCard(0).getValue() == 1 &&
							playerHands.get(handNum).isSplitFlag()) {
						break PlayerTurn;
					}
					
					// If more than one hand, tell us which is
					// currently being played
					if (playerHands.size() > 1) {
						displayIf("Hand " + (handNum+1) + " ");
					}
					
					// If Rules.SIMULATION is true, the computer determines
					// the next move.  Otherwise, the player does so.
					char nextMove = 'X';	// Initialize nextMove parameter
					if (Settings.SIMULATION) {
						nextMove = BlackjackStrategy.playerMove
							(playerHands.get(handNum), dealerHand.getCard(0));
					} else {
						nextMove = askPlayerMove(playerHands.get(handNum));	
					}
					
					// Let's execute the next move!
					switch (nextMove) {
					case 'H': // Hit: deal a card
						playerHands.get(handNum).addCard(deck.dealCard());
						displayIf("Player Hit\n");
						break;
					case 'S': // Stand: get out of this loop
						displayIf("Player Stand\n");
						break PlayerTurn;
					case 'D': // Double down: double bet, deal one card, get out
						playerHands.get(handNum).setDoubleFlag(true);
						playerHands.get(handNum).addCard(deck.dealCard());
						roundBet += betUnit;
						displayIf("Player Double\n");
						break PlayerTurn;
					case 'P': // Split: create new hand and move second card of
							  // the original hand to the new hand
						playerHands.add(new BlackjackHand(betUnit));
						Card splitCard = playerHands.get(handNum).getCard(1);
						playerHands.get(handNum).removeCard(splitCard);
						playerHands.get(playerHands.size()-1).addCard(splitCard);
						roundBet += betUnit;
						// Set splitFlag for the original and new hands to true
						playerHands.get(handNum).setSplitFlag(true);
						playerHands.get(playerHands.size()-1).setSplitFlag(true);
						// Deal a second card to each hand, then play the hand again
						playerHands.get(handNum).addCard(deck.dealCard());
						playerHands.get(playerHands.size()-1).addCard(deck.dealCard());
						// If splitting aces, only one card is allowed on
						// each ace, then get out
						if (splitCard.getValue() == 1) {
							displayIf("Player Split (aces - must stand)\n");
							break PlayerTurn;
						} else {
							displayIf("Player Split\n");
							break;	// Didn't split aces, so keep going
						}
					default: // Should never get here
						System.out.println("Not sure what happened, let's stop");
						break PlayerTurn;
					}
					showStateOfRound(playerHands, dealerHand, true);
				}

			// Check if the hand busted; if not, the dealer will have to play
			// out their hand, so set allBust to false
			if (playerHands.get(handNum).getBlackjackScore() <= 21) {
				allBust = false;
			} else {
				displayIf("Player Busted\n");
			}

			handNum++; // Increase the counter by 1
		} 
		
		// Unless all hands busted, we will now play out the dealer's hand
		showStateOfRound(playerHands, dealerHand, false);
		DealerTurn:
			while (dealerHand.getBlackjackScore() < 21 && allBust == false) {
				// The nextMove is determined from BlackjackStrategy
				char nextMove = BlackjackStrategy.dealerMove(dealerHand);
				switch (nextMove) {
				case 'H': // Hit: deal a card
					dealerHand.addCard(deck.dealCard());
					displayIf("Dealer Hit\n");
					break;
				case 'S': // Stand: get out of this loop
					displayIf("Dealer Stand\n");
					break DealerTurn;
				default: // Should never get here
					System.out.println("Not sure what happened, let's stop");
					break DealerTurn;
				}
				showStateOfRound(playerHands, dealerHand, false);
			}
		
		// The final outcome of each hand is determined
		
		// If the dealer bust, every player hand wins unless it also busted.
		if (dealerHand.getBlackjackScore() > 21) {
			// Dealer busted!
			displayIf("Dealer Busted\n");
			for (BlackjackHand hand : playerHands) {
				if (hand.getBlackjackScore() == 21) { 
					// Dealer busted, player's hand was 21
					if (hand.getCardCount() == 2 && hand.isSplitFlag() == false) {
						// If it was in two cards and not split, it's a blackjack
						roundWinnings += hand.getBet() * 1.5;
						stats.winBj();
					} else if (hand.isDoubleFlag() == true) {
						// If doubleFlag is true, it was a double down
						roundWinnings += hand.getBet() * 2;
						stats.winDouble(hand.isSplitFlag());
					} else {
						// Otherwise, just a plain winning hand
						roundWinnings += hand.getBet();
						stats.win(hand.isSplitFlag());
					}
				} else if (hand.getBlackjackScore() < 21) {	
					// Dealer busted, player's hand was less than 21
					if (hand.isDoubleFlag() == true) {
						// If doubleFlag is true, it was a double down
						roundWinnings += hand.getBet() * 2;
						stats.winDouble(hand.isSplitFlag());
					} else {
						// Otherwise, just a plain winning hand
						roundWinnings += hand.getBet();
						stats.win(hand.isSplitFlag());
					}
				} else { 
					// Dealer and player both busted - player loses this hand
					if (hand.isDoubleFlag() == true) {
						// If doubleFlag is true, it was a double down
						roundWinnings -= hand.getBet() * 2;
						stats.lossDouble(hand.isSplitFlag());
					} else {
						// Otherwise, just a plain losing hand
						roundWinnings -= hand.getBet();
						stats.loss(hand.isSplitFlag());
					}
				}
			}
			roundRecord.processRoundResults(playerHands, dealerHand);
			stats.updateWinnings(roundWinnings, roundBet);
			return roundWinnings;  // We are done; exit the function now.
		}
		
		// The dealer has 21 or less, so we now compare hands
		for (BlackjackHand hand : playerHands) {
			if (hand.getBlackjackScore() > 21) {
				// The player's hand busted, automatic loss
				if (hand.isDoubleFlag() == true) {
					// If doubleFlag is true, it was a double down
					roundWinnings -= hand.getBet() * 2;
					stats.lossDouble(hand.isSplitFlag());
				} else {
					// Otherwise, just a plain losing hand
					roundWinnings -= hand.getBet();
					stats.loss(hand.isSplitFlag());
				}
			} else if (hand.getBlackjackScore() > dealerHand.getBlackjackScore()) {
				// Player's hand wins
				if (hand.isDoubleFlag() == true) { 
					// If doubleFlag is true, it was a double down
					roundWinnings += hand.getBet() * 2;
					stats.winDouble(hand.isSplitFlag());
				} else {
					// Otherwise, just a plain winning hand
					roundWinnings += hand.getBet();
					stats.win(hand.isSplitFlag());
				}
			} else if (hand.getBlackjackScore() < dealerHand.getBlackjackScore()) {
				// Dealer's hand wins
				if (hand.isDoubleFlag() == true) { 
					// If doubleFlag is true, it was a double down
					roundWinnings -= hand.getBet() * 2;
					stats.lossDouble(hand.isSplitFlag());
				} else {
					// Otherwise, just a plain losing hand
					roundWinnings -= hand.getBet();
					stats.loss(hand.isSplitFlag());
				}
			} else {
				// The hands tied, so it's a push and nothing won or lost
				if (hand.isDoubleFlag() == true) { 
					// If doubleFlag is true, it was a double down
					stats.pushDouble(hand.isSplitFlag());
				} else {
					// Otherwise, just a plain push
					stats.push(hand.isSplitFlag());
				}
			}	
		}
		roundRecord.processRoundResults(playerHands, dealerHand);
		stats.updateWinnings(roundWinnings, roundBet);
		return roundWinnings; // We are finished, exit the function now
	}
	
	private static void displayIf(String theString) {
		// This function handles several strings that are only shown
		// if the player requests it (VERBOSE = true), or are 
		// playing manually (SIMULATION = false)
		if (Settings.VERBOSE || !Settings.SIMULATION) {
			System.out.print(theString);
		}
	}

	public static char askPlayerMove(Hand hand) {
		// This function is used when playing manually
		
		char playerMove = 'X';	// Initialize the playerMove variable

		// In all cases, the loop continues until the user selects a 
		// valid option for that situation
		if (hand.getCardCount() == 2) {
			// If the hand has two cards, offer the option to Double
			if (hand.isPair()) {
				// If the hand has a pair, also offer option to sPlit
				while (playerMove != 'H' && playerMove != 'S' 
						&& playerMove != 'D' && playerMove != 'P') {
					System.out.print("Hit, Stand, Double, sPlit? ");
					playerMove = readChoice();
				}
			} else {
				while (playerMove != 'H' && playerMove != 'S' && 
						playerMove != 'D') {
					System.out.print("Hit, Stand, Double? ");
					playerMove = readChoice();
				}
			}
			return playerMove;
		} else {
			// The hand has more than two cards, only Hit or Stand
			while (playerMove != 'H' && playerMove != 'S') {
				System.out.print("Hit, Stand? ");
				playerMove = readChoice();
			}
		}
		return playerMove;

	}
	
	private static char askPlayerInsurance() {
		// This function is used when playing manually

		char playerMove = 'X';	// Initialize the playerMove variable

		// If the hand has a pair, also offer option to sPlit
		while (playerMove != 'N' && playerMove != 'Y') {
			System.out.print("Insurance, Yes or No? ");
			playerMove = readChoice();
		}
		return playerMove;
	}

	private static char readChoice() {
		// This function returns a single uppercase character from the keyboard 

		// Initialize the char variable
		char choice = 'X';
		try {
			choice = (char) System.in.read();
			while (System.in.read() != '\n');
		} catch (IOException e) { e.printStackTrace(); }

		// Make sure the returned character is uppercase
		return Character.toUpperCase(choice);
	}

	public static void showStateOfRound(ArrayList<BlackjackHand> playerHands, 
			BlackjackHand dealerHand, boolean playerTurn) {
		// Displays the player hands and the dealer hand
		// This information is only displayed if the player requests it
		// (VERBOSE = true), or are playing manually (SIMULATION = false)
		if (Settings.VERBOSE || !Settings.SIMULATION) { 
			
			if (playerHands.size() > 1) {
				// If more than one hand, give the index number of each hand
				// Calling "hand" in the println command calls hand.toString()
				for (BlackjackHand hand : playerHands) {
					System.out.println("Player Hand " + 
						(1+playerHands.indexOf(hand)) +	": " + hand + " (" +
						hand.getBlackjackScore() + ")");	
				}
			} else {
				// Only one hand, so no need for index number
				System.out.println("Player Hand: " + 
						playerHands.get(0).toString() +	" (" + 
						playerHands.get(0).getBlackjackScore() + ")");
			}

			// If playerTurn == true, do not show dealer's hole card or total
			if (playerTurn) {
				// Only show the dealer's up card
				System.out.println("Dealer Hand:  " + dealerHand.getCard(0));
			} else {
				// Show the dealer's entire hand
				System.out.println("Dealer Hand: " + dealerHand + " (" +
						dealerHand.getBlackjackScore() + ")");
			}
		}

	}
	
	private static void updateProgress(int i) {
		// At set intervals, this function is called to show the user
		// how many hands have been played, and update statistics
		// This is called even if VERBOSE is false
		System.out.println(i + " rounds played");
		System.out.println(stats);
	}
}