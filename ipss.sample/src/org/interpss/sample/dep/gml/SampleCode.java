package org.interpss.sample.dep.gml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class SampleCode {
	public static void main(String[] args) throws Exception {
		PipedInputStream input = new PipedInputStream();
		PipedOutputStream output = new PipedOutputStream();
		output.connect(input);
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
		writer.append("aaaa");
		writer.flush();
		writer.close();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		System.out.println(reader.readLine());
	}
}
