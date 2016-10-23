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
	 * PNG, JPG AND BMP ONLY.
	 */
	public BufferedImage imageScale(String pathName, float widthScale, float heightScale) throws IOException {
		if (!pathName.endsWith("png") && !pathName.endsWith("jpg") && !pathName.endsWith("bmp")) {
			throw new RuntimeException("Wrrong File!");
		}
		File file = new File(pathName);
		BufferedImage bufferedImage = ImageIO.read(file);
		
		widthScale = 1/widthScale;
		heightScale = 1/heightScale;
		lanczosSize = widthScale > 1 ? 3 : 2;
		int srcW = bufferedImage.getWidth();
		int srcH = bufferedImage.getHeight();
		int destW = (int)(bufferedImage.getWidth() / widthScale);
		int destH = (int)(bufferedImage.getHeight() / heightScale);
		
		int[] inPixels = bufferedImage.getRGB(0, 0, srcW, srcH, null, 0, srcW);
		int[] outPixels = new int[destW * destH];

		for (int col = 0; col < destW; col++) {
			
			
			double x = col * widthScale; 
			double fx = (double)Math.floor(col * widthScale);
			for (int row = 0; row < destH; row ++) {
				
				double y = row * heightScale;
				double fy = (double)Math.floor(y);
				double[] argb = {0, 0, 0, 0};
				int[] pargb = {0, 0, 0, 0};
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
                            for (int i = 0; i < 4; i++)
                            	pargb[i] = (inPixels[index] >> 24 - 8 * i) & 0xff;
                            totalWeight += weight; 
                            for (int i = 0; i < 4; i++)
                            	argb[i] += weight * pargb[i];
						}
					}
				}
				for (int i = 0; i < 4; i++)
					pargb[i] = (int)(argb[i] / totalWeight);
				outPixels[row * destW + col] = (clamp(pargb[0]) << 24) |
						(clamp(pargb[1]) << 16) |
						(clamp(pargb[2]) << 8) |
						clamp(pargb[3]);
			}
		}
		
		BufferedImage bufImg = new BufferedImage(destW, destH,
				pathName.endsWith("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		
		bufImg.setRGB(0, 0, destW, destH, outPixels, 0, destW);
		return bufImg;
	}
    private int clamp(int v)  
    {  
        return v > 255 ? 255 : (v < 0 ? 0 : v);  
    }  
  
    private double getLanczosFactor(double x) {  
        if (x >= lanczosSize)  
            return 0;   
        if (Math.abs(x) < 1e-16)  
            return 1;  
        x *= Math.PI; 
        return Math.sin(x) * Math.sin(x/lanczosSize) / (x*x);  
    }  
}
