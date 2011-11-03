
package cz.witzany.gamev2.graphics.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.opengl.GL11;

public class TGALoader {
    private static final ByteBuffer uTGAcompare;  // Uncompressed TGA Header
    private static final ByteBuffer cTGAcompare;  // Compressed TGA Header

    static {
        byte[] uncompressedTgaHeader = new byte[]{0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] compressedTgaHeader = new byte[]{0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        uTGAcompare = ByteBuffer.allocateDirect(uncompressedTgaHeader.length);
        uTGAcompare.put(uncompressedTgaHeader);  // Uncompressed TGA Header
        uTGAcompare.flip();

        cTGAcompare = ByteBuffer.allocateDirect(compressedTgaHeader.length);
        cTGAcompare.put(compressedTgaHeader);  // Compressed TGA Header
        cTGAcompare.flip();
    }

    // Load a TGA file
    public static void loadTGA(Texture texture, String filename) throws IOException        
    {
        ByteBuffer header = ByteBuffer.allocateDirect(12);
        ReadableByteChannel in = Channels.newChannel(new FileInputStream(filename));
        readBuffer(in, header);

        // See if header matches the predefined header of
        if (uTGAcompare.equals(header))  
        {   // an Uncompressed TGA image
            // If so, jump to Uncompressed TGA loading code
            loadUncompressedTGA(texture, in);  
        } else if (cTGAcompare.equals(header))    
        {            
            // See if header matches the predefined header of
            // an RLE compressed TGA image
            // If so, jump to Compressed TGA loading code
            loadCompressedTGA(texture, in); 
        } else        // If header matches neither type
        {
            in.close();
            // Display an error
            throw new IOException("TGA file be type 2 or type 10 ");  

        }
    }

    private static void readBuffer(ReadableByteChannel in, ByteBuffer buffer) 
            throws IOException {
        while (buffer.hasRemaining()) {
            in.read(buffer);
        }
        buffer.flip();
    }

    private static void loadUncompressedTGA(Texture texture, ReadableByteChannel in) 
            throws IOException  
            // Load an uncompressed TGA (note, much of this code is based on NeHe's
    {  // TGA Loading code nehe.gamedev.net)
        TGA tga = new TGA();
        readBuffer(in, tga.header);

        // Determine The TGA width  (highbyte*256+lowbyte)
        texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + 
                unsignedByteToInt(tga.header.get(0));  
        // Determine The TGA height  (highbyte*256+lowbyte)
        texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + 
                unsignedByteToInt(tga.header.get(2));          
        // Determine the bits per pixel
        texture.bpp = unsignedByteToInt(tga.header.get(4));
        tga.width = texture.width;  // Copy width into local structure
        tga.height = texture.height;  // Copy height into local structure
        tga.bpp = texture.bpp;    // Copy BPP into local structure

        if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && 
                (texture.bpp != 32)))  // Make sure all information is valid
        {
            throw new IOException("Invalid texture information"); // Display Error
        }

        if (texture.bpp == 24)    // If the BPP of the image is 24...
            texture.type = GL11.GL_RGB;  // Set Image type to GL_RGB
        else        // Else if its 32 BPP
            texture.type = GL11.GL_RGBA;  // Set image type to GL_RGBA

        tga.bytesPerPixel = (tga.bpp / 8); // Compute the number of BYTES per pixel
        
