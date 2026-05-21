import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        do {
            menu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    TicTacToe ticTacToe = new TicTacToe();
                    ticTacToe.initBoard();
                    ticTacToe.displayBoard();
                    while (true) {
                        int []player_inpput = new int[2];
                        System.out.println("请输入玩家1的坐标（行 列）：");
                        player_inpput[0] = scanner.nextInt();
                        player_inpput[1] = scanner.nextInt();
                        if(player_inpput[0] >= 1 && player_inpput[0] <= 3 && player_inpput[1] >=1 && player_inpput[1] <=3) {
                            player_inpput[0] -= 1;
                            player_inpput[1] -= 1;
                        }else {
                            System.out.println("输入坐标超出范围");
                            continue;
                        }
                        if (!(ticTacToe.playerMove(player_inpput[0], player_inpput[1], 'X'))) {
                            continue;
                        }
                        ticTacToe.displayBoard();
                        int result = ticTacToe.win(1,player_inpput[0],player_inpput[1]);
                        if (result == 2) {
                            System.out.println("玩家获胜！");
                            break;
                        }else if (result == 0) {
                            System.out.println("平局");
                            break;
                        }
                        int []computerMove = ticTacToe.computerMove();
                        ticTacToe.displayBoard();
                        result = ticTacToe.win(2,computerMove[0],computerMove[1]);
                        if (result == 3) {
                            System.out.println("电脑获胜！");
                            break;
                        }else if (result == 0) {
                            System.out.println("平局");
                            break;
                        }
                    }
                    break;
                case 2:
                    System.out.println("退出游戏...");
                    scanner.close();
                    return;
                default:
                    System.out.println("输入错误，请重新输入...");
                    break;
            }
        } while (true);
    }
    static void menu() {
        System.out.println("****************************");
        System.out.println("*****      三子棋      *****");
        System.out.println("*****    1.开始游戏    *****");
        System.out.println("*****    2.退出游戏    *****");
        System.out.println("****************************");
    }
}

class TicTacToe {
    final int len = 3;
    final int col = 3;
    private char[][] board;
    void initBoard() {
        board = new char[len][col];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = ' ';
            }
        }
    }
    void displayBoard() {
        for (int i = 0; i < len; i++) {
            System.err.print(" ");
            for (int j = 0; j < col; j++) {
                System.out.print(board[i][j]);
                if (j != col - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (i != len - 1) {
                System.out.println("-----------");
            }
        }
    }
    boolean playerMove(int x, int y, char player_input) {
        while (true) {
            if (board[x][y] == ' ') {
                board[x][y] = player_input;
                return true;
            } else {
                System.out.println("该位置已被占用，请重新输入...");
                return false;
            }
        }
    }
    int[] computerMove() {
        // 电脑随机选择一个空位置进行落子
        int x, y;
        do {
            x = (int) (Math.random() * len);
            y = (int) (Math.random() * col);
        } while (board[x][y] != ' ');
        board[x][y] = 'O';
        int []arr = {x,y};
        return arr;
    }
    int win(int player_or_computer, int x, int y) {
        int temp = 0;
        if (player_or_computer == 1) {
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < col; j++) {
                    if (board[i][j] == 'X'){
                        temp += 1;
                    }
                }
                if (temp == 3) {
                    return 2;
                }else {
                    temp = 0;
                }
            }
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < col; j++) {
                    if (board[j][i] == 'X'){
                        temp += 1;
                    }
                }
                if (temp == 3) {
                    return 2;
                }else {
                    temp = 0;
                }
            }
            if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] || board[2][0] == board[1][1] && board[1][1] == board[0][2]) && board[1][1] == 'X') {
                return 2;
            }
        }else {
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < col; j++) {
                    if (board[i][j] == 'O'){
                        temp += 1;
                    }
                }
                if (temp == 3) {
                    return 3;
                }else {
                    temp = 0;
                }
            }
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < col; j++) {
                    if (board[j][i] == 'O'){
                        temp += 1;
                    }
                }
                if (temp == 3) {
                    return 3;
                }else {
                    temp = 0;
                }
            }
            if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] || board[2][0] == board[1][1] && board[1][1] == board[0][2]) && board[1][1] == 'O') {
                return 3;
            }
        }
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != ' ') {
                    temp += 1;
                }
            }
        }
        if (temp == 9) {
            return 0;
        }else{
            return 1;
        }
    }
}
