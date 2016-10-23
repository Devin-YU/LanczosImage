package LanczosImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LanczosTest {

	public static void main(String[] args) throws IOException {
		LanczosFliter lanczosFliter = new LanczosFliter();
		String str1 = "SourcePicture.jpg";
		BufferedImage buf = null;
		
		buf = lanczosFliter.imageScale(str1, 2f, 2f);
		File destFile1 = new File("ZoomOutPicture.jpg");
		ImageIO.write(buf, "jpg", destFile1);
		
		buf = lanczosFliter.imageScale(str1, 0.5f, 0.5f);
		File destFile2 = new File("ZoomInPicture.jpg");
		ImageIO.write(buf, "jpg", destFile2);
		

	}

}
