import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.text.Text;
import javax.swing.JOptionPane;

import java.util.Scanner;

public class SinglePlayerGUI extends Application
{
	SinglePlayer singlePlayer = new SinglePlayer();
		
	//CONSTANTS
	final int shipsRequired = 4;	
	final int margin = 110; //Not cell units, instead flat pixel number
	final int boardSize = 400; //pixels
	final int wWidth = boardSize * 2 + margin;
	final int wHeight = boardSize + margin;
	
	//Adjusted board variables
	int boardUnits = 8; //Set to 8 as default
	private int variableCellSize;

	//Variables
	private boolean playerShipPlacementNotOver=true;
	private boolean shotTaken = false;
	private boolean playerFiring = false;
	private boolean gameOver = false;
	private int computerShipsSunk = 0;
	private int playerShipsSunk = 0;
	private String playerName = singlePlayer.player.getName();
	
	//JavaFX Init GUI Fundamentals
	Group root; 
	Scene scene;
	Canvas canvas;
	GraphicsContext gc;
	
	VBox vBox;
	Label stats = new Label();
	Label shipsSunkLabel = new Label();
	Label shipsSunkLabel2 = new Label();
	Label playerNameLabel = new Label(playerName);
	
	//Creates Buttons
	Button p1b1;
	Button p1b2;
	Button p1b3;
	Button p1b4;
	Button p1b5;
	Button p1b6;
	
	//Player Name
	String p1Name;
	String p2Name = "Computer";
	
	public static void main(String[] args)
	{
		launch(args); 
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		singlePlayer.setBoard(boardUnits);
		singlePlayer.player.setName(p1Name);
		singlePlayer.computer.setName(p2Name);
		
		singlePlayer.player.setBoardLength(boardUnits-1);
		singlePlayer.computer.setBoardLength(boardUnits-1);
		
		
		singlePlayer.singlePlayerGame(4);
		//Creates cell size
		variableCellSize = (int)(boardSize/boardUnits);
		
		primaryStage.setTitle("Battleship");
		createScene(primaryStage);
		primaryStage.setScene(scene);

		primaryStage.show();
	}
	
	public void createScene(Stage primaryStage)
	{
		//Scene 1
		root = new Group();
		scene = new Scene(root);
		canvas = new Canvas(wWidth, wHeight);
		gc = canvas.getGraphicsContext2D();
		canvas.setMouseTransparent(true);
						
		vBox = new VBox();
		stats = new Label();
		shipsSunkLabel = new Label(); //Not added yet
				
		stats.setLayoutX(boardSize);
		root.getChildren().add(stats);
				
		shipsSunkLabel.setLayoutX(boardSize);

		p1b1 = new Button("Done");
		p1b2 = new Button("Battleship");
		p1b3 = new Button("Submarine");
		p1b4 = new Button("Destroyer");
		p1b5 = new Button("Patrol Boat");
		p1b6 = new Button("Rotate");
						
		//Sets minimum button width
		vBox.setPrefWidth(75);
						
		//Draws Player Text
		gc.setStroke(Color.BLACK);
		gc.strokeText(p1Name, 0, 10);
		gc.strokeText(p2Name, boardSize + margin, 10);
						
		//Sets Positions
		p1b1.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b1.setLayoutY(wHeight - 50);
		p1b2.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b2.setLayoutY(wHeight - 2*50);
		p1b3.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b3.setLayoutY(wHeight - 3*50);
		p1b4.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b4.setLayoutY(wHeight - 4*50);
		p1b5.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b5.setLayoutY(wHeight - 5*50);
		p1b6.setLayoutX((int)(wWidth/2) - (int)(vBox.getPrefWidth()/2));
		p1b6.setLayoutY(wHeight - 6*50);
						
		//Sets Minimum size for consistency
		p1b1.setMinWidth(vBox.getPrefWidth());
		p1b2.setMinWidth(vBox.getPrefWidth());
		p1b3.setMinWidth(vBox.getPrefWidth());
		p1b4.setMinWidth(vBox.getPrefWidth());
		p1b5.setMinWidth(vBox.getPrefWidth());
		p1b6.setMinWidth(vBox.getPrefWidth());
					
		//Draws Boards and grid-lines
		drawBoard();
		root.getChildren().add(canvas);	

		//Adds Buttons
		root.getChildren().add(p1b2);
		root.getChildren().add(p1b3);
		root.getChildren().add(p1b4);
		root.getChildren().add(p1b5);
		root.getChildren().add(p1b6);
		
		//Scene 1 event handles
		
		//Handle done button
		p1b1.setOnAction(e -> handleDoneButton());
								
		//Handle Ship Buttons
		p1b2.setOnAction(e -> handleShipButton(singlePlayer.player.battleship, singlePlayer.player));
		p1b3.setOnAction(e -> handleShipButton(singlePlayer.player.submarine, singlePlayer.player));
		p1b4.setOnAction(e -> handleShipButton(singlePlayer.player.destroyer, singlePlayer.player));
		p1b5.setOnAction(e -> handleShipButton(singlePlayer.player.patrolBoat, singlePlayer.player));
				
		//Handle rotate button
		p1b6.setOnAction(e -> handleRotateButton(singlePlayer.player));
				
		//Mouse event handler
		scene.setOnMouseClicked(e -> handleMouseClick(singlePlayer.player, e, primaryStage));
		
	}
	
	
	public void winnerIsDetermined(Stage primaryStage)
	{
		if (gameOver == true)
		{
			Scanner input = new Scanner(System.in);
			String line = input.nextLine();
			primaryStage.close();
		}
	}
	
