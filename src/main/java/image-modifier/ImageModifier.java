import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;

public class ImageModifier
{
	public static void grey(BufferedImage original)
	{
		int width = original.getWidth();
		int height = original.getHeight();

		for(int y=0; y<original.getHeight(); ++y)
		{
			for(int x=0; x<original.getWidth(); ++x)
			{
				Color color = new Color( original.getRGB(x, y) );

				int red = (int)(color.getRed() * 0.21);
				int green = (int)(color.getGreen() * 0.72);
				int blue = (int)(color.getBlue() * 0.07);

				int sum = red + blue + green;

				Color newColor = new Color(sum, sum, sum);
				original.setRGB( 
					x, 
					y, 
					newColor.getRGB() 
				);
			}
		}
	}

	public static BufferedImage resize(
		BufferedImage original,
		int scaledWidth,
		int scaledHeight,
		boolean alpha)
	{
		int imageType = ( alpha ) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		
		BufferedImage scaledImage = new BufferedImage(
			scaledWidth, 
			scaledHeight,
			imageType					
		);

		Graphics2D graphics = scaledImage.createGraphics();
		if ( alpha )
		{
			graphics.setComposite( AlphaComposite.Src );
		}
		graphics.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
		graphics.dispose();
		return scaledImage;
	}

	public static void main ( String[] args )
		throws IOException
	{
		int scaledWidth = 180;
		int scaledHeight = 200;
		// get images
		List<Path> paths = Files.walk( Paths.get( args[0] ) )
			.filter( Files::isRegularFile )
			.collect( Collectors.toList( ) );

		for ( Path path : paths )
		{
			
			BufferedImage image = resize( 
				ImageIO.read( 
					path.toFile()
				),
				scaledWidth,
				scaledHeight,
				true
			);
			grey( image );
			ImageIO.write(
				image, 
				"jpg", 
				new File(
					path.getFileName().toString()
				)
			);
		}
	} // end of main
} // end of class
