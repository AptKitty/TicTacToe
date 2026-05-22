import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static TicTacToe game;
    private static JButton[][] cellButtons = new JButton[3][3];
    private static JLabel statusLabel;
    private static boolean gameOver = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShow);
    }

    private static void createAndShow() {
        frame = new JFrame("三子棋");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createGamePanel(), "game");
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel title = new JLabel("三子棋", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
        title.setForeground(new Color(255, 255, 255));
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 0, 40, 0);
        panel.add(title, gbc);
        JButton startBtn = createStyledButton("开始游戏");
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 60, 8, 60);
        panel.add(startBtn, gbc);
        JButton exitBtn = createStyledButton("退出游戏");
        gbc.gridy = 2;
        panel.add(exitBtn, gbc);
        startBtn.addActionListener(e -> { setupNewGame(); cardLayout.show(mainPanel, "game"); });
        exitBtn.addActionListener(e -> System.exit(0));
        return panel;
    }
    private static JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statusLabel = new JLabel("你的回合", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel, BorderLayout.NORTH);
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 6, 6));
        boardPanel.setBackground(new Color(60, 60, 60));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 42));
                btn.setBackground(new Color(70, 70, 70));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
                final int row = r, col = c;
                btn.addActionListener(e -> onCellClick(row, col));
                cellButtons[r][c] = btn;
                boardPanel.add(btn);
            }
        }
        panel.add(boardPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(45, 45, 45));
        JButton restartBtn = createSmallButton("重新开始");
        JButton menuBtn = createSmallButton("返回菜单");
        restartBtn.addActionListener(e -> setupNewGame());
        menuBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        bottomPanel.add(restartBtn);
        bottomPanel.add(menuBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    private static void setupNewGame() {
        game = new TicTacToe();
        gameOver = false;
        statusLabel.setText("你的回合");
        statusLabel.setForeground(Color.WHITE);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cellButtons[r][c].setText("");
                cellButtons[r][c].setEnabled(true);
                cellButtons[r][c].setBackground(new Color(70, 70, 70));
                cellButtons[r][c].setForeground(Color.WHITE);
            }
        }
    }

    private static void onCellClick(int row, int col) {
        if (gameOver) return;
        if (!game.placeMove(row, col, TicTacToe.PLAYER)) return;
        cellButtons[row][col].setText("X");
        cellButtons[row][col].setForeground(new Color(100, 200, 255));
        cellButtons[row][col].setEnabled(false);

        TicTacToe.GameState state = game.checkGameState();
        if (state == TicTacToe.GameState.PLAYER_WINS) {
            endGame("你赢了！", new Color(100, 255, 100));
            return;
        } else if (state == TicTacToe.GameState.DRAW) {
            endGame("平局！", new Color(255, 200, 100));
            return;
        }

        statusLabel.setText("电脑思考中...");
        statusLabel.setForeground(new Color(180, 180, 180));

        Timer timer = new Timer(400, evt -> {
            int[] move = game.computerMove();
            cellButtons[move[0]][move[1]].setText("O");
            cellButtons[move[0]][move[1]].setForeground(new Color(255, 130, 130));
            cellButtons[move[0]][move[1]].setEnabled(false);

            TicTacToe.GameState s = game.checkGameState();
            if (s == TicTacToe.GameState.COMPUTER_WINS) {
                endGame("电脑赢了！", new Color(255, 100, 100));
            } else if (s == TicTacToe.GameState.DRAW) {
                endGame("平局！", new Color(255, 200, 100));
            } else {
                statusLabel.setText("你的回合");
                statusLabel.setForeground(Color.WHITE);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void endGame(String message, Color color) {
        gameOver = true;
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                cellButtons[r][c].setEnabled(false);
    }
    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 22));
        btn.setBackground(new Color(70, 130, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(90, 150, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(70, 130, 220));
            }
        });
        return btn;
    }

    private static JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        btn.setBackground(new Color(80, 80, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(110, 110, 110));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 80, 80));
            }
        });
        return btn;
    }
}