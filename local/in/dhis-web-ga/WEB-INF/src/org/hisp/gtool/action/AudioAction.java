package org.hisp.gtool.action;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioAction 
{
	static AudioFormat audioFormat;
	static AudioInputStream audioInputStream;
	static SourceDataLine sourceDataLine;
	static boolean stopPlayback = false;
	String audioFilePath = "C:\\Program Files\\DHIS2\\AudioFiles\\";
	
	static PlayThread t1 = new PlayThread();
	
	public void stopAudio()
	{
		stopPlayback =  true;
		System.out.println("Audio is Stopped");
	}
	
	public void playAudio(String audioFileName) 
	{						
		try
	    {									
			while(t1.isAlive()) 
				{System.out.println("Thread is live"); stopAudio();}
			System.out.println("Thread is Dead");
			stopPlayback =  false;
			String audioFile = audioFilePath + audioFileName;
			File soundFile = new File(audioFile);
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioFormat = audioInputStream.getFormat();
			System.out.println("AudioFormat : "+audioFormat);
	      
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,audioFormat);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);

			//new PlayThread().start();			
			t1 = new PlayThread();			
			t1.start();			
	    }
	    catch (Exception e) { System.out.println(e.getMessage()); }
	  }//end playAudio


//	=============================================//
//	Inner class to play back the data from the
//	 audio file.
	static class PlayThread extends Thread
	{
	  byte tempBuffer[] = new byte[10000];

	  public void run()
	  {
		  try
		  {
			  sourceDataLine.open(audioFormat);
			  sourceDataLine.start();

			  int cnt;
			  while((cnt = audioInputStream.read(
					  tempBuffer,0,tempBuffer.length)) != -1
	                       && stopPlayback == false){
				  		if(cnt > 0) sourceDataLine.write(tempBuffer, 0, cnt);	      
			  }//end while	      	      	      
		  }// try block end
		  catch (Exception e) { System.out.println(e.getMessage());}
		  finally
		  {
			  sourceDataLine.drain();
			  sourceDataLine.close();
			  stopPlayback = false;
		  }
	  }//end run
	}//end inner class PlayThread
			
}// class end
