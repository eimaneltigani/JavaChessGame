# Java Chess Game - Human vs AI

![Chess](res/ChessScreenshot.png)

## The GUI
Built using Java Swing with simple and easy-to-use features:
* Available moves are highlighted after user-click
* Click functionality is only enabled for legal moves, ensuring game accuracy
* Side panel highlights current players turn and displays captured pieces are displayed for both teams
* Supports castling, promotional etc.
* Warning when King is in check

## The AI Engine
The Computer Player uses a decision-making algorithm used in two-player games called Minimax.
The algorithm evaluates all possible moves of a player and returns the best move based off the potential outcome of the board and its corresponding score. 
Based off the assumption the opponent will play their most optimal move (providing the worst score for the player), the search will return the maximum or minimum score based off who's turn it is throughout the game tree.

## Performance optimization
Considering the large branching factor (~35) estimated for chess, Alpha-Beta pruning was implemented to reduce computation time. This technique works by pruning nodes that are guaranteed to lead to a worst outcome compared to our previous positions.
For example, consider the diagram below:

![Pruning example](https://media.geeksforgeeks.org/wp-content/uploads/MIN_MAX2.jpg)

After returning all the possible outcomes at the base of the tree, it is now the Maximizing players turn to pick their move. For Node D, they will choose the highest score of 5. For Node E, the first branch guarantees a score >= 6. Despite having a higher outcome, we know parent node B will choose the lowest value D regardless. As a result, no further search is necessary!

| Depth: 3       | MiniMax   | + Alpha-Beta |
|----------------|-----------|--------------|
| Nodes reached: | 11,513    | 6,322        |
| Time elapsed:  | 995.73 ms | 421.19 ms    |

Though I implemented only one of many optimization techniques, the above comparison shows the significant difference with just one technique! 