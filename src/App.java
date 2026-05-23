import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 三子棋（Tic-Tac-Toe）Swing GUI 主程序。
 *
 * 使用 CardLayout 管理两个界面：
 * 1. 菜单界面 — 标题 + 开始/退出按钮
 * 2. 游戏界面 — 3×3 棋盘 + 状态栏 + 操作按钮
 *
 * 玩家执 X（蓝色），电脑执 O（红色）。玩家点击空格子后，
 * 电脑经过短暂延迟自动落子，直到分出胜负或平局。
 */
public class App {
    // === 全局 UI 组件 ===
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    // === 游戏相关状态 ===
    private static TicTacToe game;
    private static JButton[][] cellButtons = new JButton[3][3];
    private static JLabel statusLabel;
    private static boolean gameOver = false;

    /**
     * 入口：在事件分发线程中启动 GUI，避免线程安全问题。
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShow);
    }

    /**
     * 创建主窗口，初始化 CardLayout 并加载菜单和游戏面板。
     */
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

    /**
     * 构建菜单面板：标题 + "开始游戏"按钮 + "退出游戏"按钮。
     * 使用 GridBagLayout 居中对齐。
     */
    private static JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // 标题
        JLabel title = new JLabel("三子棋", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
        title.setForeground(new Color(255, 255, 255));
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 0, 40, 0);
        panel.add(title, gbc);

        // 开始按钮 — 初始化新游戏并切换到棋盘界面
        JButton startBtn = createStyledButton("开始游戏");
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 60, 8, 60);
        panel.add(startBtn, gbc);

        // 退出按钮 — 直接关闭程序
        JButton exitBtn = createStyledButton("退出游戏");
        gbc.gridy = 2;
        panel.add(exitBtn, gbc);

        startBtn.addActionListener(e -> { setupNewGame(); cardLayout.show(mainPanel, "game"); });
        exitBtn.addActionListener(e -> System.exit(0));
        return panel;
    }

    /**
     * 构建游戏面板：状态标签 + 3×3 棋盘网格 + 底部操作按钮。
     */
    private static JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 顶部状态标签 — 显示当前回合或游戏结果
        statusLabel = new JLabel("你的回合", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel, BorderLayout.NORTH);

        // 中间棋盘 — 3×3 网格，间距 6px
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

        // 底部操作栏 — 重新开始 / 返回菜单
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

    /**
     * 重置游戏状态：新建 TicTacToe 实例，清空所有格子按钮。
     */
    private static void setupNewGame() {
        game = new TicTacToe();
        gameOver = false;
        statusLabel.setText("你的回合");
        statusLabel.setForeground(Color.WHITE);

        // 重置所有格子按钮的文字和状态
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cellButtons[r][c].setText("");
                cellButtons[r][c].setEnabled(true);
                cellButtons[r][c].setBackground(new Color(70, 70, 70));
                cellButtons[r][c].setForeground(Color.WHITE);
            }
        }
    }

    /**
     * 玩家点击棋格的回调。
     * 放置 X，检查游戏状态，然后触发电脑走棋（延迟 400ms）。
     */
    private static void onCellClick(int row, int col) {
        if (gameOver) return;
        if (!game.placeMove(row, col, TicTacToe.PLAYER)) return;

        // 更新按钮显示为 X（蓝色）
        cellButtons[row][col].setText("X");
        cellButtons[row][col].setForeground(new Color(100, 200, 255));
        cellButtons[row][col].setEnabled(false);

        // 检查玩家是否获胜或平局
        TicTacToe.GameState state = game.checkGameState();
        if (state == TicTacToe.GameState.PLAYER_WINS) {
            endGame("你赢了！", new Color(100, 255, 100));
            return;
        } else if (state == TicTacToe.GameState.DRAW) {
            endGame("平局！", new Color(255, 200, 100));
            return;
        }

        // 电脑走棋 — 使用 Timer 延迟，保持 UI 响应
        statusLabel.setText("电脑思考中...");
        statusLabel.setForeground(new Color(180, 180, 180));

        Timer timer = new Timer(400, evt -> {
            int[] move = game.computerMove();
            // 更新按钮显示为 O（红色）
            cellButtons[move[0]][move[1]].setText("O");
            cellButtons[move[0]][move[1]].setForeground(new Color(255, 130, 130));
            cellButtons[move[0]][move[1]].setEnabled(false);

            // 检查电脑是否获胜或平局
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
        timer.setRepeats(false); // 只执行一次
        timer.start();
    }

    /**
     * 游戏结束：显示结果文字，禁用所有格子。
     */
    private static void endGame(String message, Color color) {
        gameOver = true;
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                cellButtons[r][c].setEnabled(false);
    }

    /**
     * 创建大号主按钮（菜单用），蓝色背景 + 悬停高亮。
     */
    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 22));
        btn.setBackground(new Color(70, 130, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
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

    /**
     * 创建小号操作按钮（棋盘底部用），灰色背景 + 悬停高亮。
     */
    private static JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        btn.setBackground(new Color(80, 80, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
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