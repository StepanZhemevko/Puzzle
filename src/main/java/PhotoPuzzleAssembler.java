import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class PhotoPuzzleAssembler {
    public static void main(String[] args) {
        String puzzleFolder = "/home/stepan/Desktop/puzz/";

        try {
            File folder = new File(puzzleFolder);
            File[] puzzleFiles = folder.listFiles();
            if (puzzleFiles == null) {
                System.out.println("Error: The folder does not contain any puzzle files.");
                return;
            }

            BufferedImage[] puzzles = new BufferedImage[16];
            for (int i = 0; i < 16; i++) {
                puzzles[i] = ImageIO.read(puzzleFiles[i]);
            }

            BufferedImage[][] assembledPuzzle = solvePuzzle(puzzles);

            BufferedImage assembledImage = concatenatePuzzle(assembledPuzzle);

            ImageIO.write(assembledImage, "jpg", new File("/home/stepan/Desktop/tabir/assembled_image.jpg"));

            System.out.println("Assembled puzzle successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage[][] solvePuzzle(BufferedImage[] puzzles) {
        BufferedImage[][] assembledPuzzle = new BufferedImage[4][4];

        List<BufferedImage> unusedPuzzles = new ArrayList<>(Arrays.asList(puzzles));

        assembledPuzzle[0][0] = unusedPuzzles.remove(0);

        for (int col = 1; col < 4; col++) {
            assembledPuzzle[0][col] = findNearestNeighbor(assembledPuzzle[0][col - 1], unusedPuzzles);
        }


        for (int row = 1; row < 4; row++) {
            assembledPuzzle[row][0] = findNearestNeighbor(assembledPuzzle[row - 1][0], unusedPuzzles);
            for (int col = 1; col < 4; col++) {
                assembledPuzzle[row][col] = findNearestNeighbor(assembledPuzzle[row][col - 1], unusedPuzzles);
            }
        }

        return assembledPuzzle;
    }

    private static BufferedImage findNearestNeighbor(BufferedImage puzzle, List<BufferedImage> unusedPuzzles) {
        int nearestDistance = Integer.MAX_VALUE;
        BufferedImage nearestNeighbor = null;

        for (BufferedImage otherPuzzle : unusedPuzzles) {
            int distance = calculateDistance(puzzle, otherPuzzle);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNeighbor = otherPuzzle;
            }
        }

        unusedPuzzles.remove(nearestNeighbor);
        return nearestNeighbor;
    }

    private static int calculateDistance(BufferedImage puzzle1, BufferedImage puzzle2) {
        int width = puzzle1.getWidth();
        int height = puzzle1.getHeight();
        int distance = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel1 = puzzle1.getRGB(x, y);
                int pixel2 = puzzle2.getRGB(x, y);

                int redDiff = (pixel1 >> 4 & 0xFF) - (pixel2 >> 4 & 0xFF);
                int greenDiff = (pixel1 >> 2 & 0xFF) - (pixel2 >> 2 & 0xFF);
                int blueDiff = (pixel1 & 0xFF) - (pixel2 & 0xFF);

                distance += redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
            }
        }

        return distance;
    }

    private static BufferedImage concatenatePuzzle(BufferedImage[][] assembledPuzzle) {
        int assembledWidth = 0;
        int assembledHeight = 0;

        for (BufferedImage[] row : assembledPuzzle) {
            int rowWidth = 0;
            int rowHeight = row[0].getHeight();

            for (BufferedImage puzzle : row) {
                rowWidth += puzzle.getWidth();
            }

            assembledWidth = Math.max(assembledWidth, rowWidth);
            assembledHeight += rowHeight;
        }

        BufferedImage assembledImage = new BufferedImage(assembledWidth, assembledHeight, BufferedImage.TYPE_INT_RGB);

        int currentY = 0;
        for (BufferedImage[] row : assembledPuzzle) {
            int currentX = 0;
            int rowHeight = row[0].getHeight();

            for (BufferedImage puzzle : row) {
                int puzzleWidth = puzzle.getWidth();

                assembledImage.getGraphics().drawImage(puzzle, currentX, currentY, null);

                currentX += puzzleWidth;
            }
            currentY += rowHeight;
        }
        return assembledImage;
    }
}
