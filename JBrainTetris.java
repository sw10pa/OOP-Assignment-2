import java.awt.*;
import javax.swing.*;

public class JBrainTetris extends JTetris {

    private int count;
    private JSlider adversary;
    private JCheckBox brainMode;
    private Brain.Move brainMove;
    private JLabel adversaryStatus;
    private JCheckBox animateFalling;
    private DefaultBrain defaultBrain;

    JBrainTetris(int pixels) {
        super(pixels);
        defaultBrain = new DefaultBrain();
    }

    @Override
    public Piece pickNextPiece() {
        int randNum = random.nextInt(100);
        if (randNum >= adversary.getValue()) {
            adversaryStatus.setText("ok");
            return super.pickNextPiece();
        }

        Piece worstPiece = null;
        double worstScore = 0;
        for (int i = 0; i < pieces.length; i++) {
            brainMove = defaultBrain.bestMove(board, pieces[i], board.getHeight(), brainMove);
            if (brainMove != null && brainMove.score > worstScore) {
                worstPiece = brainMove.piece;
                worstScore = brainMove.score;
            }
        }

        adversaryStatus.setText("*ok*");
        return worstPiece;
    }

    @Override
    public void tick(int verb) {
        if (brainMode.isSelected() && verb == DOWN) {
            if (count != super.count) {
                board.undo();
                count = super.count;
                brainMove = defaultBrain.bestMove(board, currentPiece, board.getHeight(), brainMove);
            }
            if (brainMove != null) {
                if (!currentPiece.equals(brainMove.piece)) {
                    super.tick(ROTATE);
                }
                if (currentX < brainMove.x) {
                    super.tick(RIGHT);
                } else if (currentX > brainMove.x) {
                    super.tick(LEFT);
                }
                if (!animateFalling.isSelected() && currentY > brainMove.y) {
                    super.tick(DROP);
                }
            }
        }
        super.tick(verb);
    }

    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();

        panel.add(new JLabel("Brain:"));

        brainMode = new JCheckBox("Brain active");
        brainMode.setSelected(false);
        panel.add(brainMode);

        animateFalling = new JCheckBox("Animate Falling");
        animateFalling.setSelected(true);
        panel.add(animateFalling);

        JPanel little = new JPanel();

        little.add(new JLabel("Adversary:"));

        adversary = new JSlider(0, 100, 0);
        adversary.setPreferredSize(new Dimension(100, 15));
        little.add(adversary);

        adversaryStatus = new JLabel("ok");
        little.add(adversaryStatus);

        panel.add(little);

        return panel;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }

        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }

}
