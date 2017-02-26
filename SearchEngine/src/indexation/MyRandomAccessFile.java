package indexation;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MyRandomAccessFile {

	private String filepath;// = "C:/Users/nikos7/Desktop/input.txt";
	public MyRandomAccessFile(String filepath){
		this.filepath = filepath;
	}	

	public byte[] readFromFile(int position, int size)
			throws IOException {

		RandomAccessFile file = new RandomAccessFile(filepath, "r");
		file.seek(position);
		byte[] bytes = new byte[size];
		file.read(bytes);
		file.close();
		return bytes;

	}
	public void writeToFile(String data, int position)
			throws IOException {

		RandomAccessFile file = new RandomAccessFile(filepath, "rw");
		file.seek(position);
		file.write(data.getBytes());
		file.close();

	}
}