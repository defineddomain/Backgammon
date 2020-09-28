
# Backgammon Group 4 
API
An API was developed that communicates between the BackgammonModel and the grapical user interface (GUI). The GUI was 
created in Javascript and so gives a full graphical representation of the game and requires no use of the command line to
play. So any commands to interact with the API are not mentioned.
The GUI is made in javascript. The model is BackgammonModel. The API is BackgammonResources, and connects the model 
with the GUI.
Currently the game is run using the lab machines by importing the gradle project into IntelliJ, making sure the Main
class is selected the then running the Main file for the server.


Api in the current file is BackgammonResources. “/api/Backgammon” is the path to interact with the api and other methods 
in the api have different paths which are in the form “/api/Backgammon/{}/{}/{}”

Backgammon.js fetches the responses in the form of JSON (JavaScript Object Notation) and updates the board every time it 
receives a response. 

JSON representation of initial state of the board is as follows:
gameNo: 1
url: "http://localhost:8080/api/backgammon/1"
game:
height: 28
dice1Value: 0
dice2Value: 0
currentPlayer: "null"
board: Array(28)
0: (15) ["B", "B", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
1: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
2: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
3: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
4: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
5: (15) ["W", "W", "W", "W", "W", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
6: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
7: (15) ["W", "W", "W", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
8: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
9: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
10: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
11: (15) ["B", "B", "B", "B", "B", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
12: (15) ["W", "W", "W", "W", "W", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
13: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
14: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
15: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
16: (15) ["B", "B", "B", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
17: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
18: (15) ["B", "B", "B", "B", "B", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
19: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
20: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
21: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
22: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
23: (15) ["W", "W", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
24: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
25: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
26: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
27: (15) ["NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL"]
length: 28

Instructions To Play:

1. Start the server in the gradle project (ensuring you have the latest version of gradle, the server file is Main.java
2. Ensure a web browser is currently open and in the URL bar type http://localhost:8080
3. From the browser you will be presented with a screen showing the button "Start Game", ensure you are in the right URL and the server is running in order to see the start button.
4. Click the "Start Game" button to start a new game.
5. A backgammon board game will appear along with two squares representing the dice and a "Roll Dice” button.

Playing the Game
To begin the game click the "Roll Dice" button, only one dice will be rolled and this is the top one for the black player, click the button again for a second dice throw for the white player, the highest score will indicate which player(black or white) goes first.


The dice cannot be rolled again until a player makes their turn.

When making a move you must click on the piece you want to move and then click on the corresponding triangle you want to move it to. There are no graphical indications what triangle you will be clicking in, so ensure you are precise with your turns. If you click on a playing piece and then click on a piece where you cannot move to, you will not be able to make the turn and the move is reset. You will then need to click on the piece again.
When moving a piece and there is a stack of pieces in that location, you can only move the piece on the stack that is closest to the center of the board (Only the top pieces are moveable).
Once you have made your turn you will not be able to move again any further.
The next player will then need to click the “Roll Dice” button before they can play

To capture a piece, make a move as normal by clicking the piece you want to move and then click the location you want to move to. If there is a lone piece of the opposite colour you can capture this piece.
The captured piece will then appear in the bar in the centre of the screen, the number and colour of captured pieces will be shown in the centre bar. 


If you have a captured piece you cannot move any pieces until the capture piece is returned to your opponents home area.



To move your piece from the bar to the opponents home area, click the piece in the bar and then select a place in the  opponents home position, this being location dependent on the roll of the dice. 

The moves will continue until all of your pieces are in the home position.

Once the pieces are in bearing off position, the moves to bear off no longer function.


