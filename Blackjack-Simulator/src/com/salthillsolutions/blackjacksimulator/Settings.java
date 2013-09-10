package com.salthillsolutions.blackjacksimulator;

// Settings for the run of the program

public class Settings {
	
	// Total number of rounds to play in the simulation
	public static final int TOTAL_ROUNDS = 100000;
	
	// Number of rounds to play between on-screen statistic updates
	// These appear even if VERBOSE == false; they are there to update the
	// user, and also to confirm that the program is still running
	// It is suggested to make this 1/10 of TOTAL_ROUNDS
	public static final int UPDATE_INTERVAL = 10000;
	
	// The betting unit - the number of currency units played at the start
	// of each hand.  Do not make it 1, since roundoff error will occur:
	// it is an integer, and a blackjack earns 1.5 times the bet.
	public static final int BET_UNIT = 100;
	
	// How many 52-card decks in the total deck
	public static final int NUMBER_OF_DECKS = 6;
	
	// When this percent of the total deck is used, we will reshuffle at
	// the next hand
	public static final int CUT_CARD_PERCENTAGE = 70;
	
	// Number of hands to be dealt in each round.  Simplest to maintain at 1
	public static final int HANDS_PER_ROUND = 1;

	// Show the state of each hand, keep false when simulating hands
	// This is overridden to true if the user is playing manually
	public static final Boolean VERBOSE = false;
	
	// Is this a simulation operated by the computer?
	// If this is false, a false VERBOSE value will be overridden
	public static final Boolean SIMULATION = true;
	
	// If true, all hands for a round will be saved in CSV file in a
	// single line.  If false, each hand in a round will be in
	// a separate line - if a hand is split, it will have two lines in
	// the file.
	public static final Boolean FLATCSV = false;
	
	// Name of output file with blackjack hand data.  The file will be
	// stored in the directory where the program is running.
	public static final String OUTPUT_FILE = "blackjacksim.csv";
	
}