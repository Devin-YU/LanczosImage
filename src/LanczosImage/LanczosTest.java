package LanczosImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LanczosTest {

	public static void main(String[] args) throws IOException {
		LanczosFliter lanczosFliter = new LanczosFliter();
		String str1 = "picture1.jpg";
		String str2 = "picture2.png";
		String str3 = "picture3.bmp";
		BufferedImage buf = null;
		
		buf = lanczosFliter.imageScale(str1, 2f, 2f);
		File destFile1 = new File("LanczosZoomPicture.jpg");
		ImageIO.write(buf, "jpg", destFile1);
		
		buf = lanczosFliter.imageScale(str2, 0.5f, 1.5f);
		File destFile2 = new File("LanczosZoomPicture.png");
		ImageIO.write(buf, "png", destFile2);
		
		buf = lanczosFliter.imageScale(str3, 0.5f, 0.5f);
		File destFile3 = new File("LanczosZoomPicture.bmp");
		ImageIO.write(buf, "bmp", destFile3);

	}

}
