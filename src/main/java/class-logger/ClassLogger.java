import java.io.FileWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.image.Raster;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassLogger
{
	public static void main(String[] args)
		throws IOException
	{
		// args0 image
		// args1 class
		// args2 output file
		Path imagePath = Paths.get( args[0] );

		FileWriter writer = new FileWriter( args[2], true );
		BufferedImage image = ImageIO.read( imagePath.toFile() );
		Raster raster = image.getData();
		writer.write( args[1] + "," + imagePath.getFileName().toString() + "," );
		for(int x=0; x<raster.getWidth(); ++x)
		{
			for(int y=0; y<raster.getHeight(); ++y)
			{
				writer.write(
					raster.getSample(x, y, 0) + " "
				);
			}
		}
		writer.write("\n");
		writer.close();
	} // end of main
} // end of class
