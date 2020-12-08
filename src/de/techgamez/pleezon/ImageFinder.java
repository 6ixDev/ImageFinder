package de.techgamez.pleezon;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFinder
{

    private double minColorSimilarity;
    private double minPixelSimilarity;
    private BufferedImage bi;

    public ImageFinder(final String path) throws IOException {
        this.minColorSimilarity = 1.0;
        this.minPixelSimilarity = 1.0;
        this.bi = ImageIO.read(new File(path));
    }
    public ImageFinder(final BufferedImage bufferedImage) throws IOException {
        this.minColorSimilarity = 1.0;
        this.minPixelSimilarity = 1.0;
        this.bi = bufferedImage;
    }
    public ImageFinder(final String path, final double minColorSimilarity, final double minPixelSimilarity, final double scaleX, final double scaleY) throws IOException {
        this.minColorSimilarity = 1.0;
        this.minPixelSimilarity = 1.0;
        this.minColorSimilarity = minColorSimilarity;
        this.minPixelSimilarity = minPixelSimilarity;
        this.bi = ImageIO.read(new File(path));
        if (scaleX != 1.0 || scaleY != 1.0) {
            final int newWidth = new Double(this.bi.getWidth() * scaleX).intValue();
            final int newHeight = new Double(this.bi.getHeight() * scaleY).intValue();
            final BufferedImage resized = new BufferedImage(newWidth, newHeight, this.bi.getType());
            final Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(this.bi, 0, 0, newWidth, newHeight, 0, 0, this.bi.getWidth(), this.bi.getHeight(), null);
            g.dispose();
            this.bi = deepCopy(resized);
        }
    }
    public ImageFinder(final BufferedImage bufferedImage, final double minColorSimilarity, final double minPixelSimilarity, final double scaleX, final double scaleY) throws IOException {
        this.minColorSimilarity = 1.0;
        this.minPixelSimilarity = 1.0;
        this.minColorSimilarity = minColorSimilarity;
        this.minPixelSimilarity = minPixelSimilarity;
        this.bi = bufferedImage;
        if (scaleX != 1.0 || scaleY != 1.0) {
            final int newWidth = new Double(this.bi.getWidth() * scaleX).intValue();
            final int newHeight = new Double(this.bi.getHeight() * scaleY).intValue();
            final BufferedImage resized = new BufferedImage(newWidth, newHeight, this.bi.getType());
            final Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(this.bi, 0, 0, newWidth, newHeight, 0, 0, this.bi.getWidth(), this.bi.getHeight(), null);
            g.dispose();
            this.bi = deepCopy(resized);
        }
    }

    public ImageFinderChild find() throws AWTException, PictureNotFoundOnScreenException {
        final long startTime = System.nanoTime();
        final Point pt = this.findOnScreen(this.bi);
        final long elapsedTime = System.nanoTime() - startTime;
        return new ImageFinderChild(pt, new Point((int)pt.getX() + this.bi.getWidth() / 2, (int)pt.getY() + this.bi.getHeight() / 2), elapsedTime / 1000000.0f);
    }

    public ImageFinderChild find(final BufferedImage image) throws AWTException, PictureNotFoundOnScreenException {
        final long startTime = System.nanoTime();
        final Point pt = this.findOnScreen(this.bi, image);
        final long elapsedTime = System.nanoTime() - startTime;
        return new ImageFinderChild(pt, new Point((int)pt.getX() + this.bi.getWidth() / 2, (int)pt.getY() + this.bi.getHeight() / 2), elapsedTime / 1000000.0f);
    }

    public ImageFinderChild find(final String path) throws AWTException, PictureNotFoundOnScreenException, IOException {
        final long startTime = System.nanoTime();
        final Point pt = this.findOnScreen(this.bi, ImageIO.read(new File(path)));
        final long elapsedTime = System.nanoTime() - startTime;
        return new ImageFinderChild(pt, new Point((int)pt.getX() + this.bi.getWidth() / 2, (int)pt.getY() + this.bi.getHeight() / 2), elapsedTime / 1000000.0f);
    }

    private Point findOnScreen(final BufferedImage bi) throws AWTException {
        return this.findOnScreen(bi, new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
    }

    public boolean isTransparent(int rgb) {
        return (rgb>>24) == 0x00;
    }

    private Point findOnScreen(final BufferedImage bi, final BufferedImage image) throws AWTException, PictureNotFoundOnScreenException {
        int totalColorMisses = 0;
        for (int x = 0; x < image.getWidth() - bi.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight() - bi.getHeight(); ++y) {
                boolean invalid = false;
                int k = x;
                for (int a = 0; a < bi.getWidth(); ++a) {
                    int l = y;
                    for (int b = 0; b < bi.getHeight(); ++b) {
                        ++totalColorMisses;
                        int rgb=image.getRGB(k, l);
                        if ((!isTransparent(rgb)) && (!this.isPixelEqual(bi.getRGB(a, b), rgb) && totalColorMisses > (1.0 - this.minPixelSimilarity) * (a * b))) {
                            invalid = true;
                            break;
                        }
                        ++l;
                    }
                    if (invalid) {
                        break;
                    }
                    ++k;
                }
                if (!invalid) {
                    return new Point(x, y);
                }
            }
        }
        throw new PictureNotFoundOnScreenException("Image not found on screen.");
    }

    private boolean isPixelEqual(final int col1, final int col2) {
        double dif = getDifferenceInPercent(new Color(col1), new Color(col2));
        boolean found = 1d-dif >= this.minColorSimilarity;
        if(found) {
//    		System.out.println("found - " + col1 + " | " + col2 + " = 1 - " + String.format("%f", dif) +  " >= " + this.minColorSimilarity);
        }
        return found;
    }

    private double getDifferenceInPercent(Color a, Color b) {
        double perc1 = Math.round(((a.getRed()/2.55d)+(a.getGreen()/2.55d)+(a.getBlue()/2.55d))/3);
        double perc2 = Math.round(((b.getRed()/2.55d)+(b.getGreen()/2.55d)+(b.getBlue()/2.55d))/3);
        return Math.abs(perc1 - perc2) / 100d;
    }

    static BufferedImage deepCopy(final BufferedImage bi) {
        final ColorModel cm = bi.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
