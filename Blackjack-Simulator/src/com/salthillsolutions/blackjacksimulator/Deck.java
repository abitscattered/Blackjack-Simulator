package com.salthillsolutions.blackjacksimulator;

// Generic methods for decks of cards.

public class Deck {

	private Card[] deck;   // An array of 52 Cards, representing the deck.
	private int cardsUsed; // How many cards have been dealt from the deck.

	public Deck() {
		// Create an unshuffled deck of cards
		deck = new Card[52*Settings.NUMBER_OF_DECKS];
		int cardCt = 0;	// Card counter
		for ( int d = 0; d < Settings.NUMBER_OF_DECKS; d++) { // New deck
			for ( int s = 0; s <= 3; s++ ) { // New suit
				for ( int v = 1; v <= 13; v++ ) { // New value in suit
					deck[cardCt] = new Card(v,s);
					cardCt++;
				}
			}	
		}

		// Since this is a new deck, no cards have been used yet
		cardsUsed = 0;
	}

	public void shuffle() {
		// PShuffle all cards of the desk in a random order
		for ( int i = 52*Settings.NUMBER_OF_DECKS - 1; i > 0; i-- ) {
			int rand = (int)(Math.random()*(i+1));
			Card temp = deck[i];
			deck[i] = deck[rand];
			deck[rand] = temp;
		}

		// Since the entire deck is freshly shuffled, no cards have been used
		cardsUsed = 0;
	}

	public Card dealCard() {
		// Deals one card from the deck and returns it.
		if (cardsUsed == 52*Settings.NUMBER_OF_DECKS)
			// If the deck runs out, shuffle it and restart
			// This should never happen in blackjack, since the deck
			// is shuffled well before it is depleted.
			shuffle();
		cardsUsed++;
		return deck[cardsUsed - 1];
	}
		
	public int cardsLeft() {
		// Returns the number of cards left to be dealt in the deck.
		return 52*Settings.NUMBER_OF_DECKS - cardsUsed;
	}
	
	public int deckCount() {
		// Returns the current deck count, based on +1 for played 2-6 cards
		// and -1 for played aces and 10-cards
		int deckCount = 0;
		for (int i = 0; i < cardsUsed; i++) {
			deckCount += deck[i].getCardCountValue();
		}
		
		return deckCount;
	}

}