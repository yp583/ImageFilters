import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ImageFilters {
    // makes a filter grayscale by averaging rgb values of each pixel in image
    public static PixImage grayscaleFilter(PixImage image) {
        PixImage copy = new PixImage(image);
        int[][] grayint = new int[copy.height][copy.width];
        PixImage grayImage = new PixImage(copy.height, copy.width);
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                float gray = (copy.red[i][j] + copy.blue[i][j] + copy.green[i][j])/3.0f;
                grayint[i][j] = Math.round(gray);
                grayImage.red[i][j] = grayint[i][j];
                grayImage.blue[i][j] = grayint[i][j];
                grayImage.green[i][j] = grayint[i][j];
            }
        }

        return grayImage;
    }
    //finds all pixels within a certain range of colors and changes them to a different color
    public static PixImage changeColFilter(PixImage image, int[] original, int[] newCol, int tolerance){
        PixImage copy = new PixImage(image.height, image.width);
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                if (Math.abs(original[0]-image.red[i][j]) < tolerance && Math.abs(original[1]-image.green[i][j]) < tolerance && Math.abs(original[2]-image.blue[i][j]) < tolerance) {
                    copy.red[i][j] = newCol[0];
                    copy.green[i][j] = newCol[1];
                    copy.blue[i][j] = newCol[2];
                }
                else{
                    copy.red[i][j] = image.red[i][j];
                    copy.blue[i][j] = image.blue[i][j];
                    copy.green[i][j] = image.green[i][j];
                }
            }
        }
        return copy;
    }
    //stretches the image based on distance from top of the image. If the image is further from top it stretches the pixel more.
    public static PixImage stretchFilter(PixImage image){
        PixImage copy = new PixImage(image.height * 2, image.width);
        float k = 1.0f;
        float threshold = 0.5f;
        for (int i = 0; i < copy.height; i++) {
            int l = (int)(k*i);
            for (int j = 0; j < copy.width; j++) {
                copy.red[i][j] = image.red[l][j];
                copy.green[i][j] = image.green[l][j];
                copy.blue[i][j] = image.blue[l][j];
            }
            if (k > threshold) {
                k *= .999;
            }
        }
        return copy;
    }
    //blurs image by taking average of surroundings of each pixel
    public static PixImage blurFilter(PixImage image, int blurAmount) {
        PixImage copy = new PixImage(image);
        int[][] outImage = new int[copy.height][copy.width];
        PixImage blurImage = new PixImage(copy.height, copy.width);
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                blurImage.blue[i][j] = getAverageOfKernel(copy.blue, new int[]{i, j}, blurAmount);
                blurImage.red[i][j] = getAverageOfKernel(copy.red, new int[]{i, j}, blurAmount);
                blurImage.green[i][j] = getAverageOfKernel(copy.green, new int[]{i, j}, blurAmount);
            }
        }

        return blurImage;
    }
    //helper function that takes average of a group of pixels
    public static int getAverageOfKernel(int[][] color, int[] center, int intHalfSize){
        float average = 0;
        int startX = center[0] - intHalfSize < 0 ? 0 : center[0] - intHalfSize;
        int endX = center[0] + intHalfSize > color.length ? color.length : center[0] + intHalfSize;

        int startY = center[1] - intHalfSize < 0 ? 0 : center[1] - intHalfSize;
        int endY = center[1] + intHalfSize > color[0].length ? color[0].length : center[1] + intHalfSize;

        int pixelAmount = 0;
        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                average += color[i][j];
                pixelAmount += 1;
            }
        }
        average/=(float)pixelAmount;
        int intAvg = (int)average;

        return intAvg;
    }
    //sharpens pixels by multiplying three by three pixel groups by a certain matrix that brings out its features.
    public static PixImage sharpenFilter(PixImage image) {
        PixImage copy = new PixImage(image.height, image.width);
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                copy.blue[i][j] = SharpenKernel(image.blue, new int[]{i, j});
                copy.red[i][j] = SharpenKernel(image.red, new int[]{i, j});
                copy.green[i][j] = SharpenKernel(image.green, new int[]{i, j});
            }
        }

        return copy;
    }
    //helper function of sharpen filter
    public static int SharpenKernel(int[][] col, int[] center){
        int startX = center[0] - 1;
        int startY = center[1] - 1;
        int endX = center[0] + 2;
        int endY = center[1] + 2;

        int[][] kernelMatrix = new int[][]{new int[]{0, -1, 0}, new int[]{-1, 5, -1}, new int[]{0, -1, 0}};
        int out = 0;

        int kernelX = 0;


        for (int i = startX; i < endX; i++) {
            int kernelY = 0;
            for (int j = startY; j < endY; j++) {

                if (i >= 0 && i < col.length){
                    if (j >= 0 && j < col[i].length){
                        out += col[i][j] * kernelMatrix[kernelX][kernelY];
                    }
                    else{
                        out+=0;
                    }
                }
                else{
                    out+=0;
                }
                kernelY++;
            }
            kernelX++;
        }
        return out;

    }
    //simply scales the image to a new size by looking up the same pixel for a scaleXscale group of pixels
    public static PixImage scaleFilter(PixImage image, float scale){
        PixImage copy = new PixImage((int)(image.height * scale), (int)(image.width * scale));
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                copy.blue[i][j] = image.blue[(int)(i/scale)][(int)(j/scale)];
                copy.red[i][j] = image.red[(int)(i/scale)][(int)(j/scale)];
                copy.green[i][j] = image.green[(int)(i/scale)][(int)(j/scale)];
            }
        }
        return copy;
    }
    //this one is a little complicated. I used something called k-means-clustering which takes the colors of the pixel
    //and finds the average of its colors. If I specify that I want 5 unique colors, it basically finds which pixel colors,
    //are closest to the avg, then I update the avg color to be the avg of the closest pixels. I do this multiple times,
    //to get better results. The image output is something that looks like a cartoon.
    public static PixImage colorReduceFilter(PixImage image, int colorNum, int interations){
        PixImage copy = new PixImage(image);
        int[][] avgColors = new int[colorNum][3];
        for (int i = 0; i < colorNum; i++) {
            for (int j = 0; j < 3; j++) {
                avgColors[i][j] = (int)(Math.random()*255);
            }
        }

        HashMap<String, Integer> pixel2avgColIndex = new HashMap<>();
        HashMap<Integer, ArrayList<int[]>> avgCol2pixelIndex = new HashMap<>();
        for (int i = 0; i < colorNum; i++) {
            avgCol2pixelIndex.put(i, new ArrayList<>());
        }
        for (int o = 0; o < interations; o++) {
            for (int i = 0; i < copy.height; i++) {
                for (int j = 0; j < copy.width; j++) {
                    float closestDist = Float.MAX_VALUE;
                    for (int k = 0; k < colorNum; k++) {
                        int[] pixel = new int[]{copy.red[i][j], copy.green[i][j], copy.blue[i][j]};
                        float dist = getDist(pixel, avgColors[k]);

                        if (dist < closestDist){
                            closestDist = dist;
                            pixel2avgColIndex.put(i + ":" + j, k);
                        }
                    }
                }
            }
            for (int i = 0; i < copy.height; i++) {
                for (int j = 0; j < copy.width; j++) {
                    int k = pixel2avgColIndex.get(i+":"+j);
                    //System.out.println(k);
                    avgCol2pixelIndex.get(k).add(new int[]{i, j});
                }
            }
            for (int i = 0; i < colorNum; i++) {
                ArrayList<int[]> currPixelList = avgCol2pixelIndex.get(i);
                float[] avg = new float[] {0, 0, 0};
                for (int j = 0; j < currPixelList.size(); j++) {
                    avg[0] += copy.red[currPixelList.get(j)[0]][currPixelList.get(j)[1]];
                    avg[1] += copy.green[currPixelList.get(j)[0]][currPixelList.get(j)[1]];
                    avg[2] += copy.blue[currPixelList.get(j)[0]][currPixelList.get(j)[1]];
                }
                for (int j = 0; j < 3; j++) {
                    avgColors[i][j] = (int)(avg[j]/(float)currPixelList.size());
                }
            }
        }
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                copy.red[i][j] = avgColors[pixel2avgColIndex.get(i + ":" + j)][0];
                copy.green[i][j] = avgColors[pixel2avgColIndex.get(i + ":" + j)][1];
                copy.blue[i][j] = avgColors[pixel2avgColIndex.get(i + ":" + j)][2];
            }
        }
        //System.out.println(pixel2centroidIndex);
        return copy;

    }
    //helper function for colorReduction filter.
    public static float getDist(int[] a, int[] b){
        int total = 0;
        for (int i = 0; i < a.length; i++) {
            total += (a[i] -  b[i]) * (a[i] -  b[i]);
        }
        return (float)Math.sqrt(total);
    }
    //rotates an Image and mirrors it to give is a keleidascope effect.
    public static PixImage kaleidascopeFilter(PixImage image, int mirrorAmount){
        PixImage copy = new PixImage(image);
        for (int i = 0; i < mirrorAmount; i++) {
            copy = ImageFilters.rotateFilter(copy, (float)(i*Math.PI/mirrorAmount));
            copy = ImageFilters.mirrorFilter(copy);
            copy = ImageFilters.rotateFilter(copy, (float)(-i*Math.PI/mirrorAmount));
        }
        return copy;
    }
    //mirrors image by setting the first half's pixels to the other side's pixels
    public static PixImage mirrorFilter(PixImage image){
        PixImage copy = new PixImage(image);
        for (int i = 0; i < copy.height ; i++) {
            for (int j = 0; j <= copy.width/2; j++) {
                copy.blue[i][j] = image.blue[i][copy.width-1-j];
                copy.red[i][j] = image.red[i][copy.width-1-j];
                copy.green[i][j] = image.green[i][copy.width-1-j];
            }
        }
        return copy;
    }
    //converts index coords to xy coords and converts those to polar coords. Adds an angle which scales with distance to center
    // to the polar coords, and converts back to index coords.
    // Sets the new pixel index values to old pixel index values
    public static PixImage spiralFilter(PixImage image, float spiralStr) {
        PixImage copy = new PixImage(image.height, image.width);

        float angle = 0;
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                int y = i - copy.height/2;
                int x = j - copy.width/2;
                double theta = Math.atan2(y, x);
                double r = Math.sqrt(x*x + y*y);

                int halfWidth = copy.width/2;
                int halfHeight = copy.height/2;

                double maxR = Math.sqrt(halfWidth*halfWidth + halfHeight*halfHeight);

                angle = (float)((r/maxR) * Math.PI * spiralStr);

                int newI = (int) Math.round(r*Math.cos(theta+angle)) + (copy.width/2);
                int newJ = (int) Math.round(r * Math.sin(theta+angle)) + (copy.height/2);

                if (newI >= 0 && newI < copy.width){
                    if (newJ >= 0 && newJ < copy.height){
                        copy.red[i][j] = image.red[newJ][newI];
                        copy.blue[i][j] = image.blue[newJ][newI];
                        copy.green[i][j] = image.green[newJ][newI];
                    }
                }


            }
        }

        return copy;
    }
    //rotate the same way as the spiral filter but the angle does not scale with distance from center
    public static PixImage rotateFilter(PixImage image, float angle) {
        PixImage copy = new PixImage(image.height, image.width);
        for (int i = 0; i < copy.height; i++) {
            for (int j = 0; j < copy.width; j++) {
                int y = i - copy.height/2;
                int x = j - copy.width/2;
                double theta = Math.atan2(y, x);
                double r = Math.sqrt(x*x + y*y);

                int newX = (int) Math.round(r*Math.cos(theta+angle)) + (copy.width/2);
                int newY = (int) Math.round(r * Math.sin(theta+angle)) + (copy.height/2);

                if (newX >= 0 && newX < copy.width){
                    if (newY >= 0 && newY < copy.height){
                        copy.red[i][j] = image.red[newY][newX];
                        copy.blue[i][j] = image.blue[newY][newX];
                        copy.green[i][j] = image.green[newY][newX];
                    }
                }


            }
        }

        return copy;
    }
}
