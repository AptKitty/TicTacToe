import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    playGame(scanner);
                    break;
                case 2:
                    System.out.println("退出游戏...");
                    scanner.close();
                    return;
                default:
                    System.out.println("输入错误，请重新输入...");
            }
        }
    }

    private static void showMenu() {
        System.out.println("*****************************");
        System.out.println("*****       三子棋      *****");
        System.out.println("*****    1. 开始游戏    *****");
        System.out.println("*****    2. 退出游戏    *****");
        System.out.println("*****************************");
    }

    private static void playGame(Scanner scanner) {
        TicTacToe game = new TicTacToe();
        game.displayBoard();

        while (true) {
            int row, col;
            while (true) {
                System.out.println("请输入你的坐标（行 列），用空格分隔：");
                row = scanner.nextInt() - 1;
                col = scanner.nextInt() - 1;

                if (row < 0 || row > 2 || col < 0 || col > 2) {
                    System.out.println("坐标超出范围，请输入 1~3 之间的数字。");
                    continue;
                }
                if (game.placeMove(row, col, 'X')) {
                    break;
                }
            }
            game.displayBoard();

            TicTacToe.GameState state = game.checkGameState();
            if (state == TicTacToe.GameState.PLAYER_WINS) {
                System.out.println("你赢了！");
                return;
            } else if (state == TicTacToe.GameState.DRAW) {
                System.out.println("平局！");
                return;
            }

            System.out.println("电脑思考中...");
            int[] computerMove = game.computerMove();
            System.out.println("电脑落子: " + (computerMove[0] + 1) + " " + (computerMove[1] + 1));
            game.displayBoard();

            state = game.checkGameState();
            if (state == TicTacToe.GameState.COMPUTER_WINS) {
                System.out.println("电脑赢了！");
                return;
            } else if (state == TicTacToe.GameState.DRAW) {
                System.out.println("平局！");
                return;
            }
        }
    }
}
