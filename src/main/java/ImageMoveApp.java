import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMoveApp {

    private JFrame frame;
    private BufferedImage[] puzzlePieces;
    private int pieceWidth, pieceHeight;
    private int mouseX, mouseY;

    private JLabel[] puzzleGrid;
    private boolean[] pieceLocked;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageMoveApp app = new ImageMoveApp();
            app.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Image Move App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage image = loadImage();

        int numPieces = 16;
        assert image != null;
        puzzlePieces = splitImageIntoPieces(image, numPieces);
        pieceWidth = image.getWidth() / 4;
        pieceHeight = image.getHeight() / 4;

        JPanel puzzlePanel = new JPanel(null);
        puzzlePanel.setPreferredSize(new Dimension(image.getWidth() / 2, image.getHeight() / 2));

        puzzleGrid = new JLabel[numPieces];
        pieceLocked = new boolean[numPieces];

        for (int i = 0; i < numPieces; i++) {
            puzzleGrid[i] = new JLabel();
            puzzleGrid[i].setSize(pieceWidth, pieceHeight);
            int row = i / 4;
            int col = i % 4;
            puzzleGrid[i].setLocation(col * pieceWidth, row * pieceHeight);
            puzzleGrid[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            puzzlePanel.add(puzzleGrid[i]);
        }

        for (int i = 0; i < numPieces; i++) {
            JLabel pieceLabel = new JLabel(new ImageIcon(puzzlePieces[i]));
            pieceLabel.setSize(pieceWidth, pieceHeight);
            pieceLabel.setLocation((int) (Math.random() * (puzzlePanel.getWidth() - pieceWidth)), (int) (Math.random() * (puzzlePanel.getHeight() - pieceHeight)));
            pieceLabel.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        rotatePiece(pieceLabel);
                    } else {
                        mouseX = e.getX();
                        mouseY = e.getY();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    int index = getPuzzleGridIndex(pieceLabel.getX(), pieceLabel.getY());
                    if (index != -1 && !pieceLocked[index]) {
                        int gridX = puzzleGrid[index].getX();
                        int gridY = puzzleGrid[index].getY();
                        if (Math.abs(pieceLabel.getX() - gridX) <= 10 && Math.abs(pieceLabel.getY() - gridY) <= 10) {
                            pieceLabel.setLocation(gridX, gridY);
                            pieceLocked[index] = true;
                            checkPuzzleComplete();
                        }
                    }
                }
            });
            pieceLabel.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int dx = e.getX() - mouseX;
                    int dy = e.getY() - mouseY;
                    int newX = pieceLabel.getX() + dx;
                    int newY = pieceLabel.getY() + dy;
                    pieceLabel.setLocation(newX, newY);
                    mouseX = e.getX();
                    mouseY = e.getY();
                }
            });
            puzzlePanel.add(pieceLabel);
        }

        JButton autoArrangeButton = new JButton("Auto Arrange");
        autoArrangeButton.addActionListener(e -> autoArrangePuzzle(puzzlePieces));
        autoArrangeButton.setBounds(10, 580, 120, 30);
        puzzlePanel.add(autoArrangeButton);

        frame.getContentPane().add(new JScrollPane(puzzlePanel));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("/home/stepan/Desktop/tabir/eskiz.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage[] splitImageIntoPieces(BufferedImage image, int numPieces) {
        int rows = (int) Math.sqrt(numPieces);
        int cols = numPieces / rows;

        BufferedImage[] pieces = new BufferedImage[numPieces];
        int pieceWidth = image.getWidth() / cols;
        int pieceHeight = image.getHeight() / rows;

        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int startX = x * pieceWidth;
                int startY = y * pieceHeight;
                BufferedImage piece = image.getSubimage(startX, startY, pieceWidth, pieceHeight);
                pieces[index] = piece;
                index++;
            }
        }

        return pieces;
    }

    private int getPuzzleGridIndex(int x, int y) {
        int row = y / pieceHeight;
        int col = x / pieceWidth;
        if (row >= 0 && row < 4 && col >= 0 && col < 4) {
            return row * 4 + col;
        }
        return -1;
    }

    private void checkPuzzleComplete() {
        boolean isComplete = true;
        for (boolean locked : pieceLocked) {
            if (!locked) {
                isComplete = false;
                break;
            }
        }
        if (isComplete) {
            JOptionPane.showMessageDialog(frame, "Puzzle completed!");
        }
    }

    private void rotatePiece(JLabel pieceLabel) {
        Icon icon = pieceLabel.getIcon();
        if (icon instanceof ImageIcon) {
            BufferedImage image = (BufferedImage) ((ImageIcon) icon).getImage();
            BufferedImage rotatedImage = rotateImage(image);
            pieceLabel.setIcon(new ImageIcon(rotatedImage));
            pieceLabel.setSize(pieceLabel.getHeight(), pieceLabel.getWidth());
        }
    }

    private void autoArrangePuzzle(BufferedImage[] pieces) {
        for (int i = 0; i < puzzleGrid.length; i++) {
            if (!pieceLocked[i]) {
                puzzleGrid[i].setIcon(new ImageIcon(pieces[i]));
                puzzleGrid[i].repaint();
                pieceLocked[i] = true;
            }
        }
        checkPuzzleComplete();
    }

    private BufferedImage rotateImage(BufferedImage image) {
        double radianAngle = Math.toRadians(90);
        double sin = Math.abs(Math.sin(radianAngle));
        double cos = Math.abs(Math.cos(radianAngle));
        int newWidth = (int) Math.round(image.getWidth() * cos + image.getHeight() * sin);
        int newHeight = (int) Math.round(image.getHeight() * cos + image.getWidth() * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int centerX = image.getWidth() / 2;
        int centerY = image.getHeight() / 2;

        AffineTransform transform = new AffineTransform();
        transform.translate((double) newWidth / 2, (double) newHeight / 2);
        transform.rotate(radianAngle);
        transform.translate(-centerX, -centerY);

        g.drawImage(image, transform, null);
        g.dispose();

        return rotatedImage;
    }
}