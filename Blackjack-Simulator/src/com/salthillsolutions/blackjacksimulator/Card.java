package com.salthillsolutions.blackjacksimulator;

import java.util.Comparator;

// Generic methods for a single card.

public class Card {
	
	// Codes for the 4 suits
	public final static int SPADES = 0, HEARTS = 1, DIAMONDS = 2, CLUBS = 3;

	// Codes for the non-numeric cards; if ACE is to be high, it would be 14.
	public final static int ACE = 1, JACK = 11, QUEEN = 12, KING = 13;
	
	protected final int suit;   // The suit of this card, one of the constants
	protected final int value;  // The value of this card, from 1 to 13.

	
	public Card(int theValue, int theSuit) {
		// Construct a card with the specified value and suit.
		// Value must be between 1 and 13.  Suit must be between
		// 0 and 3.  If the parameters are outside these ranges,
		// the constructed card object will be invalid.
		value = theValue;
		suit = theSuit;
	}

	public int getSuit() {
		// Return the int that codes for this card's suit.
		return suit;
	}

	public int getValue() {
		// Return the int that codes for this card's value.
		return value;
	}
	
	public String getSuitAsString() {
		// Return a String representing the card's suit.
		// (If the card's suit is invalid, "?" is returned.)
		switch (suit) {
		case SPADES:   return "Spades";
		case HEARTS:   return "Hearts";
		case DIAMONDS: return "Diamonds";
		case CLUBS:    return "Clubs";
		default:       return "?";
		}
	}
	
	public String getSuitAsOneChar() {
		// Return a one-character String representing the card's suit.
		// (If the card's suit is invalid, "?" is returned.)
		switch (suit) {
		case SPADES:   return "S";
		case HEARTS:   return "H";
		case DIAMONDS: return "D";
		case CLUBS:    return "C";
		default:       return "?";
		}
	}

	public String getValueAsString() {
		// Return a String representing the card's value.
		// If the card's value is invalid, "?" is returned.
		switch (value) {
		case 1:   return "Ace";
		case 2:   return "2";
		case 3:   return "3";
		case 4:   return "4";
		case 5:   return "5";
		case 6:   return "6";
		case 7:   return "7";
		case 8:   return "8";
		case 9:   return "9";
		case 10:  return "10";
		case 11:  return "Jack";
		case 12:  return "Queen";
		case 13:  return "King";
		default:  return "?";
		}
	}
	
	public String getValueAsOneChar() {
		// Return a one-character String representing the card's value.
		// If the card's value is invalid, "?" is returned.
		switch (value) {
		case 1:   return "A";
		case 2:   return "2";
		case 3:   return "3";
		case 4:   return "4";
		case 5:   return "5";
		case 6:   return "6";
		case 7:   return "7";
		case 8:   return "8";
		case 9:   return "9";
		case 10:  return "T";
		case 11:  return "J";
		case 12:  return "Q";
		case 13:  return "K";
		default:  return "?";
		}
	}

	public String toString() {
		// Return a 2-character representation of this card, such as
		// "6H" or "QS".
		return getValueAsOneChar() + getSuitAsOneChar();
		
		// We could return a full string representation of this card, such as
		// "Queen of Spades", by uncommenting the following line
		//return getValueAsString() + " of " + getSuitAsString();
	}
	
	public int getBlackjackValue() {
		// Return the int of the number of points for this card in blackjack
		// 10, Jack, Queen, King are all worth 10 points.
		// Ace is worth 11 points, although that could be reduced to 1 later.
		if (value > 10) {
			return 10;
		} else if (value == 1) {
			return 11;
		}
		return value;
	}
	
	public int getCardCountValue() {
		// Return a value of +1 if the card is 2-6, 0 if the card is 7-9, 
		// or -1 if ten-value card or ace 
		if (value >= 2 && value <= 6) {
			return 1;
		} else if (value >= 10 || value == 1) {
			return -1;
		}
		return 0;	
	}
		
}
	
class CardSuitComparator implements Comparator<Card>{
	// This comparator ranks first by suit, then by value within the suit

	public int compare(Card c1, Card c2) {
		int result = Integer.compare(c1.getSuit(),c2.getSuit());
		if (result == 0) {
			result = -1*(Integer.compare(c1.getValue(),c2.getValue()));
		}
		return result;
	}
}

class CardValueComparator implements Comparator<Card>{
	// This comparator ranks first by value, then by suit within the value
	
	public int compare(Card c1, Card c2) {
		int result = -1*(Integer.compare(c1.getValue(),c2.getValue()));
		if (result == 0) {
			result = Integer.compare(c1.getSuit(),c2.getSuit());
		}
		return result;
	}
}