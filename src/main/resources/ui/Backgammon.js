var backgammonBoard, dimensions;
var innerColorOfBoard, borderColorOfBoard;
var colorOfFirstTriangles, triangleBorderColor;
var colorOfWhiteCheckers, colorOfBlackCheckers;
var mousePositions;
var gameNo;
var trianglePaths = new Array();
var checkerPaths = new Map();
var blackCheckers = new Map();
var whiteCheckers = new Map();
var blackBarCheckers = new Map();
var whiteBarCheckers = new Map();
var whiteBearCheckers = new Map();
var blackBearCheckers = new Map();
var toTriangle = null;
var fromTriangle = null;
var assignedTurns = false;
var rollingCount = 0;
var currentPlayerColour = null;

/**
 * @author Group 4.
 */
(function() {

    window.onload = initializeGame;

    /*
     * function that is initialised on loading the window.
     * and executes onClick events.
     */
    function initializeGame() {
        var ngButton = document.getElementById('start_game');
        ngButton.onclick = newGame
        ngButton = document.getElementById("roll_left_dice");
        ngButton.onclick = rollDice
    }

    /*
     * function that interacts with the api and the calls setUpBoard().
     */
    function newGame() {
        var opts = {
            methods: 'GET',
            headers: {
                'Content-Type': 'application/json',
                "Accept": "application/json"
            }
        };
        fetch('/api/Backgammon/newGame', opts).then(setupBoard)
    }

    /*
     * function that takes the response in the form of Json and setups the board.
     */
    async function setupBoard(response) {
        var json = await response.json();
        console.log(json)
        gameNo = json.gameNo;
        document.getElementById("start_game").style.visibility = "hidden";
        document.getElementById("left-dice").style.visibility = "visible";
        backgammonBoard = document.getElementById("BackgammonBoard");
        dimensions = backgammonBoard.getContext('2d');

        extractDataFromJson(json, 1);
        createBoard();
        backgammonBoard.addEventListener("click", function(evt) {
            mousePositions = getMousePositions(backgammonBoard, evt);
            if (fromTriangle == null && toTriangle == null) {
                fromTriangle = findCheckerPosition(mousePositions);
            } else if (fromTriangle != null & toTriangle == null) {
                if (findCheckerPosition(mousePositions) != null) {
                    toTriangle = findCheckerPosition(mousePositions);
                } else {
                    toTriangle = findTrianglePosition(mousePositions);
                }

                playTurn(fromTriangle, toTriangle);

            }
        });
    }

    /*
     * function that interacts with the api and calls the method that updates the board.
     */
    function playTurn(x, y) {
        if (x !== null && y !== null && currentPlayerColour != null) {
            var opts = {
                method: 'POST',
                headers: { 'ACCEPT': "application/json" }
            };
            fetch("/api/Backgammon/" + gameNo + "/playTurn/" + x + "/" + y, opts).then(updateBoard)
        }
    }

    /**
     * function that updates the board.
     */
    async function updateBoard(response) {
        //Get data from the json file and update checkerMaps
        var json = await response.json();
        console.log(json);
        extractDataFromJson(json, 2);

        reDrawCanvas();
    }

    /*
     * helper method for updating and redrawing canvas.
     */
    function reDrawCanvas() {
        //Clearing the canvas
        dimensions.clearRect(0, 0, backgammonBoard.width, backgammonBoard.height);
        dimensions.beginPath();
        checkerPaths = new Map();
        trianglePaths = new Array();
        drawEmptyBoard();
        drawRectangles();
        drawTriangles();
        drawCheckersOnBoard(blackCheckers, colorOfBlackCheckers);
        drawCheckersOnBoard(whiteCheckers, colorOfWhiteCheckers);
        drawCheckersOnTheBar(blackBarCheckers, colorOfBlackCheckers, blackBarCheckers.get(25));
        drawCheckersOnTheBar(whiteBarCheckers, colorOfWhiteCheckers, whiteBarCheckers.get(28));
        drawCheckersOnBearOff(blackBearCheckers, colorOfBlackCheckers, blackBearCheckers.get(26));
        drawCheckersOnBearOff(whiteBearCheckers, colorOfWhiteCheckers, whiteBearCheckers.get(27));
        fromTriangle = null;
        toTriangle = null;
    }

    /*
     * function that is called when the player clicks rollDice button.
     *this method interacts with the api and calls the methods that values numbers on the dice.
     */
    function rollDice() {
        if (!assignedTurns && rollingCount == 0) {
            rollingCount++;
            var opts = {
                method: 'GET',
                headers: { 'ACCEPT': "Application/json" }
            };
            fetch("/api/Backgammon/" + gameNo + "/rollDice", opts).then(displayNumberOnDiceOne);
        } else if (!assignedTurns && rollingCount == 1) {
            rollingCount++;
            var opts = {
                method: 'GET',
                headers: { 'ACCEPT': "Application/json" }
            };
            fetch("/api/Backgammon/" + gameNo + "/rollDice", opts).then(displayNumberOnDiceTwo);
        } else {
            var opts = {
                method: 'GET',
                headers: { 'ACCEPT': "Application/json" }
            };
            fetch("/api/Backgammon/" + gameNo + "/rollDice", opts).then(displayNumberOnDice);
        }
    }

    /*
     * function that displays value only on the first dice.
     */
    async function displayNumberOnDiceOne(response) {
        var json = await response.json();
        dice1Value = json.dice1Value;
        document.getElementById("dice1").innerText = dice1Value;
        document.getElementById("dice2").innerText = " ";
    }

    /*
     * function that displays value only on the second dice.
     */
    async function displayNumberOnDiceTwo(response) {
        var json = await response.json();
        console.log(json);
        dice2Value = json.dice2Value;
        document.getElementById("dice2").innerText = dice2Value;
        currentPlayerColour = json.currentPlayer;
        if (currentPlayerColour == "null") {
            rollingCount = 0;
            assignedTurns = false;
        } else {
            assignedTurns = true;
        }
    }

    /*
     * function that displays value on both the dice.
     */
    async function displayNumberOnDice(response) {
        var json = await response.json();
        dice1Value = json.dice1Value;
        dice2Value = json.dice2Value;
        document.getElementById("dice1").innerText = dice1Value;
        document.getElementById("dice2").innerText = dice2Value;
    }

    /*
     * function the draws the layout of the board on the canvas.
     */
    function createBoard() {
        let width = 800;
        let height = 600;
        backgammonBoard.width = width;
        backgammonBoard.height = height;
        colorSettingOfBoard();
        colorSettingOfTriangles();
        colorSettingOfCheckers();
        drawEmptyBoard();
        placeCheckersAtInitialPositions();
    }

    /*
     * function that places the checkers at initial positions on the board.
     */
    function placeCheckersAtInitialPositions() {
        drawCheckersOnBoard(blackCheckers, colorOfBlackCheckers);
        drawCheckersOnBoard(whiteCheckers, colorOfWhiteCheckers);
    }

    /*
     * function that draws empty board with out any components.
     * and calls the method that draws the rectangles
     */
    function drawEmptyBoard() {
        dimensions.fillStyle = borderColorOfBoard;
        dimensions.fillRect(0, 0, backgammonBoard.width, backgammonBoard.height);
        dimensions.restore();
        drawRectangles();
    }

    /*
     * function that sets colors of the board.
     */
    function colorSettingOfBoard() {
        borderColorOfBoard = "#663300";
        innerColorOfBoard = "#708090";
    }

    /*
     * function that sets colors of the traingles.
     */
    function colorSettingOfTriangles() {
        colorOfFirstTriangles = "#CD853F";
        triangleBorderColor = "#444";
    }

    /*
     * function that sets colors of the checkers.
     */
    function colorSettingOfCheckers() {
        colorOfBlackCheckers = "#000000";
        colorOfWhiteCheckers = "#FFFFFF";
    }

    /*
     * function that draws all the rectangles on the board.
     */
    function drawRectangles() {
        dimensions.fillStyle = innerColorOfBoard;
        dimensions.fillRect(16, 24, 48, 240);
        dimensions.fillRect(16, 336, 48, 240);
        dimensions.fillRect(736, 24, 48, 240);
        dimensions.fillRect(736, 336, 48, 240);
        dimensions.fillRect(80, 24, 288, 552);
        dimensions.fillRect(432, 24, 288, 552);
        dimensions.restore();
        drawTriangles();
    }

    /*
     * function that draws all the triangles on the board.
     */
    function drawTriangles() {
        dimensions.fillStyle = colorOfFirstTriangles;

        //bottom right
        let length = 330;
        let direction = -1;
        let y = 575;
        let x = 720;
        for (i = 0; i < 6; i++) {
            drawTriangle();
        }

        //Bottom left
        length = 330;
        x = 368;
        for (i = 0; i < 6; i++) {
            drawTriangle();
        }

        //Top left
        x = 80;
        y = 25;
        length = 270;
        direction = 1;
        for (i = 0; i < 6; i++) {
            drawTriangle();
        }

        //top right
        x = 432;
        for (i = 0; i < 6; i++) {
            drawTriangle();
        }
        dimensions.fill();

        function drawTriangle() {
            let path = new Path2D();
            path.moveTo(x, y);
            path.lineTo(x + (direction * 48), y);
            path.lineTo(x + (direction * 28), length);
            trianglePaths.push(path);
            dimensions.fill(path);
            x = x + (direction * 48);
        }
    }

    /*
     * function that draws all the checkers on the board by calling the method that draws individual checkers.
     */
    function drawCheckersOnBoard(checkers, color) {

        for (const [key, value] of checkers.entries()) {
            let x = 696;
            let y = 554;
            let padding = key - 1;
            let direction = -1;
            let paddingForBar = 0;

            if (key > 12) {
                direction = 1;
                x = 105;
                y = 45;
                padding = padding - 12;
            }

            if (key > 6 && key <= 12 || key > 18)
                paddingForBar = 65;

            for (i = 0; i < value; i++) {
                drawCheckerOnBoard(x + (48 * padding * direction) + (paddingForBar * direction), y + (48 * i * direction), color, key);
            }
        }
    }

    /*
     * function that draws individual checker on the board.
     */
    function drawCheckerOnBoard(x, y, color, key) {

        const radius = 24;
        let path = new Path2D();
        path.arc(x, y, radius * 0.99, 0, 2 * Math.PI);
        dimensions.fillStyle = color;
        checkerPaths.set(key, path); // each time we add a new checker on an already occupied triangle this line replaces the previouse path. So only the top checker will be identified.
        dimensions.fill(path);
    }

    /*
     * function that gets mouse positions when the player clicks.
     */
    function getMousePositions(backgammonBoard, evt) {
        // makeMove();
        var rect = backgammonBoard.getBoundingClientRect();
        return {
            x: evt.clientX - rect.left,
            y: evt.clientY - rect.top
        };
    }

    /*
     * function that find the traingle number on which the player clicks based on the mousepositions.
     */
    function findTrianglePosition(mousePositions) {
        //Catch exception because when there is no path on that point dimensions cannot call the isPointInPath method
        try {
            for (var i = 0; i <= trianglePaths.length; i++) {
                if (dimensions.isPointInPath(trianglePaths[i], mousePositions.x, mousePositions.y)) {
                    return i + 1;
                }
            }
        } catch (e) {
            return null;
        }
    }

    /*
     * function that identifies the position of triangle on which the checker is clicked.
     */
    function findCheckerPosition(mousePositions) {
        try {
            for (const [key, value] of checkerPaths.entries()) {
                if (dimensions.isPointInPath(value, mousePositions.x, mousePositions.y)) {
                    return key;
                }
            }
        } catch (e) {
            return null;
        }
    }

    /*
     * function that displays checkers on the bar.
     */
    function drawCheckersOnTheBar(checkerMap, color, key) {
        if (color == colorOfBlackCheckers && checkerMap.size > 0) {
            drawCheckerOnBoard(400, 200, color, 25);
            if (key != 0) {
                dimensions.font = "60px Arial";
                dimensions.fillStyle = color;
                dimensions.textAlign = "center";
                dimensions.fillText(key, 400, 150);
            }
        } else if (color === colorOfWhiteCheckers && checkerMap.size > 0) {
            drawCheckerOnBoard(400, 400, color, 28);
            if (key != 0) {
                dimensions.font = "60px Arial";
                dimensions.fillStyle = "#FFFFFF";
                dimensions.textAlign = "center";
                dimensions.fillText(key, 400, 500);
            }
        }
    }

    /*
     * function that displays checkers on the bearoff rectangles.
     */
    function drawCheckersOnBearOff(checkerMap, color, key) {
        if (color == colorOfBlackCheckers && checkerMap.size > 0) {
            drawCheckerOnBoard(750, 200, color, 26);
            if (key != 0) {
                dimensions.font = "60px Arial";
                dimensions.fillStyle = color;
                dimensions.textAlign = "center";
                dimensions.fillText(key, 750, 150);
            }
        } else if (color == colorOfBlackCheckers && checkerMap.size > 0) {
            drawCheckerOnBoard(750, 200, color, 27);
            if (key != 0) {
                dimensions.font = "60px Arial";
                dimensions.fillStyle = color;
                dimensions.textAlign = "center";
                dimensions.fillText(key, 750, 450);
            }
        }
    }

    /*
     * function that extracts data from the json retrived as response.
     */
    function extractDataFromJson(json, jsonId) {
        checkerPaths = new Map();
        blackCheckers = new Map();
        whiteCheckers = new Map();
        blackBarCheckers = new Map();
        whiteBarCheckers = new Map();
        whiteBearCheckers = new Map();
        blackBearCheckers = new Map();
        var count = 0;
        var rowCounter = 1;
        var myJson;
        if (jsonId === 1) {
            myJson = json.game.board;
        } else
            myJson = json.board;
        for (var y in myJson) {
            var row = myJson[y];
            var color = row[0];
            for (var x in row) {
                if (row[x] == "B" || row[x] == "W") {
                    count += 1;

                }
                if (rowCounter == 25 && color == "B") { // Bar
                    blackBarCheckers.set(25, count);
                    reDrawCanvas();

                } else if (rowCounter == 28 && color == "W") {
                    whiteBarCheckers.set(28, count);
                    reDrawCanvas();

                } else if (rowCounter == 26 && color == "B") {
                    blackBearCheckers.set(26, count);
                    reDrawCanvas();


                } else if (rowCounter == 27 && color == "B") {
                    whiteBearCheckers.set(27, count);
                    reDrawCanvas();

                } else if (color == "B") {
                    blackCheckers.set(rowCounter, count);
                } else if (color == "W") {
                    whiteCheckers.set(rowCounter, count);
                }
            }
            count = 0;
            rowCounter += 1;
        }
    }

})();