	public void handleShipButton(Ship ship, Player player)
	{
		System.out.println("Place " + ship.getName() + "\n");
		player.current = ship;
		//twoPlayer.player1.getStats();
		stats.setText(player.getStats());
	}
	
	//Handle rotate button
	public void handleRotateButton(Player player)
	{
		if(player.current.getName() == null)
		{
			//System.out.println("Please select a ship first.");
		}
		else
		{
			System.out.println("Rotated Ship\n");
			player.rotate();
			//twoPlayer.player1.getStats();
			stats.setText(player.getStats());
		}
	}
	
	//Handle done button
	public void handleDoneButton()
	{	
		root.getChildren().remove(p1b6);
		root.getChildren().remove(p1b1);
		root.getChildren().remove(stats);
		
		singlePlayer.shipPlacer(singlePlayer.computer.battleship);
		singlePlayer.shipPlacer(singlePlayer.computer.submarine);
		singlePlayer.shipPlacer(singlePlayer.computer.destroyer);
		singlePlayer.shipPlacer(singlePlayer.computer.patrolBoat);
		
		singlePlayer.boardLinking();
		
		playerShipPlacementNotOver = false;
	}
	
	//Handle mouse clicks
	public void handleMouseClick(Player player, MouseEvent event, Stage primaryStage)
	{
		//If still placing ships
		if(playerShipPlacementNotOver)
		{			
			if(player.current.getName() == null)
			{
				System.out.println("Please select a ship first.");
			}
			else
			{
				//System.out.println("X: " + (int)(event.getX()/cellSize) + ", Y: " + ((int)(event.getY()/cellSize)-2));
				
				player.setXPos((int)(event.getX()/variableCellSize));
				if(event.getY() <= margin + (boardSize-variableCellSize*boardUnits))//If click is above board
				{
					player.setYPos(-1);//Just an invalid pos
				}
				else 
				{
					player.setYPos((int)((event.getY()-margin-(boardSize-variableCellSize*boardUnits))/variableCellSize));
				}
				
				boolean validPlacement = player.validShipPlacement(player.current);
				
				if(validPlacement)
				{
					//Updates GUI on click
					if(player.current.getVerticalOrientation())//Ship vertical
					{
						int[] y = player.current.getYPositions();
						int x = player.current.getXPos();
						
						int x1 = variableCellSize*x;
						int y1 = variableCellSize*y[0] + margin + (boardSize-variableCellSize*boardUnits);
						int w = variableCellSize;
						int h = variableCellSize*player.current.getLength();
									
						gc.fillRect(x1,y1,w,h);
					}
					else //Ship horizontal
					{
						int[] x = player.current.getXPositions();
						int y = player.current.getYPos();
						
						int x1 = variableCellSize*x[0];
						int y1 = variableCellSize*y + margin + (boardSize-variableCellSize*boardUnits);
						int w = variableCellSize*player.current.getLength();
						int h = variableCellSize;

						gc.fillRect(x1,y1,w,h);
					}
					
					player.shipPlacement(player.current);
					singlePlayer.player.setXPos(0);
					singlePlayer.player.setYPos(0);
					
					if(player.current.getName() == "Battleship")
					{
						root.getChildren().remove(p1b2);
					}
					else if(player.current.getName() == "Submarine")
					{
						root.getChildren().remove(p1b3);
					}
					else if(player.current.getName() == "Destroyer")
					{
						root.getChildren().remove(p1b4);
					}
					else
					{
						root.getChildren().remove(p1b5);
					}	
					
					//Adds done button once all ships are placed, and removes rotate button
					if(player.shipsPlaced.size() == shipsRequired)
					{
						root.getChildren().add(p1b1);
						root.getChildren().remove(p1b6);
					}
					
					player.current = player.nullShip; //Null ship makes it so player must choose a new ship in between ship placements 
					stats.setText(player.getStats());

				}
			}
		}
		//No longer placing ships, now attacking phase
		else
		{
			int x;
			int y;
			
			x = boardUnits-1 - (int)((wWidth - event.getX())/variableCellSize ); //boardUnits-1 to convert to index in array
			if(event.getY() <= margin + (boardSize-variableCellSize*boardUnits))//If click is above board
			{
				y = -1; //Just an invalid position so no action taken
			}
			else 
			{
				y = (int)((event.getY()-margin-(boardSize-variableCellSize*boardUnits))/variableCellSize);
			}
			
			System.out.println("x: "+ x + " y: " + y);
			
			int xl = wWidth - (boardUnits-x)*variableCellSize;
			int yl = (y*variableCellSize)+margin+(boardSize-variableCellSize*boardUnits);
			
			int w = variableCellSize;
			
			if((x>=0 && y>=0 ) && (x<=boardUnits-1 && y<=boardUnits-1) && player.enemyBoard.grid[x][y].getBeenHit())
			{
				System.out.println("Already shot that spot.");
			}
			if ((x>=0 && y>=0) && (x<=boardUnits-1 && y<=boardUnits-1) && !shotTaken && !player.enemyBoard.grid[x][y].getBeenHit())
			{
				player.enemyBoard.grid[x][y].setBeenHit(true);
				if (singlePlayer.player.enemyBoard.grid[x][y].getHasShip())// check if grid starts at 1 or 0
				{
					System.out.println("hit ship");
					gc.setFill(Color.RED);
					gc.fillRect(xl,yl,w,w);
						
					if(singlePlayer.computer.shipChecker(x, y))
					{
						computerShipsSunk++;	
						shipsSunkLabel.setText("Number of computer ships sunk = " + computerShipsSunk);
						if (playerShipsSunk == 4 || computerShipsSunk == 4)
						{
							gameOver = true;
							System.out.println("GAME OVER: PLAYER WINS");
						}
						winnerIsDetermined(primaryStage);
					}
						
				}
				else //Hit empty space
				{
					gc.setFill(Color.BLUE);
					gc.fillRect(xl,yl,w,w);
				}
				
				shotTaken = true;
				root.getChildren().add(p1b1);
				singlePlayer.boardLinking(); 
			}
			
			primaryStage.show();
			
			//AI shooting
			singlePlayer.computer.shipFire();
			singlePlayer.computer.enemyBoard.grid[singlePlayer.computer.getXPos()][singlePlayer.computer.getYPos()].setBeenHit(true);
			
			int xc = singlePlayer.computer.getXPos()*variableCellSize;
			int yc = singlePlayer.computer.getYPos()*variableCellSize+margin+(boardSize-variableCellSize*boardUnits);
			
			if (singlePlayer.computer.enemyBoard.grid[singlePlayer.computer.getXPos()][singlePlayer.computer.getYPos()].getHasShip())
			{
				gc.setFill(Color.RED);
				gc.fillRect(xc,yc,variableCellSize,variableCellSize);
				if(singlePlayer.player.shipChecker(singlePlayer.computer.getXPos(), singlePlayer.computer.getYPos()))
				{
						playerShipsSunk++;	
						shipsSunkLabel2.setText("Number of player ships sunk = "+ playerShipsSunk);
						if (playerShipsSunk == 4 || computerShipsSunk == 4)
						{
							gameOver = true;
							System.out.println("GAME OVER: CPU WINS");
						}
						winnerIsDetermined(primaryStage);
				}
			}
			else
			{
				gc.setFill(Color.GRAY);
				gc.fillRect(xc,yc,variableCellSize,variableCellSize);				
			}
		}					
	}
	