        // Compute the total amout ofmemory needed to store data
        tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);    
        // Allocate that much memory
        texture.imageData = ByteBuffer.allocateDirect(tga.imageSize);  

        readBuffer(in, texture.imageData);

        for (int cswap = 0; cswap < tga.imageSize; cswap += tga.bytesPerPixel) {
            byte temp = texture.imageData.get(cswap);
            texture.imageData.put(cswap, texture.imageData.get(cswap + 2));
            texture.imageData.put(cswap + 2, temp);
        }
    }

    private static void loadCompressedTGA(Texture texture, ReadableByteChannel fTGA) 
            throws IOException    // Load COMPRESSED TGAs
    {
        TGA tga = new TGA();
        readBuffer(fTGA, tga.header);

        // Determine The TGA width  (highbyte*256+lowbyte)
        texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + 
                unsignedByteToInt(tga.header.get(0));          
        // Determine The TGA height  (highbyte*256+lowbyte)        
        texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + 
                unsignedByteToInt(tga.header.get(2));          
        texture.bpp = unsignedByteToInt(tga.header.get(4)); // Determine Bits Per Pixel
        tga.width = texture.width;  // Copy width to local structure
        tga.height = texture.height;  // Copy width to local structure
        tga.bpp = texture.bpp;    // Copy width to local structure

        if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && 
                (texture.bpp != 32)))  //Make sure all texture info is ok
        {
            // If it isnt...Display error
            throw new IOException("Invalid texture information");  
        }

        if (texture.bpp == 24)    // If the BPP of the image is 24...
            texture.type = GL11.GL_RGB;  // Set Image type to GL_RGB
        else        // Else if its 32 BPP
            texture.type = GL11.GL_RGBA;  // Set image type to GL_RGBA

        tga.bytesPerPixel = (tga.bpp / 8); // Compute BYTES per pixel
        // Compute amout of memory needed to store image
        tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);    
        // Allocate that much memory
        texture.imageData = ByteBuffer.allocateDirect(tga.imageSize);          
        texture.imageData.position(0);
        texture.imageData.limit(texture.imageData.capacity());

        int pixelcount = tga.height * tga.width; // Nuber of pixels in the image
        int currentpixel = 0;  // Current pixel being read
        int currentbyte = 0;  // Current byte
        // Storage for 1 pixel
        ByteBuffer colorbuffer = ByteBuffer.allocateDirect(tga.bytesPerPixel);      

        do {
            int chunkheader;  // Storage for "chunk" header
            try {
                ByteBuffer chunkHeaderBuffer = ByteBuffer.allocateDirect(1);
                fTGA.read(chunkHeaderBuffer);
                chunkHeaderBuffer.flip();
                chunkheader = unsignedByteToInt(chunkHeaderBuffer.get());
            } catch (IOException e) {
                throw new IOException("Could not read RLE header");  // Display Error
            }

            // If the ehader is < 128, it means the that is the number of 
            // RAW color packets minus 1
            if (chunkheader < 128)    
            {      // that follow the header
                chunkheader++;  // add 1 to get number of following color values
                // Read RAW color values
                for (short counter = 0; counter < chunkheader; counter++)    
                {
                    readBuffer(fTGA, colorbuffer);
                    // write to memory
                    // Flip R and B vcolor values around in the process
                    texture.imageData.put(currentbyte, colorbuffer.get(2));  
                    texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
                    texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

                    if (tga.bytesPerPixel == 4)  // if its a 32 bpp image
                    {
                        // copy the 4th byte
                        texture.imageData.put(currentbyte + 3, colorbuffer.get(3));  
                    }

                    // Increase thecurrent byte by the number of bytes per pixel
                    currentbyte += tga.bytesPerPixel;  
                    currentpixel++;    // Increase current pixel by 1

                    // Make sure we havent read too many pixels
                    if (currentpixel > pixelcount) 
                    {
                        // if there is too many... Display an error!
                        throw new IOException("Too many pixels read");      
                    }
                }
            } else
            {
                // chunkheader > 128 RLE data, next color reapeated chunkheader - 127 times
                chunkheader -= 127;  // Subteact 127 to get rid of the ID bit
                readBuffer(fTGA, colorbuffer);

                // copy the color into the image data as many times as dictated
                for (short counter = 0; counter < chunkheader; counter++)          
                {  // by the header
                    texture.imageData.put(currentbyte, colorbuffer.get(2));  
                    texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
                    texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

                    if (tga.bytesPerPixel == 4) // if its a 32 bpp image
                    {
                        // copy the 4th byte
                        texture.imageData.put(currentbyte + 3, colorbuffer.get(3));  
                    }

                    // Increase current byte by the number of bytes per pixel
                    currentbyte += tga.bytesPerPixel;  
                    currentpixel++;  // Increase pixel count by 1

                    // Make sure we havent written too many pixels
                    if (currentpixel > pixelcount) 
                    {
                        // if there is too many... Display an error!
                        throw new IOException("Too many pixels read");      
                    }
                }
            }
        } while (currentpixel < pixelcount); // Loop while there are still pixels left
    }

    private static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
    

	static class TGA {
	    // First 6 Useful Bytes From The Header
	    ByteBuffer header = ByteBuffer.allocateDirect(6);                
	    // Holds Number Of Bytes Per Pixel Used In The TGA File
	    int bytesPerPixel;              
	    // Used To Store The Image Size When Setting Aside Ram
	    int imageSize;                
	    int temp;    // Temporary Variable
	    int type;           
	    int height;    // height of Image
	    int width;    // width ofImage
	    int bpp;    // Bits Per Pixel
	}
}