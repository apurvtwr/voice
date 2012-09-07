package raxa.demo;

import java.io.FileNotFoundException;
import java.io.IOException;

import raxa.nlp.tools.TagsReader;

public class Demo {

	public static void main(String args[]) throws IOException {
		TagsReader reader = new TagsReader("./resource/1.txt", "#STD");
		reader.start();
		//System.out.println("Testing");
	}
}