	//Draws the main frame of the screen
	public void drawBoard()
	{
		/*
		//Used only for testing purposes, borders max board size and used to compensate for remainder
		gc.strokeLine(0, margin, wWidth, margin);
		gc.strokeLine(boardSize, 0, boardSize , wHeight);
		gc.strokeLine(boardSize + margin, 0, boardSize + margin, wHeight);
		*/

		//Draws Grids, and creates number intervals
		for(int i = 0; i < boardUnits+1; i++)
		{
			//Left grid
			gc.strokeLine(0, wHeight-(i*variableCellSize), (boardUnits*variableCellSize), wHeight-(i*variableCellSize));
			gc.strokeLine(i*variableCellSize, wHeight, i*variableCellSize, wHeight-(boardUnits*variableCellSize));
			
			//Right grid
			gc.strokeLine(wWidth, wHeight-(i*variableCellSize), wWidth-(boardUnits*variableCellSize), wHeight-(i*variableCellSize));
			gc.strokeLine(wWidth-(i*variableCellSize), wHeight, wWidth-(i*variableCellSize), wHeight-(boardUnits*variableCellSize));	
			
			//Because Intervals from 1-n, not n+1 
			if(i > 0)
			{
				String interval = Integer.toString(i);
				
				//Horizontal intervals
				gc.strokeText(interval, i*variableCellSize-(int)(variableCellSize/2)-5, wHeight - (boardUnits*variableCellSize) - 2);
				gc.strokeText(interval, wWidth-(boardUnits*variableCellSize) + i*variableCellSize-(int)(variableCellSize/2)-5, wHeight - (boardUnits*variableCellSize) - 2);
				
				//Vertical intervals
				if(i > 9)
				{
					gc.strokeText(interval, wWidth-(boardUnits*variableCellSize)-15, margin+i*variableCellSize-(int)(variableCellSize/2)+(boardSize-boardUnits*variableCellSize) + 5);

				}
				else 
				{
					gc.strokeText(interval, wWidth-(boardUnits*variableCellSize)-10, margin+i*variableCellSize-(int)(variableCellSize/2)+(boardSize-boardUnits*variableCellSize) + 5);

				}
				gc.strokeText(interval, (boardUnits*variableCellSize)+2, margin+i*variableCellSize-(int)(variableCellSize/2)+(boardSize-boardUnits*variableCellSize) + 5);
			}	
		}
	}
}
