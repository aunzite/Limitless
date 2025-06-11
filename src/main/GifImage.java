/////////////////////////////////////////////////////////////////////////////
// Limitless
// GifImage.java
// 
// Description: Handles GIF image loading and animation including:
// - Frame loading and management
// - Animation timing
// - Frame rendering
// - Resource cleanup
/////////////////////////////////////////////////////////////////////////////

package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Handles GIF image loading and animation
public class GifImage {
    // Animation state
    private List<BufferedImage> frames;          // Array of animation frames
    private List<Integer> delays;                // Delay between frames in milliseconds
    private long lastFrameTime;      // Last frame update time
    private int currentFrame = 0;    // Current frame index
    
    // Image properties
    private int width;               // Image width
    private int height;              // Image height
    
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
                return;
            }
            
            ImageInputStream input = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            
            if (!readers.hasNext()) {
                return;
            }
            
            ImageReader reader = readers.next();
            reader.setInput(input);
            
            int numFrames = reader.getNumImages(true);
            width = -1;
            height = -1;
            
            for (int i = 0; i < numFrames; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);
                int delay = getDelayFromMetadata(metadata);
                delays.add(delay);
                
                if (width == -1 || height == -1) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
                
                frames.add(image);
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