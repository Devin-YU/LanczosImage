package LanczosImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LanczosFliter {
	private int lanczosSize = 2;
	/*
	 * This method is used to zoom in or zoom out a picture, which is based
	 * on Lanczos Algorithm.
	 * When 0 < widthScale, heightScale < 1 , the picture will be zoomed in.
	 * When 1 < widthScale, heightScale, the picture will be zoomed out.
	 * PNG, JPG AND BMP ONLY 
	 */
	public BufferedImage imageScale(String fileName, float widthScale, float heightScale) {
		if (!fileName.endsWith("png") && !fileName.endsWith("jpg") && !fileName.endsWith("bmp")) {
			throw new RuntimeException("Wrrong File!");
		}
		File file = new File(fileName);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		widthScale = 1/widthScale;
		heightScale = 1/heightScale;
		lanczosSize = widthScale > 1 ? 3 : 2;
		int srcW = bufferedImage.getWidth();
		int srcH = bufferedImage.getHeight();
		int destW = (int)(bufferedImage.getWidth() / widthScale);
		int destH = (int)(bufferedImage.getHeight() / heightScale);
		
		int[] inPixels = getImageRGBArray(bufferedImage);
		int[] outPixels = new int[destW * destH];
		
		double x = 0, fx = 0, y = 0, fy = 0;
		for (int col = 0; col < destW; col++) {
			int pr = 0, pg = 0, pb = 0, pa = 0;
			
			x = col * widthScale; 
			fx = (double)Math.floor(col * widthScale);
			for (int row = 0; row < destH; row ++) {
				
				y = row * heightScale;
				fy = (double)Math.floor(y);
				double red = 0, green = 0, blue = 0, alpha = 0;
				double totalWeight = 0;
				
				for (int subrow = (int)(fy - lanczosSize + 1); subrow <= fy + lanczosSize; subrow++) {
					if (subrow < 0 || subrow >= srcH) 
						continue;
					
					for (int subcol = (int)(fx - lanczosSize + 1); subcol <= fx + lanczosSize; subcol++) {
						if (subcol < 0 || subcol >= srcW)  
	                        continue;
						
						double weight = getLanczosFactor(x - subcol) * getLanczosFactor(y - subrow);
						
						if (weight > 0) {  
                            int index = (subrow * srcW + subcol); 
                            pa = (inPixels[index] >> 24) & 0xff;
                            pr = (inPixels[index] >> 16) & 0xff;  
                            pg = (inPixels[index] >> 8) & 0xff;  
                            pb = inPixels[index] & 0xff;  
                            totalWeight += weight; 
                            alpha += weight * pa;
                            red += weight * pr;  
                            green += weight * pg;  
                            blue += weight * pb;  
						}
					}
				}
				pa = (int)(alpha / totalWeight);
				pb = (int)(blue / totalWeight);
				pr = (int)(red / totalWeight);
				pg = (int)(green / totalWeight);
				outPixels[row * destW + col] = (clamp(pa) << 24) |
						(clamp(pr) << 16) |
						(clamp(pg) << 8) |
						clamp(pb);
				
				alpha = 0;
				blue = 0;
				red = 0;
				green = 0;
				totalWeight = 0;
			}
		}
		
		BufferedImage bufImg = new BufferedImage(destW, destH,
				fileName.endsWith("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < destH; i++) {
			for (int j = 0; j < destW; j++) {
				bufImg.setRGB(j, i, outPixels[i * destW + j]);
			}
		}
		return bufImg;
	}
    private int clamp(int v)  
    {  
        return v > 255 ? 255 : (v < 0 ? 0 : v);  
    }  
  
    private double getLanczosFactor(double x) {  
        if (x > lanczosSize)  
            return 0;   
        if (Math.abs(x) < 1e-16)  
            return 1;  
        x *= Math.PI; 
        return Math.sin(x) * Math.sin(x/lanczosSize) / (x*x);  
    }  
	private int[] getImageRGBArray(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		int[] RGBArray = new int[width * height];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				RGBArray[i * width + j] = img.getRGB(j, i);
			}
		}
		
		return RGBArray;
	}
}
