import java.io.*;
import java.util.*;

public class maskGenome {
    public static HashMap<String,ArrayList<Integer>> bedMap;
    public static int insert;
    public static int readlength;
    public static int prob;
    public static void main(String[] args) {
	//mask genome based on bed file
	if (args.length != 3) {
	    System.out.println("Error with command line arguments");
	    System.out.println("example: java maskGenome unaligned.bed virus.fa output.fa");
	    System.exit(0);
	}
	
	try {
	    bedMap = new HashMap<String,ArrayList<Integer>>();

	    BufferedReader bedReader = new BufferedReader(new FileReader(new File(args[0])));
	    BufferedReader faReader = new BufferedReader(new FileReader(new File(args[1])));
	    BufferedWriter out1 = new BufferedWriter(new FileWriter(new File(args[2])));
	    readlength = 50;
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
	    while (true) {
		line = faReader.readLine();
		if (line == null) {
		    break;
		}
		if (line.indexOf(">") != 0) {
		    if (bedMap.containsKey(strain)) {
			ArrayList<Integer> myList = bedMap.get(strain);
			//System.out.println(strain);
			//System.out.println(line.length());
			for (int i = 0; i < myList.size(); i++) {
			    int maskStart = myList.get(i);
			    if (maskStart + 50 < line.length()) {
				line = line.substring(0,maskStart) + "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + line.substring(maskStart + 50);
			    } else if (maskStart < line.length()) {
				//System.out.println(line);
				//System.out.println(line.length());
				//System.out.println(maskStart);
				//int diff = maskStart + 50 - line.length();
				//System.out.println(diff);
				//String addN = "";
				//for (int n = 0; n < diff; n++) {
				//    addN += 'N';
				//}
				line = line.substring(0,maskStart);
			    }
			}
			double ncount = 0;
			double count = 0;
			for (int i = 0; i < line.length(); i++) {
			    if (line.charAt(i) == 'N') {
				ncount++;
			    } else {
				count ++;
			    }
			}
			System.out.println(strain + " is " + ncount*100/(count + ncount) + "% masked");
			//System.out.println(line);
			//System.out.println(line.length());
			out1.write(line);
			out1.newLine();
		    } else {
			out1.write(line);
                        out1.newLine();
		    }
                } else {
		    if (line.indexOf("NC_") != -1 && line.length() > 1) {
			strain = line.substring(1).split("NC_")[0] + "NC_" + line.split("NC_")[1].split("\\|")[0] + "|";

			//strain = line.substring(1);

			//strain = ">" + line.substring(1).split("NC_")[0] + "NC_" + line.split("NC_")[1].split("\\|")[0] + "|" + line.split("NC_")[1].split("\\|")[1];
		    } else {
			strain = line.substring(1);
			//System.out.println(strain);
		    }
		    //System.out.println(strain);
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
