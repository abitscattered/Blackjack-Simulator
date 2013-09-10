package com.salthillsolutions.blackjacksimulator;

/* Player's strategy for the simulation.  At this point, the strategy is
 * very basic; always reject insurance, and the hand play is not affected
 * by the card count.
 * Dealer's strategy is locked: hit 16 or less, stand 17 or more, hit soft 17
 * Options: Hit, Stand, Double, sPlit
 */

public class BlackjackStrategy {
	
	public static char insurance () {
		// Player's move if the dealer's up card is an ace. For now,
		// we will always refuse insurance.
		return 'N';
	}
	
	public static char dealerMove (BlackjackHand dealerHand) {
		// Dealer's next move depends solely on the dealer's hand
		// Hit on 16 or less, or soft 17; Stand on 18 or more, or hard 17
		if (dealerHand.getBlackjackScore() <= 16 || 
				dealerHand.getSoft17() == true) {
			return 'H';
		} else {
			return 'S';
		}
		
	}
	
	public static char playerMove (BlackjackHand playerHand, 
			Card dealerUpCard) {
		// Player's next move is dependent on the player's hand and the
		// dealer's up card
		// Options are: Hit, Stand, Double, sPlit
		
		// The point value of the up card
		int upCard = dealerUpCard.getBlackjackValue(); 
		
		// If the player has exactly 2 cards, check if they are a pair
		if (playerHand.getCardCount() == 2 && playerHand.isPair()) {
			// The value of the first card is the value of the pair
			int thePair = playerHand.getCard(0).getValue();
			// Now get the next move based on the pair
			switch (thePair) {
			case 1:
				// Aces should always be split, but most casinos do not allow
				// resplitting aces. If the pair of aces results from a split,
				// we must hit, otherwise we will split.
				if (playerHand.isSplitFlag()) {
					return 'H';
				} else {
					return 'P';					
				}
			case 2:
			case 3:
				if (upCard <= 3 || upCard >= 8) {
					return 'H';
				} else {
					return 'P';
				}
			case 4:
				return 'H';
			case 5:
				if (upCard == 1 || upCard == 10) {
					return 'H';
				} else {
					return 'D';
				}
			case 6:
				if (upCard <= 2 || upCard >= 7) {
					return 'H';
				} else {
					return 'P';
				}
			case 7:
				if (upCard == 1 || upCard >= 8) {
					return 'H';
				} else {
					return 'P';
				}
			case 8:
				return 'P';
			case 9:
				if (upCard == 1 || upCard == 7 || upCard == 10) {
					return 'S';
				} else {
					return 'P';
				}
			case 10:
			case 11:
			case 12:
			case 13:
				// 10-13 is 10, jack, queen, king
				return 'S';
			default:
				// Should never get here!
				return 'X';
			}	
		}
		
		// If the player has exactly 2 unpaired cards, check if one is an ace
		if (playerHand.getCardCount() == 2 && playerHand.isValue(1)) {
			// One card is an ace; find the value of the non-ace card
			int theOtherCard = playerHand.getCard(0).getValue();
			if (theOtherCard == 1) {  // In case the first card was the ace
				theOtherCard = playerHand.getCard(1).getValue();
			}
			// Now get the next move based on the non-ace card 
			// It shouldn't be a 10-valued card - that would be a blackjack
			switch (theOtherCard) {
			case 2:
			case 3:
				if (upCard <= 4 || upCard >= 7) {
					return 'H';
				} else {
					return 'D';
				}
			case 4:
			case 5:
				if (upCard <= 3 || upCard >= 7) {
					return 'H';
				} else {
					return 'D';
				}
			case 6:
				if (upCard <= 2 || upCard >= 7) {
					return 'H';
				} else {
					return 'D';
				}
			case 7:
				if (upCard == 1 || upCard >= 9) {
					return 'H';
				} else if (upCard == 2 || upCard == 7 || upCard == 8) {
					return 'S';
				} else {
					return 'D';
				}
			case 8:
			case 9:
				return 'S';
			default:
				// Should never get here!
				return 'X';
			}	
		}
		
		// If the player has 2 unpaired cards with no ace, or more than
		// 2 cards, calculate the score of the hand
		// The score cannot be less than 5 (3-2), and 5-8 always hits
		// The score cannot be more than 20, and 17-20 always stands
		// Note that any situation calling for a Double will be a Hit if more
		// than 2 cards have been played
		// Now get the next move based on the current score
		switch (playerHand.getBlackjackScore()) {
		case 5:
		case 6:
		case 7:
		case 8:
			return 'H';
		case 9:
			if (upCard <= 2 || upCard >= 7) {
				return 'H';
			} else {
				if (playerHand.getCardCount() == 2) {
					return 'D';
				} else {
					return 'H';
				}
			}
		case 10:
			if (upCard == 1 || upCard == 10) {
				return 'H';
			} else {
				if (playerHand.getCardCount() == 2) {
					return 'D';
				} else {
					return 'H';
				}
			}
		case 11:
			if (upCard == 1) {
				return 'H';
			} else {
				if (playerHand.getCardCount() == 2) {
					return 'D';
				} else {
					return 'H';
				}
			}
		case 12:
			if (upCard <= 3 || upCard >= 7) {
				return 'H';
			} else {
				return 'S';
			}
		case 13:
		case 14:
		case 15:
		case 16:
			if (upCard == 1 || upCard >= 7) {
				return 'H';
			} else {
				return 'S';
			}
		case 17:
		case 18:
		case 19:
		case 20:
			return 'S';
		default:
			// Should never get here!
			return 'X';
		}
	}
}
