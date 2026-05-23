/**
 * 三子棋（Tic-Tac-Toe）核心逻辑引擎。
 *
 * 职责：管理 3×3 棋盘状态，执子落子、胜负判定、电脑 AI。
 * 不包含任何 UI 或 I/O 代码，可被控制台或 GUI 复用。
 */
public class TicTacToe {

    /** 游戏状态枚举 */
    public enum GameState {
        PLAYING,        // 游戏进行中
        PLAYER_WINS,    // 玩家获胜
        COMPUTER_WINS,  // 电脑获胜
        DRAW            // 平局（棋盘满且无人获胜）
    }

    // === 棋盘常量 ===
    private static final int SIZE = 3;
    public static final char PLAYER = 'X';    // 玩家棋子
    public static final char COMPUTER = 'O';  // 电脑棋子
    public static final char EMPTY = ' ';     // 空格子

    /** 棋盘状态：board[row][col] */
    private final char[][] board = new char[SIZE][SIZE];

    /**
     * 初始化棋盘，所有格子置为空。
     */
    public TicTacToe() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    /**
     * 读取指定格子的内容，供 GUI 查询显示。
     */
    public char getCell(int row, int col) {
        return board[row][col];
    }

    /**
     * 在指定位置落子。
     *
     * @param row   行索引（0-2）
     * @param col   列索引（0-2）
     * @param piece 棋子类型（PLAYER 或 COMPUTER）
     * @return true 落子成功，false 该位置已被占用
     */
    public boolean placeMove(int row, int col, char piece) {
        if (board[row][col] != EMPTY) {
            return false;
        }
        board[row][col] = piece;
        return true;
    }

    /**
     * 扫描棋盘，判断当前游戏状态。
     *
     * 检查顺序：行 → 列 → 对角线 → 是否填满。
     * 只要任意一方连成三子即返回对应结果。
     *
     * @return 当前 GameState
     */
    public GameState checkGameState() {
        // 检查三行
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
            }
        }
        // 检查三列
        for (int j = 0; j < SIZE; j++) {
            if (board[0][j] != EMPTY && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
            }
        }
        // 检查主对角线（左上→右下）
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
        }
        // 检查副对角线（右上→左下）
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2] == PLAYER ? GameState.PLAYER_WINS : GameState.COMPUTER_WINS;
        }
        // 检查是否还有空格子 — 有则继续，无则平局
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return GameState.PLAYING;
                }
            }
        }
        return GameState.DRAW;
    }

    /**
     * 电脑 AI 走棋，按优先级依次尝试：
     *
     * 1. 自己能赢 → 直接赢
     * 2. 对手即将赢 → 堵住
     * 3. 中心 (1,1) 为空 → 占中心
     * 4. 四角有空 → 占角
     * 5. 以上都不满足 → 任意空格子
     *
     * @return 落子坐标 [row, col]，若无空位返回 [-1, -1]
     */
    public int[] computerMove() {
        int[] move;

        // 优先级 1：尝试直接获胜
        move = findWinningMove(COMPUTER);
        if (move != null) {
            board[move[0]][move[1]] = COMPUTER;
            return move;
        }

        // 优先级 2：堵住玩家的获胜路线
        move = findWinningMove(PLAYER);
        if (move != null) {
            board[move[0]][move[1]] = COMPUTER;
            return move;
        }

        // 优先级 3：占据中心
        if (board[1][1] == EMPTY) {
            board[1][1] = COMPUTER;
            return new int[]{1, 1};
        }

        // 优先级 4：占据任意角（优先于边）
        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == EMPTY) {
                board[corner[0]][corner[1]] = COMPUTER;
                return corner;
            }
        }

        // 优先级 5：遍历所有格子，取第一个空位
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = COMPUTER;
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{-1, -1}; // 无空格子
    }

    /**
     * 寻找能令指定棋子连成三子的空格子（模拟落子后判赢）。
     *
     * @param piece 要检查的棋子
     * @return 获胜格坐标，找不到返回 null
     */
    private int[] findWinningMove(char piece) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = piece;
                    boolean wins = checkForWin(piece);
                    board[i][j] = EMPTY; // 恢复，只做探测
                    if (wins) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    /**
     * 轻量判赢：检查指定棋子是否已在棋盘上连成三子。
     * 专供 findWinningMove 探测使用。
     */
    private boolean checkForWin(char piece) {
        // 行
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == piece && board[i][1] == piece && board[i][2] == piece) return true;
            if (board[0][i] == piece && board[1][i] == piece && board[2][i] == piece) return true;
        }
        // 对角线
        if (board[0][0] == piece && board[1][1] == piece && board[2][2] == piece) return true;
        if (board[0][2] == piece && board[1][1] == piece && board[2][0] == piece) return true;
        return false;
    }
}