import java.io.*;
import java.util.*;

public class maskGenome {
    public static HashMap<String,ArrayList<Integer>> bedMap;
    public static int insert;
    public static int readlength;
    public static int prob;
    public static void main(String[] args) {
	//mask genome based on bed file
	if (args.length != 3 && args.length != 4) {
	    System.out.println("Usage: java maskGenome unaligned.bed virus.fa output.fa");
	    System.out.println("Optional fourth parameter read length (default 75):");
	    System.out.println("java maskGenome unaligned.bed virus.fa output.fa 100");
	    System.exit(0);
	}
	
	try {
	    bedMap = new HashMap<String,ArrayList<Integer>>();

	    BufferedReader bedReader = new BufferedReader(new FileReader(new File(args[0])));
	    BufferedReader faReader = new BufferedReader(new FileReader(new File(args[1])));
	    BufferedWriter out1 = new BufferedWriter(new FileWriter(new File(args[2])));
	    readlength = 75;
	    if (args.length == 4) {
		readlength = Integer.parseInt(args[3]);
	    }
	    String line;
	    String previousLine = "";

	    //make reads from transcripts
	    while (true) {
		line = bedReader.readLine();
		if (line == null) {
		    break;
		}
		StringTokenizer token = new StringTokenizer(line);
		String ID = token.nextToken();
		if (!token.hasMoreTokens()) {
		    System.out.println("ID: " + ID);
		}
		int min = Integer.parseInt(token.nextToken());
		int max = Integer.parseInt(token.nextToken());
		if (bedMap.containsKey(ID)) {
		    ArrayList<Integer> toAdd = bedMap.get(ID);
		    for (int i = min; i <= max; i++) {
                        toAdd.add(new Integer(i));
                    }
                    bedMap.put(ID,toAdd);
		    //System.out.println(ID);
		} else {
		    ArrayList<Integer> toAdd = new ArrayList<Integer>();
		    for (int i = min; i <= max; i++) {
			toAdd.add(new Integer(i));
		    }
		    bedMap.put(ID,toAdd);
		}
	    }
	    String strain = "";
	    String seq = "";
	    while (true) {
		line = faReader.readLine();
		if (line == null) {
		    if (bedMap.containsKey(strain)) {
			ArrayList<Integer> myList = bedMap.get(strain);
			for (int i = 0; i < myList.size(); i++) {
			    int maskStart = myList.get(i);
			    if (maskStart + readlength < seq.length()) {
				String addN = "";
				for (int n = 0; n < readlength; n++) {
				    addN += 'N';
				}
				seq = seq.substring(0,maskStart) + addN + seq.substring(maskStart + readlength);
			    } else if (maskStart < seq.length()) {
				seq = seq.substring(0,maskStart);
			    }
			}
			double ncount = 0;
			double count = 0;
			for (int i = 0; i < seq.length(); i++) {
			    if (seq.charAt(i) == 'N') {
				ncount++;
			    } else {
				count ++;
			    }
			}
			System.out.println(strain + " is " + ncount*100/(count + ncount) + "% masked");
			out1.write(seq);
			out1.newLine();
		    } else {
			out1.write(seq);
                        out1.newLine();
		    }
		    break;
		}
		if (line.indexOf(">") != 0) {
		    seq = seq + line;
                } else {
		    if (bedMap.containsKey(strain)) {
			ArrayList<Integer> myList = bedMap.get(strain);
			for (int i = 0; i < myList.size(); i++) {
			    int maskStart = myList.get(i);
			    if (maskStart + readlength < seq.length()) {
				String addN = "";
				for (int n = 0; n < readlength; n++) {
				    addN += 'N';
				}
				seq = seq.substring(0,maskStart) + addN + seq.substring(maskStart + readlength);
			    } else if (maskStart < seq.length()) {
				seq = seq.substring(0,maskStart);
			    }
			}
			double ncount = 0;
			double count = 0;
			for (int i = 0; i < seq.length(); i++) {
			    if (seq.charAt(i) == 'N') {
				ncount++;
			    } else {
				count ++;
			    }
			}
			System.out.println(strain + " is " + ncount*100/(count + ncount) + "% masked");
			out1.write(seq);
			out1.newLine();
		    } else if (seq != "") {
			out1.write(seq);
                        out1.newLine();
		    }
		    seq = "";
		    strain = line.substring(1);
		    out1.write(line);
		    out1.newLine();
		}
	    }
	    out1.close();
	} catch (Exception e) {
	    e.printStackTrace(System.out);
	    //System.out.println(e);
	}
    }
}
