package main;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GifImage {
    private List<BufferedImage> frames;
    private List<Integer> delays;
    private long lastFrameTime;
    private int currentFrame;
    private int width;
    private int height;
    
    public GifImage(String path) {
        frames = new ArrayList<>();
        delays = new ArrayList<>();
        lastFrameTime = System.currentTimeMillis();
        currentFrame = 0;
        loadGif(path);
    }
    
    private void loadGif(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("GIF file not found: " + path);
                return;
            }
            
            ImageInputStream input = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            
            if (!readers.hasNext()) {
                System.err.println("No image reader found for GIF");
                return;
            }
            
            ImageReader reader = readers.next();
            reader.setInput(input);
            
            int numFrames = reader.getNumImages(true);
            BufferedImage master = null;
            width = -1;
            height = -1;
            
            for (int i = 0; i < numFrames; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);
                int delay = getDelayFromMetadata(metadata);
                delays.add(delay);
                
                // Get frame position (x, y)
                int x = 0, y = 0;
                try {
                    String[] names = metadata.getMetadataFormatNames();
                    for (String name : names) {
                        if (name.equals("javax_imageio_gif_image_1.0")) {
                            Node root = metadata.getAsTree(name);
                            NodeList children = root.getChildNodes();
                            for (int j = 0; j < children.getLength(); j++) {
                                Node node = children.item(j);
                                if (node.getNodeName().equals("ImageDescriptor")) {
                                    x = Integer.parseInt(node.getAttributes().getNamedItem("imageLeftPosition").getNodeValue());
                                    y = Integer.parseInt(node.getAttributes().getNamedItem("imageTopPosition").getNodeValue());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore, use default (0,0)
                }
                
                if (width == -1 || height == -1) {
                    width = image.getWidth();
                    height = image.getHeight();
                    master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                }
                
                // Compose onto master
                Graphics2D g2 = master.createGraphics();
                g2.drawImage(image, x, y, null);
                g2.dispose();
                
                // Copy the composed image for this frame
                BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2copy = copy.createGraphics();
                g2copy.drawImage(master, 0, 0, null);
                g2copy.dispose();
                frames.add(copy);
                
                // Handle disposal method (restore to background)
                try {
                    String[] names = metadata.getMetadataFormatNames();
                    for (String name : names) {
                        if (name.equals("javax_imageio_gif_image_1.0")) {
                            Node root = metadata.getAsTree(name);
                            NodeList children = root.getChildNodes();
                            for (int j = 0; j < children.getLength(); j++) {
                                Node node = children.item(j);
                                if (node.getNodeName().equals("GraphicControlExtension")) {
                                    String disposal = node.getAttributes().getNamedItem("disposalMethod").getNodeValue();
                                    if (disposal.equals("restoreToBackgroundColor")) {
                                        Graphics2D g2clear = master.createGraphics();
                                        g2clear.setComposite(java.awt.AlphaComposite.Clear);
                                        g2clear.fillRect(x, y, image.getWidth(), image.getHeight());
                                        g2clear.dispose();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            reader.dispose();
            input.close();
            
        } catch (IOException e) {
            System.err.println("Error loading GIF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private int getDelayFromMetadata(IIOMetadata metadata) {
        try {
            String[] names = metadata.getMetadataFormatNames();
            for (String name : names) {
                if (name.equals("javax_imageio_gif_image_1.0")) {
                    Node root = metadata.getAsTree(name);
                    NodeList children = root.getChildNodes();
                    
                    for (int j = 0; j < children.getLength(); j++) {
                        Node node = children.item(j);
                        if (node.getNodeName().equals("GraphicControlExtension")) {
                            return Integer.parseInt(node.getAttributes().getNamedItem("delayTime").getNodeValue()) * 10;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading frame delay: " + e.getMessage());
        }
        return 100; // Default delay if not found
    }
    
    public BufferedImage getCurrentFrame() {
        if (frames.isEmpty()) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= delays.get(currentFrame)) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }
        
        return frames.get(currentFrame);
    }
} 