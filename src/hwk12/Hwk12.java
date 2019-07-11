package hwk12;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Hwk12 {
    public static void main(String[] args) {
        try {
            String l = UIManager.getCrossPlatformLookAndFeelClassName(); // also called `metal' look and feel
            UIManager.setLookAndFeel(l);
            new GameController();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

class GameController {

    public static GameModel currentGame;
    public static GameView gameView;
    private static ArrayList<Integer[]> winningIndexCombinations;

    GameController() {
        GameController.currentGame = new GameModel();
        GameController.gameView = new GameView();
        GameController.winningIndexCombinations = getWinningCombinations();
    }

    public static void resetGame() {
        GameController.currentGame.resetGameData();

        for (GameCell cell : GameView.gameCellList) {
            cell.unMarkCell();
            cell.removeHightLight();
        }

        gameView.enableRadioBtns();
    }

    private ArrayList<Integer[]> getWinningCombinations() {
        ArrayList<Integer[]> winCombinationsList = new ArrayList<>();

        winCombinationsList.add(new Integer[]{0, 1, 2});
        winCombinationsList.add(new Integer[]{3, 4, 5});
        winCombinationsList.add(new Integer[]{6, 7, 8});
        winCombinationsList.add(new Integer[]{0, 3, 6});
        winCombinationsList.add(new Integer[]{1, 4, 7});
        winCombinationsList.add(new Integer[]{2, 5, 8});
        winCombinationsList.add(new Integer[]{0, 4, 8});
        winCombinationsList.add(new Integer[]{2, 4, 6});

        return winCombinationsList;
    }

    public static void checkForWin() {

        // if number of steps is less than 5, no need to
        // check for win
        if (GameController.currentGame.getSteps() < 5) {
            return;
        }

        int[] grid = GameController.currentGame.getGrid();
        boolean hasWon = false;

        int num1, num2, num3;

        for (Integer[] arr : winningIndexCombinations) {

            num1 = grid[arr[0]];
            num2 = grid[arr[1]];
            num3 = grid[arr[2]];

            if (GameController.hasPlayerWon(num1, num2, num3)) {
                hasWon = true;
                GameController.highlightGameCells(arr[0], arr[1], arr[2]);
                break;
            }
        }

        if (hasWon) {
            GameController.gameWon();
        } else if (GameController.currentGame.getSteps() == 9) {
            GameController.gameDrawn();
        }
    }

    private static boolean hasPlayerWon(int num1, int num2, int num3) {
        return ((num1 + num2 + num3) == 3) || ((num1 + num2 + num3) == 0);
    }

    private static void highlightGameCells(int index1, int index2, int index3) {
        GameView.gameCellList.get(index1).highLightCell();
        GameView.gameCellList.get(index2).highLightCell();
        GameView.gameCellList.get(index3).highLightCell();
    }

    private static void gameWon() {
        GameController.currentGame.setGameOver(true);

        switch (GameController.currentGame.getCurrentPlayer()) {
            case "x":
                GameController.currentGame.incrementPlayerXWinCount();
                gameView.updatePlayerXWinCountLabel();
                break;
            case "o":
                GameController.currentGame.incrementPlayerOWinCount();
                gameView.updatePlayerOWinCountLabel();
                break;
        }
    }

    private static void gameDrawn() {
        GameController.currentGame.setGameOver(true);
        GameController.currentGame.incrementDrawsCount();
        gameView.updateDrawsCountLabel();
    }
}

class GameModel {
    private String currentPlayer;
    private int playerXWinCount;
    private int playerOWinCount;
    private int drawsCount;
    private int steps;
    private boolean isGameOver;
    private int[] grid;

    GameModel() {
        this.playerXWinCount = 0;
        this.playerOWinCount = 0;
        this.drawsCount = 0;
        this.steps = 0;
        this.isGameOver = false;

        // -10 ----> empty cell
        //   0 ----> O marked cell
        //   1 ----> X marked cell
        this.grid = new int[]{-10, -10, -10, -10, -10, -10, -10, -10, -10};
    }

    // reset only those variables that need to be reset after each game
    public void resetGameData() {
        this.steps = 0;
        this.isGameOver = false;
        this.grid = new int[]{-10, -10, -10, -10, -10, -10, -10, -10, -10};
    }

    public void setStartingPlayer(String startingPlayer) {
        this.currentPlayer = startingPlayer;
    }

    public int getSteps() {
        return steps;
    }

    public void incrementGameStep() {
        this.steps++;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public void changePlayer() {
        if (currentPlayer.equals("x")) {
            this.currentPlayer = "o";
        } else {
            this.currentPlayer = "x";
        }
    }

    public int getDrawsCount() {
        return drawsCount;
    }

    public void incrementDrawsCount() {
        this.drawsCount++;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayerXWinCount() {
        return playerXWinCount;
    }

    public void incrementPlayerXWinCount() {
        this.playerXWinCount++;
    }

    public int getPlayerOWinCount() {
        return playerOWinCount;
    }

    public void incrementPlayerOWinCount() {
        this.playerOWinCount++;
    }

    public int[] getGrid() {
        return grid;
    }
}

class GameCell extends JButton {

    private ImageIcon imageIcon;
    private boolean isMarked;
    private int cellIndex;

    GameCell(int index) {
        this.isMarked = false;
        this.cellIndex = index;
    }

    public void unMarkCell() {
        this.setIcon(null);
        this.isMarked = false;
    }

    public void highLightCell() {
        this.setBackground(Color.blue);
    }

    public void removeHightLight() {
        this.setBackground(Color.WHITE);
    }

    public boolean isMarked() {
        return isMarked;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void markGameCell(String player) {
        String imageFileName;

        if (player.toLowerCase().equals("x")) {
            imageFileName = "X.png";
        } else {
            imageFileName = "O.png";
        }

        try {
            this.imageIcon = new ImageIcon(ImageIO.read(getClass().getResource(imageFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setIcon(imageIcon);

        this.isMarked = true;
    }
}

class GameView {

    private JFrame frame;
    private JPanel rootPanel;
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel bottomPanel;
    private JRadioButton radioBtnX;
    private JRadioButton radioBtnO;
    private JLabel playerXWinCountLabel;
    private JLabel playerOWinCountLabel;
    private JLabel drawsCountLabel;

    public static ArrayList<GameCell> gameCellList;

    GameView() {
        GameView.gameCellList = new ArrayList<>();

        initFrameWindow();
        setTopPanel();
        setMiddlePanel();
        setBottomPanel();
        setRootPanel();
        setFrameWindow();
    }

    private void initFrameWindow() {
        this.frame = new JFrame("Tic Tac Toe");
        this.frame.setBounds(560, 250, 500, 500);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setFrameWindow() {
        this.frame.add(rootPanel);

        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void setRootPanel() {
        this.rootPanel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(rootPanel, BoxLayout.Y_AXIS);
        this.rootPanel.setLayout(boxLayout);

        this.rootPanel.add(topPanel);
        this.rootPanel.add(middlePanel);
        this.rootPanel.add(bottomPanel);
    }

    // set top panel containing new game button and
    // radio buttons
    private void setTopPanel() {
        this.topPanel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(topPanel, BoxLayout.Y_AXIS);
        this.topPanel.setLayout(boxLayout);

        this.topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // new game button
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameBtn.setFocusable(false);

        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameController.resetGame();
            }
        });

        // radio buttons
        radioBtnX = new JRadioButton("X starts");
        radioBtnO = new JRadioButton("O starts");

        radioBtnO.setFocusable(false);

        radioBtnX.setFocusable(false);

        // radio button X selected by default
        radioBtnX.setSelected(true);

        // add radio buttons to button group
        ButtonGroup radioBtnGroup = new ButtonGroup();
        radioBtnGroup.add(radioBtnX);
        radioBtnGroup.add(radioBtnO);

        // add radio button inside a panel
        JPanel radioBtnPanel = new JPanel();
        radioBtnPanel.add(radioBtnX);
        radioBtnPanel.add(radioBtnO);

        this.topPanel.add(newGameBtn);
        // add some space between new game button and radio buttons
        this.topPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        this.topPanel.add(radioBtnPanel);
    }

    // set middle panel game cells in grid
    private void setMiddlePanel() {
        this.middlePanel = new JPanel();

        GridLayout gridLayout = new GridLayout(3, 3);
        this.middlePanel.setLayout(gridLayout);

        GameCell gameCell;

        for (int i=0; i<9; i++) {
            gameCell = new GameCell(i);
            gameCell.setPreferredSize(new Dimension(150, 150));
            gameCell.setBackground(Color.WHITE);
            gameCell.setFocusable(false);

            // add game cell to game cell list
            GameView.gameCellList.add(gameCell);

            gameCell.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GameCell cell = (GameCell) e.getSource();

                    // call gameStarted method only one time per game
                    // when any player takes the first turn
                    if (GameController.currentGame.getSteps() == 0) {
                        gameStarted();
                    }

                    if (!cell.isMarked() && !GameController.currentGame.isGameOver()) {
                        GameController.currentGame.incrementGameStep();

                        String currentPlayer = GameController.currentGame.getCurrentPlayer();
                        int gridFillNumber;

                        cell.markGameCell(currentPlayer);

                        // number to fill the grid array in GameModel class
                        gridFillNumber = currentPlayer.equals("x") ? 1 : 0;

                        GameController.currentGame.getGrid()[cell.getCellIndex()] = gridFillNumber;
                        GameController.checkForWin();
                        GameController.currentGame.changePlayer();
                    }
                }
            });

            this.middlePanel.add(gameCell);
        }
    }

    private void setBottomPanel() {
        this.bottomPanel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
        this.bottomPanel.setLayout(boxLayout);

        this.bottomPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        this.playerXWinCountLabel = new JLabel("X wins: " + GameController.currentGame.getPlayerXWinCount());
        this.playerOWinCountLabel = new JLabel("O wins: " + GameController.currentGame.getPlayerOWinCount());
        this.drawsCountLabel = new JLabel("Draws: " + GameController.currentGame.getDrawsCount());

        // add space around each label (left and right)
        this.bottomPanel.add(Box.createHorizontalGlue());
        this.bottomPanel.add(playerXWinCountLabel);

        this.bottomPanel.add(Box.createHorizontalGlue());
        this.bottomPanel.add(playerOWinCountLabel);
        this.bottomPanel.add(Box.createHorizontalGlue());

        this.bottomPanel.add(drawsCountLabel);
        this.bottomPanel.add(Box.createHorizontalGlue());
    }

    // enable radio buttons
    public void enableRadioBtns() {
        this.radioBtnX.setEnabled(true);
        this.radioBtnO.setEnabled(true);
    }

    // disable radio buttons
    public void disableRadioBtns() {
        this.radioBtnX.setEnabled(false);
        this.radioBtnO.setEnabled(false);
    }

    public void updatePlayerXWinCountLabel() {
        this.playerXWinCountLabel.setText("X wins: " + GameController.currentGame.getPlayerXWinCount());
    }

    public void updatePlayerOWinCountLabel() {
        this.playerOWinCountLabel.setText("O wins: " + GameController.currentGame.getPlayerOWinCount());
    }

    public void updateDrawsCountLabel() {
        this.drawsCountLabel.setText("Draws: " + GameController.currentGame.getDrawsCount());
    }

    // called only one time per game when any player takes first turn
    public void gameStarted() {
        // disable radio buttons
        disableRadioBtns();

        // set starting player based on selected radio button
        if (radioBtnX.isSelected()) {
            GameController.currentGame.setStartingPlayer("x");
        } else {
            GameController.currentGame.setStartingPlayer("o");
        }
    }
}
