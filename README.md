# CS 351: Project 3 Part 2
Author: Kiana Tarter

## How to run
Run Scrabble.java 

- No command line arguments needed
- This portion uses sowpods.txt for the dictionary

## Program Description:
The program shows a GUI representation of the Scrabble game 

## Design Decisions
- The dictionary words are stored in all lowercase
  for simplicity.
- Square multipliers are read from the before board
  since the after board will have the score covered.
- The human player must go first, or skip their turn


## How to Play
1. **Select a Tile**: Click on a tile in your rack to select it
2. **Place a Word**: Click on board squares to place tiles in a straight line
3. **Submit Move**: Press "Play" to submit your word for validation
4. **Skip Turn**: Press "Skip" if no valid moves are available
5. **Score**: Points are calculated automatically based on letter values and premium squares


## Limitations
- Some edge cases in larger boards
  may not always produce the optimal move
- Solver prioritizes correctness for most cases
- CPU player sometimes plays illegal side by side words
however, words are scored correctly and legal in the dictionary
