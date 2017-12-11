import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import java.lang.Math;
import java.awt.Color;

// References: 
//  - https://class.coursera.org/algs4partII-007/assignment/view?assignment_id=9
//  - http://coursera.cs.princeton.edu/algs4/assignments/seamCarving.html
//  - http://coursera.cs.princeton.edu/algs4/checklists/seamCarving.html

public class SeamCarver {

    private Picture picture;

    private int BODER_PIXEL_ENERGY = 1000;

    private double[][] energy_matrix;

    private class Point { 
        public int edgeX ;
        public double distance = Double.POSITIVE_INFINITY;

        public Point() {}
        public Point(int thisEdgeX, double thisDistance) {
            edgeX = thisEdgeX;
            distance = thisDistance;
        }
    }

    private class AcyclicSP {

        private Point[][] points;

        private double[][] matrix;

        private int width, height, minX;

        public AcyclicSP(double[][] myMatrix) {
            if(myMatrix == null)
                throw new java.lang.NullPointerException();
            if(myMatrix.length < 1)
                throw new java.lang.IndexOutOfBoundsException();
            matrix = myMatrix;

            width = matrix.length;
            height = matrix[0].length;
            points = new Point[width][height];

            for (int x = 0; x < width; x++) 
                points[x][0] = new Point(0, BODER_PIXEL_ENERGY);

            for (int x = 0; x < width; x++)
                for (int y = 1; y < height; y++) 
                    points[x][y] = new Point();

            relax_verticies();

            //print_distances();

            // find the shortest path:
            minX = -1;
            Point minPoint = null;
            for(int x = 0; x < width; x++) {
                Point p = points[x][height - 1];
                if(minPoint == null || minPoint.distance > p.distance) {
                    minPoint = p;
                    minX = x;
                }
            }

            //StdOut.printf("AcyclicSP constructed: %d minX\n", minX);
        }

        private void print_distances() {
            StdOut.println("Distances:"); 
            for(int y = 0; y < height; y++) {
                StdOut.print(y + ": "); 
                for(int x = 0; x < width; x++) {
                    StdOut.print(" " + points[x][y].distance + " "); 
                }
                StdOut.println(""); 
            }

        }

        private void relax_verticies() {
            for(int y = 0; y < height - 1; y++) {
                for(int x = 0; x < width; x++) {

                    // adjacent vertices below:
                    assert y + 1 <= height;
                    int y2 = y + 1;
                    int x2Start = x - 1;
                    x2Start = x2Start < 0 ? 0 : x2Start;

                    for(int x2 = x2Start; x2 < width &&  x2 <= x + 1; x2++) {
                        relax(x, y, x2, y2);
                    }
                }
            }
        }

        private void relax(int x, int y, int x2, int y2) {
            Point from = points[x][y];
            Point to = points[x2][y2];
            double edgeEnergy = matrix[x2][y2];
            if (to.distance > from.distance + edgeEnergy) {
                to.distance = from.distance + edgeEnergy;
                to.edgeX = x;
            }       
        }

        public double length() {
            if(minX < 0)
                return Double.POSITIVE_INFINITY;
            assert height >= 0;
            assert width >= 0;
            assert minX < width;
            return points[minX][height - 1].distance;
        }

        public int[] path() {
            if(minX < 0)
                return null;
            assert height >= 0;
            assert width >= 0;
            assert minX < width;

            int[] path = new int[height];
            int x = minX;
            Point p;
            for(int y = height - 1; y >= 0; y--) {
                path[y] = x;
                p = points[x][y];
                x = p.edgeX;                
            }
            return path;
        }
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture myPicture) {
        if(myPicture == null)
            throw new java.lang.NullPointerException();
        picture = myPicture;
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validate_pixel(x, y);

        if(x == 0 || x == width() - 1) 
            return BODER_PIXEL_ENERGY;
        if(y == 0 || y == height() - 1) 
            return BODER_PIXEL_ENERGY;

        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        Color bottom = picture.get(x, y - 1);
        Color top = picture.get(x, y + 1);
       
        double xDelta = 
            Math.pow(left.getRed() - right.getRed(), 2) +
            Math.pow(left.getGreen() - right.getGreen(), 2) +
            Math.pow(left.getBlue() - right.getBlue(), 2);

        double yDelta = 
            Math.pow(top.getRed() -   bottom.getRed(), 2) +
            Math.pow(top.getGreen() - bottom.getGreen(), 2) +
            Math.pow(top.getBlue() -  bottom.getBlue(), 2);
        return Math.sqrt(xDelta + yDelta);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        construct_energy_matrix_flipped();
        AcyclicSP sp = new AcyclicSP(energy_matrix);
        return sp.path();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        construct_energy_matrix();
        AcyclicSP sp = new AcyclicSP(energy_matrix);
        return sp.path();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if(seam == null)
            throw new java.lang.NullPointerException();

        if(seam.length != width())
            throw new java.lang.IllegalArgumentException();

        if(picture.height() <= 1)
            throw new java.lang.IllegalArgumentException("Height of picture is less or equal to 1");

        assert picture != null;
        Picture newP = new Picture(width(), height() - 1);

        int previousSeamEntry = -1;
        for(int x = 0; x < width(); x++) {
            for(int y = 0; y < height(); y++) {
                if(seam[x] < 0 || seam[x] >= height())
                    throw new java.lang.IllegalArgumentException();

                if(previousSeamEntry != -1 && Math.abs(seam[x] - previousSeamEntry) > 1)
                    throw new java.lang.IllegalArgumentException();

                if(y < seam[x])
                    newP.set(x, y, picture.get(x, y));
                else if(seam[x] < y)
                    newP.set(x, y - 1, picture.get(x, y));
                previousSeamEntry = seam[x];
            }
        }
        picture = newP;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if(seam == null)
            throw new java.lang.NullPointerException();

        if(seam.length != height())
            throw new java.lang.IllegalArgumentException();

        if(picture.width() <= 1)
            throw new java.lang.IllegalArgumentException("Width of picture is less or equal to 1");

        assert picture != null;
        Picture newP = new Picture(width() - 1, height());

        int previousSeamEntry = -1;
        for(int y = 0; y < height(); y++) {
            for(int x = 0; x < width(); x++) {
                if(seam[y] < 0 || seam[y] >= width())
                    throw new java.lang.IllegalArgumentException();

                if(previousSeamEntry != -1 && Math.abs(seam[y] - previousSeamEntry) > 1)
                    throw new java.lang.IllegalArgumentException();

                if(x < seam[y])
                    newP.set(x, y, picture.get(x, y));
                else if(seam[y] < x)
                    newP.set(x - 1, y, picture.get(x, y));
                previousSeamEntry = seam[y];
            }
        }
        picture = newP;
    }

    private void construct_energy_matrix() {
        energy_matrix = new double[width()][height()];
        for(int x = 0; x < width(); x++) 
            for(int y = 0; y < height(); y++) 
                energy_matrix[x][y] = energy(x, y);
    }

    private void construct_energy_matrix_flipped() {
        energy_matrix = new double[height()][width()];
        for(int x = 0; x < width(); x++) 
            for(int y = 0; y < height(); y++) 
                energy_matrix[y][x] = energy(x, y);
    }


    private void validate_pixel(int x, int y) {
        if(x < 0 || width() <= x) 
            throw new java.lang.IndexOutOfBoundsException("Invalid pixel x value: " + x);
        if(y < 0 || height() <= y) 
            throw new java.lang.IndexOutOfBoundsException("Invalid pixel y value: " + y);
    }

}
