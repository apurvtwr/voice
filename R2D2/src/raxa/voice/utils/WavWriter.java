/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package raxa.voice.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * 
 * @author apurv
 *	Addon util to write audio to a file without having to look into the details of 
 *	what format each time ... gossh..
 *
 */


public class WavWriter {

	private File file;
	private AudioFileFormat fileFormat;
	
	private AudioInputStream audioStream;
	
	public WavWriter(String fileName, InputStream stream) {
		this(fileName, 8000, 16, 1, false, false, stream, 10);
	}
	
	/**
	 * 
	 * @param fileName path of file to write audio into
	 * @param sampleRate Sample rate of audio
	 * @param sampleSize size of individual channels 
	 * @param channels Number of channels in the audio stream. typically 1 or 2
	 * @param signed signed or unsigned??
	 * @param bigEndian whether it is a big-endian file or not ... eh
	 * @param stream input stream that contains the audio data 
	 * @param length  Length in seconds of the audio to save
	 */
	public WavWriter (String fileName, int sampleRate,
			int sampleSize, int channels, boolean signed, 
			boolean bigEndian, InputStream stream, int length) {
		this.file = new File(fileName);	
		this.audioStream = new AudioInputStream(stream, new AudioFormat(
				sampleRate, sampleSize, channels, signed, bigEndian), length * sampleRate);
	}
	
	public WavWriter(String fileName, AudioInputStream stream){
		this.file = new File(fileName);
		this.audioStream = stream;
	}
	
	public boolean write() throws IOException {
		AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
		return true;
	}	
}

