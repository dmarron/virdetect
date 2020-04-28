import java.io.*;
import java.util.*;

public class simulateReads {
    public static HashMap<String,String> gafMap;
    public static HashMap<Integer,String> snpMap;
    public static int insert;
    public static int readlength;
    public static int prob;
    public static void main(String[] args) {

	if (args.length != 2 && args.length != 3) {
	    System.out.println("simulateReads - simulate reads of length 75 from input fa file");
	    System.out.println("Usage: java -cp simulateReads.jar simulateReads virus.fa sim.fa");
	    System.out.println("Input: fa file of virus/vector sequences virus.fa");
	    System.out.println("Output: fa file of simulated reads sim.fa");
	    System.out.println("Use optional third parameter to customize read length (default 75)");
	    System.out.println("java -cp simulateReads.jar simulateReads virus.fa sim.fa 100");
	    System.exit(0);
	}
	
	try {
	    gafMap = new HashMap<String,String>();
	    snpMap = new HashMap<Integer,String>();

	    BufferedReader transReader = new BufferedReader(new FileReader(new File(args[0])));
	    BufferedWriter out1 = new BufferedWriter(new FileWriter(new File(args[1])));
	    readlength = 75;
	    if (args.length == 3) {
		readlength = Integer.parseInt(args[2]);
	    }
	    String line;
	    String strain = "";
	    String currentRead = "";
	    String wholeSeq = "";
	    //make reads from transcripts
	    while (true) {
		line = transReader.readLine();
		if (line == null) {
		    if (wholeSeq != "") {
			for (int i = 0; i < wholeSeq.length() - readlength + 1; i++) {
			    currentRead = wholeSeq.substring(i,i+readlength);
			    out1.write(strain+":"+(i+1)+":"+(i+1+readlength)+"/1");
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
		    break;
		}
		if (line.indexOf(">") == 0) {
		    if (wholeSeq != "") {
			for (int i = 0; i < wholeSeq.length() - readlength + 1; i++) {
			    currentRead = wholeSeq.substring(i,i+readlength);
			    out1.write(strain+":"+(i+1)+":"+(i+1+readlength)+"/1");
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
		    strain = "@" + line.substring(1);
		    wholeSeq = "";
		} else {
		    if (strain == "") {
			System.out.println("Error with virus strain name.  Virus strain names must start with '>' in the fa file");
			System.exit(-1);
		    }
		    wholeSeq = wholeSeq + line;
		}
	    }
	    out1.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }
}
