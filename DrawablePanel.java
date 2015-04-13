import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class DrawablePanel extends JPanel
{
    Image image1;
    
  public DrawablePanel(Image image)
  {
    super();
  
    image1 = image;
  }
 
  protected void paintComponent(Graphics g) {
    g.drawImage(image1, 0, 0, getWidth(), getHeight(), this);
  }
}