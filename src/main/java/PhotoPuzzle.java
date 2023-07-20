//        String imagePath = "/home/stepan/Desktop/tabir/eskiz.jpg";
//        String outputFolder = "/home/stepan/Desktop/puzzle/";

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PhotoPuzzle {
    public static void main(String[] args) {
        String imagePath = "/home/stepan/Desktop/eskizsm (4).jpg";
        String outputFolder = "/home/stepan/Desktop/tabir/pazl/";

        try {

            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();


            int rectangleWidth = width / 2;
            int rectangleHeight = height / 2;


            int index = 1;


            int startX = 0;
            int startY = 0;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {

                    BufferedImage rectangle = new BufferedImage(rectangleWidth, rectangleHeight, image.getType());

                    rectangle.getGraphics().drawImage(image, 0, 0, rectangleWidth, rectangleHeight, startX, startY, startX + rectangleWidth, startY + rectangleHeight, null);

                    String rectanglePath = outputFolder + "rectangle_" + index + ".jpg";
                    ImageIO.write(rectangle, "jpg", new File(rectanglePath));

                    index++;

                    startX += rectangleWidth;
                }


                startX = 0;
                startY += rectangleHeight;
            }

            System.out.println("Фотографію розбито на прямокутники та збережено в папці " + outputFolder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
