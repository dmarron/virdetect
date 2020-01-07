import java.io.*;
import java.util.*;

public class simulateReads {
    public static HashMap<String,String> gafMap;
    public static HashMap<Integer,String> snpMap;
    public static int insert;
    public static int readlength;
    public static int prob;
    public static void main(String[] args) {
	//parameters:
	//transcripts.fa - make PER reads from this transcript
	//output fa name: such as PE_reads_ will make PE_reads_1.fa and PE_reads_2.fa
       
	//example: java addSNPMismatch viral_filtered.fa sim_reads_

	if (args.length != 2) {
	    System.out.println("Error with command line arguments");
	    System.out.println("example: java simulateReads virus.fa simulated_reads.fa");
	    System.exit(0);
	}
	
	try {
	    gafMap = new HashMap<String,String>();
	    snpMap = new HashMap<Integer,String>();

	    BufferedReader transReader = new BufferedReader(new FileReader(new File(args[0])));
	    BufferedWriter out1 = new BufferedWriter(new FileWriter(new File(args[1])));
	    readlength = 50;
	    String line;
	    String previousLine = "";

	    //make reads from transcripts
	    while (true) {
		line = transReader.readLine();
		if (line == null) {
		    break;
		}
		if (line.indexOf(">") != 0) {
		    //line is a transcript sequence and previous line is identifier
		    String currentRead = "";
		    String mateRead = "";
		    for (int i = 0; i < line.length() - readlength + 1; i++) {
			currentRead = line.substring(i,i+readlength);
			out1.write(previousLine+":"+(i+1)+":"+(i+1+readlength)+"/1");
			out1.newLine();
			out1.write(currentRead);
			out1.newLine();
			out1.write("+");
			out1.newLine();
			String qualityPlaceHolder = "";
			for (int o = 0; o < readlength; o++) {
			    qualityPlaceHolder += "Q";
			}
			out1.write(qualityPlaceHolder);
			out1.newLine();
		    }
		}
		previousLine = line;
		previousLine = "@" + previousLine.substring(1);
	    }
	    out1.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }
    public static String reverse(String input) {
	return new StringBuffer(input).reverse().toString();
    }
    public static String compliment(String input) {
	String output = "";
	for (int i = 0; i < input.length(); i++) {
	    if (input.charAt(i) == 'A') {
		output += "T";
	    } else if (input.charAt(i) == 'T') {
		output += "A";
	    } else if (input.charAt(i) == 'C') {
		output += "G";
	    } else if (input.charAt(i) == 'G') {
		output += "C";
	    } else {
		output += "N";
	    }
	}
	return output;
    }
}