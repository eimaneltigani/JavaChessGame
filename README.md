# Java Chess Game - Human vs AI

## The GUI
Built using Java Swing with simple and easy-to-use features:
* Available moves are highlighted after user-click
* Click functionality is only enabled for legal moves, ensuring game accuracy
* Player panel highlights current players turn
* Captured pieces are displayed for both teams
* User can execute castling and promotional moves

## The AI Engine
The Computer Player uses a decision-making algorithm used in two-player games called Minimax.
It is a recursive algorithm that evaluates possible moves and finds the best move based off the outcome and static evaluation of the board. 
Based off the assumption the opponent will play their most optimal move (providing the worst score for the player), the search will return the maximum or minimum score based off who's turn it is throughout the game tree.

## Performance optimization
Considering the large branching factor (~35) estimated for chess, Alpha-Beta pruning was implemented to reduce computation time. This technique works by pruning nodes that will lead to a worst outcome compared to previous positions.
For example, consider the diagram below:

![Pruning example](https://media.geeksforgeeks.org/wp-content/uploads/MIN_MAX2.jpg)
After returning all of the possible outcomes at the base of the tree, it is now the Maximizing players turn to pick their move. For Node D, they will choose the highest score of 5. For Node E, despite having a higher outcome, we know parent node B will choose the lowest value D regardless. As a result, no further search is necessary! 
