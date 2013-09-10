README – BLACKJACK-SIMULATOR

This Java program is a blackjack simulator, which tracks details of large numbers of rounds as they are played against the computer.  The program uses player strategy that is entered in the BlackjackStrategy class.  For now, the strategy used is the generally-accepted consensus, with no provisions made for card count.  The surrender option is not incorporated in this program.  The user is free to incorporate more complex strategies, based on card counting strategies.

The user can play manually against the computer dealer, but this program is intended to simulate a large number of hands in a short time.  The user cannot change the amount of the bet for each round.  Extensive benchmarking has not been conducted, but my 4-year-old Toshiba Satellite A500 laptop (Intel Core 2 Duo processor, 4 GB RAM, running Ubuntu 13.04) simulates 100,000 rounds in under 3 seconds.

This program outputs information from every round into a CSV file named blackjacksim.csv, saved in the same folder where you run the program.  The file has twelve columns, with each hand having one row.  If a player is drawn a pair and splits it, each hand will have a separate row.  The columns are:

A - Round number (will appear more than once if a hand is split)
B - Deck number (increments every time the deck is reshuffled)
C - Number of cards left in deck at beginning of round
D - Card count at beginning of round (-1 for each 2-6 card dealt, +1 for each 10-A card dealt)
E - Hand number in round (if hand was split, the first card of hand 1 and the first card of hand 2 will have been the original hand dealt to the player)
F - Player's hand (no suits, T = 10)
G - Dealer's hand (no suits, T = 10, will be the same for each hand in a round)
H - Player's score
I - Dealer's score
J - Outcome code (W = win, L = loss, P = push, D = double down, S = split, B = blackjack)
K - Money that player won/lost for the hand
L - Money that player played for the hand (equal to the bet unit, except double for a double down)

A user can import the CSV file into a spreadsheet or database, and study the effects of card counting and strategy on outcomes.  It is the size of the CSV file that limits the number of rounds that can be reasonably simulated in one run.

This version is console-based.  A later version may allow the user to enter some settings such as number of rounds and bet unit at runtime, and may introduce a GUI.
