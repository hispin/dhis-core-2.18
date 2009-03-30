package org.hisp.dhis.gis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class SVGUtils
{

    public static void convertSVG2PNG( String inputPath, String outputPath, double w, double h )
    {
        // Create a JPEG transcoder
        PNGTranscoder t = new PNGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint( PNGTranscoder.KEY_HEIGHT,  new Float(h) );
        t.addTranscodingHint( PNGTranscoder.KEY_WIDTH, new Float(w)  );

        try
        {
            // Create the transcoder input.
            String svgURI = new File( inputPath ).toURL().toString();
            TranscoderInput input = new TranscoderInput( svgURI );
            // Create the transcoder output.
            OutputStream ostream = new FileOutputStream( outputPath );
            TranscoderOutput output = new TranscoderOutput( ostream );

            // Save the image.
            t.transcode( input, output );

            // Flush and close the stream.
            ostream.flush();
            ostream.close();
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
        catch ( FileNotFoundException e )
        {            
            e.printStackTrace();
        }
        catch ( TranscoderException e )
        {           
            e.printStackTrace();
        }
        catch ( IOException e )
        {            
            e.printStackTrace();
        }

    }

}
