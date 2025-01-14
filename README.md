# Java Chess Game - Human vs AI
Written in pure Java, this personal project was developed to deepen my understanding of core coding concepts such as OOP and SOLID principles but also serve as an introductory exploration of more advanced topics I'm interested in, such as game theory and AI. Keep reading to learn more about the implementation, or run the game to enjoy a classic game of chess!

![Chess](res/ChessScreenshot.png)

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [The GUI](#the-gui)
- [The AI Engine](#the-ai-engine)
- [Further Improvements](#further-improvements)

## Prerequisites
The following prerequisites are necessary for installation:

- Java Development Kit (JDK) 8 or higher
- Git (optional, for cloning the repository)

## Installation

1. Open a terminal and run:

   ```bash
   git clone https://github.com/eimaneltigani/JavaChessGame.git
   ```
2. Navigate to the project direction
2. Compile the Java source files:

   ```bash
   javac src/**/*.java
   ```
3. Run the game:
    ```bash
   java -cp out Main
   ```
Feel free to change the level of difficulty of the game)

## The GUI
Built using Java Swing with simple and easy-to-use features:
* Available moves are highlighted after user-click
* Click functionality only enabled for legal moves, ensuring game accuracy
* Side panel highlights current players turn and displays captured pieces for both teams
* Supports advanced moves such as castling, pawn promotions, etc.
* Warning when King is in check

## The AI Engine
The Computer uses a decision-making algorithm used in two-player games called Minimax.
This recursive algorithm evaluates all possible moves of a player and returns the best move based off the potential outcome of the board and its corresponding score. 
Based off the assumption the opponent will play their most optimal move (providing the worst score for the player), the search will return the maximum or minimum score based off who's turn it is throughout the game tree.

### Performance
Considering the large branching factor (~35) estimated for chess, a popular optimization technique called Alpha-Beta pruning was implemented to reduce computation time. This technique works by pruning nodes that are guaranteed to lead to a worst outcome compared to our previous positions.
For example, consider the diagram below:

![Pruning example](https://media.geeksforgeeks.org/wp-content/uploads/MIN_MAX2.jpg)

After returning the outcomes for each move at the base of the tree, it is now the Maximizing players turn to pick the best one. For Node D, they will choose the highest score of 5. For Node E, the first branch guarantees a score >= 6. Despite the potential for an even higher outcome with the other branch, we know parent node B will choose lowest value D regardless. As a result, no further search is necessary!

| Depth: 3       | MiniMax   | + Alpha-Beta |
|----------------|-----------|--------------|
| Nodes reached: | 11,513    | 6,322        |
| Time elapsed:  | 995.73 ms | 421.19 ms    |

Though I implemented only one of many optimization techniques, the above comparison shows the significant difference with just one technique!

### Evaluation
A simple evaluation of the board is measured using these considerations:
* Material (sum of piece values of each side)
* Positional (number of moves compared to opponent)

## Further improvements
Here are some ideas I took note of that would significantly advance the game significantly:  
* Performance
  * Using bitboard to represent the board, rather than classes (performance vs readability)
  * Multi-threading to compute branches in parallel
  * Using a hashmap to store previous boards
* Evaluation
  * The concept of early game, middle game, end game. This interested me because I feel it would better replicate how humans play.


### Helpful Resources
_________
1. For getting started, this [Chess Programming Wiki](https://www.chessprogramming.org/Main_Page) was a great resource on the basics of building a chess engine. 
2. For specific questions or comments regarding this project, feel free to reach out to me:
   eltiganieiman@gmail.com