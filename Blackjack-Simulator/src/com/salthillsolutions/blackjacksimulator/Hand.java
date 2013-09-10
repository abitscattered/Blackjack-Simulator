package com.salthillsolutions.blackjacksimulator;

import java.util.ArrayList;
import java.util.Collections;

/* Generic methods for hands of cards.
 * Blackjack-specific methods are in the BlackjackHand class.
 */

public class Hand {

	protected ArrayList<Card> hand;	// The array of cards is a hand

	public Hand() {
		// Create a Hand list that is initially empty.
		hand = new ArrayList<Card>();  
	}

	public void clear() {
		// Discard all the cards from the hand.
		hand.clear();
	}


	public void addCard(Card c) {
		// Add the card c to the hand.  c should be non-null.  (If c is
		// null, nothing is added to the hand.)
		if (c != null) {
			hand.add(c);
		}
	}

	public void removeCard(Card c) {
		// If the specified card is in the hand, it is removed.
		hand.remove(hand.indexOf(c));
	}

	public void removeCard(int position) {
		// If the specified position is a valid position in the hand,
		// then the card in that position is removed.
		if (position >= 0 && position < hand.size())
			hand.remove(position);
	}

	public int getCardCount() {
		// Return the number of cards in the hand.
		return hand.size();
	}

	public Card getCard(int position) {
		// Get the card from the hand in given position, where positions
		// are numbered starting from 0.  If the specified position is
		// not the position number of a card in the hand, then null
		// is returned.
		if (position >= 0 && position < hand.size())
			return (Card)hand.get(position);
		else
			return null;
	}
	
	public void sortBySuit() {
		// Sort the hand, first by suit then by value within each suit
		Collections.sort(hand, new CardSuitComparator());
	}
	
	public void sortByValue() {
		// Sort the hand, first by value then by suit within each value
		Collections.sort(hand, new CardValueComparator());
	}
	
	public void displayHand() {
		// Display each hard of the hand, in a single-line flat list
		for (Card card : hand) {
			System.out.print(card + " ");
		}
		System.out.println("");	// Newline at end of list
	}
	
	public boolean isPair() {
		// Check if a hand of 2 cards contains a pair of the same value
		// If more than 2 cards, this check need not be done
		if (hand.size() != 2) {
			return false;
		}
		
		// Get the value of both cards
		Card card1 = hand.get(0); Card card2 = hand.get(1);
		
		// Compare if the value is the same
		if (card1.getValue() == card2.getValue()) {
			return true;
		} else { // The values are not the same
			return false;
		}
	}
	
	public boolean isValue(int value) {
		// Check if a hand contains at least one card of a given value
		// Cycles through hand, returns true at the first instance of the value
		for (Card card : hand) {
			if (card.getValue() == value) {
				return true;
			}
		}
		
		// Did not find a card of that value
		return false;
	}
	
	public String toString() {
		// Return a String with a flat list of all cards in the hand
		String theHand = "";
		for (Card card : hand) {
			theHand = theHand + " " + card;
		}
		return theHand;
	}
}
