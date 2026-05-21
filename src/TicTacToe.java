public class TicTacToe {
    public enum GameState {
        PLAYING,
        PLAYER_WINS,
        COMPUTER_WINS,
        DRAW
    }

    private static final int SIZE = 3;
    private static final char PLAYER = 'X';
    private static final char COMPUTER = 'O';
    private static final char EMPTY = ' ';

    private final char[][] board = new char[SIZE][SIZE];

    public TicTacToe() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public void displayBoard() {
        for (int i = 0; i < SIZE; i++) {
            System.out.print(" ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j]);
                if (j != SIZE - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (i != SIZE - 1) {
                System.out.println("---+---+---");
            }
        }
    }

    public boolean placeMove(int row, int col, char piece) {
        if (board[row][col] != EMPTY) {
            System.out.println("该位置已被占用，请重新输入...");
            return false;
        }
        board[row][col] = piece;
        return true;
    }

    public GameState checkGameState() {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
            }
        }
        for (int j = 0; j < SIZE; j++) {
            if (board[0][j] != EMPTY && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
            }
        }
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return GameState.PLAYING;
                }
            }
        }
        return GameState.DRAW;
    }

    public int[] computerMove() {
        int[] move;

        move = findWinningMove(COMPUTER);
        if (move != null) {
            board[move[0]][move[1]] = COMPUTER;
            return move;
        }

        move = findWinningMove(PLAYER);
        if (move != null) {
            board[move[0]][move[1]] = COMPUTER;
            return move;
        }

        if (board[1][1] == EMPTY) {
            board[1][1] = COMPUTER;
            return new int[]{1, 1};
        }

        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == EMPTY) {
                board[corner[0]][corner[1]] = COMPUTER;
                return corner;
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = COMPUTER;
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{-1, -1};
    }

    private int[] findWinningMove(char piece) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = piece;
                    boolean wins = checkForWin(piece);
                    board[i][j] = EMPTY;
                    if (wins) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    private boolean checkForWin(char piece) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == piece && board[i][1] == piece && board[i][2] == piece) return true;
            if (board[0][i] == piece && board[1][i] == piece && board[2][i] == piece) return true;
        }
        if (board[0][0] == piece && board[1][1] == piece && board[2][2] == piece) return true;
        if (board[0][2] == piece && board[1][1] == piece && board[2][0] == piece) return true;
        return false;
    }
}
