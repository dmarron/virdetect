import java.util.*;
import java.io.*;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;

public class printBlastViralReads {
    public static void main(String[] args) {
	String sampleID = "sampleID";
	String inputFile = "";
	String outDir = "";
	int chimericCount = 0;
	if (args.length < 2) {
	    inputFile = "STAR_virus_Aligned.out.sam";
	    outDir = ".";
	    System.out.println("Usage: java printBlastViralReads inputFile outDir");
	    System.exit(1);
	} else {
	    inputFile = args[0];
	    outDir = args[1];
	}
	LinkedHashMap<String,ArrayList<Integer>> genomePos = new LinkedHashMap<String,ArrayList<Integer>>();
	LinkedHashMap<String,ArrayList<String>> genomeLines = new LinkedHashMap<String,ArrayList<String>>();
	
	try {
	    String outFile = outDir + "/blast_commands.txt";
	    BufferedWriter commandout = new BufferedWriter(new FileWriter(new File(outFile)));
	    SAMFileReader inputBam = new SAMFileReader(new File(inputFile));
	    inputBam.setValidationStringency(ValidationStringency.SILENT);
	    List<SAMSequenceRecord> allSequences = inputBam.getFileHeader().getSequenceDictionary().getSequences();
	    for (int i = 0; i < allSequences.size(); i++) {
		genomePos.put(allSequences.get(i).getSequenceName(),new ArrayList<Integer>());
		genomeLines.put(allSequences.get(i).getSequenceName(),new ArrayList<String>());
	    }
	    SAMRecordIterator iter = inputBam.iterator();
	    String previousGenome = "";
	    String previousmmID = "";
	    boolean matchNext = false;
            while (iter.hasNext()) {
                SAMRecord s = iter.next();
		String samString = s.getSAMString().trim();
		String[] splitString = samString.split("\t");
		ArrayList<String> toPut = genomeLines.get(splitString[2]);
		toPut.add(samString);
		genomeLines.put(splitString[2],toPut);
		ArrayList<Integer> toPutPos = genomePos.get(splitString[2]);
		toPutPos.add(Integer.parseInt(splitString[3]));
		genomePos.put(splitString[2],toPutPos);
            }
	    iter.close();
	    inputBam.close();
	    //write genome counts
	    String tabValues = "";
	    for (String key : genomePos.keySet()) {
		ArrayList<Integer> posList = genomePos.get(key);
		ArrayList<Integer> points = new ArrayList<Integer>();
		if (posList.size() > 4) {
		    Collections.sort(posList);
		    int max = posList.get(posList.size()-1);
		    int min = posList.get(0);
		    int mid = (max-min)/2;
		    int q1 = (mid-min)/2;
		    int q2 = (max-mid)/2;
		    points.add(min);
		    if (max != min) {
			max = posList.size()-1;
			min = 0;
			mid = (max-min)/2;
			q1 = (mid-min)/2;
			q2 = (max-mid)/2 + mid;
			if (q1 != posList.get(0)) {
			    points.add(posList.get(q1));
			}
			if (mid != q1) {
			    //points.add(posList.get(mid));
			}
			if (q2 != q1) {
			    points.add(posList.get(q2));
			}
			if (max != q2) {
			    points.add(posList.get(max));
			}
		    }
		} else {
		    for (int p = 0; p < posList.size(); p++) {
			if (!points.contains(posList.get(p))) {
			    points.add(posList.get(p));
			}
		    }
		}

		ArrayList<String> lineList = genomeLines.get(key);
		for (int p = 0; p < points.size(); p++) {
		    int point = points.get(p);
		    int lpoint = 0;
		    for (int s = 0; s < lineList.size(); s++) {
			String line = lineList.get(s);
			lpoint = Integer.parseInt(line.split("\t")[3]);
			if (lpoint == point) {
			    //System.out.println(lpoint);
			    //System.out.println(lineList.get(s));
			    String virus = line.split("\t")[2];
			    if (virus.indexOf("|") != -1) {
				String[] pipeSplit = virus.split("\\|");
				virus = pipeSplit[pipeSplit.length - 1];
				if (virus.equals("virus") || virus.equals("vector") || virus.equals("dan") || virus.equals("univec")) {
				    virus = pipeSplit[pipeSplit.length - 2];
				}
			    }
			    BufferedWriter out = new BufferedWriter(new FileWriter(new File(outDir + "/" + virus + "_" + (p+1) + ".tfa")));
			    out.write(line.split("\t")[9]);
			    out.newLine();
			    out.close();
			    BufferedWriter lineout = new BufferedWriter(new FileWriter(new File(outDir + "/" + virus + "_" + (p+1) + ".txt")));
			    lineout.write(line);
			    lineout.newLine();
			    lineout.close();
			    //String workdir = "/datastore/nextgenout4/share/labs/bioinformatics/malawi/blast_test";
			    String workdir = outDir;
			    String command = "/home/parkerjs/software/ncbi-blast-2.3.0+-src/c++/ReleaseMT/bin/blastn -query " + workdir + "/" + virus + "_" + (p+1) + ".tfa" + " -db /datastore/nextgenout4/share/labs/bioinformatics/Selitsky/BLAST/nt/nt -out " + workdir + "/" + virus + "_" + (p+1) + ".out";
			    commandout.write(command);
			    commandout.newLine();
			    System.out.println(command);
			    /*try {
				Process proc = Runtime.getRuntime().exec(command);
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String errline = null;
				while ( (errline = br.readLine()) != null)
				    System.out.println(errline);
				int exitVal = proc.waitFor();
				System.out.println("process finished with exit value: " + exitVal);
				//Process process = new ProcessBuilder("/home/parkerjs/software/ncbi-blast-2.3.0+-src/c++/ReleaseMT/bin/blastn","param1","param2").start();
			    } catch (Throwable t) {
				t.printStackTrace();
			    }*/
			    break;
			}
		    }
		}
		//tabValues = tabValues + "\t" + points;
		
	    }
	    //System.out.println("Pos: " + tabValues);
	    //tabValues = "";
	    //for (String key : genomeLines.keySet()) {
	    //tabValues = tabValues + "\n" + genomeLines.get(key);
	    //}
	    //System.out.println("Lines: " + tabValues);

	    commandout.close();

	} catch (Exception e) {
	    System.out.println(e);
	}
    }
}
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
	    {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line=null;
		while ( (line = br.readLine()) != null)
		    System.out.println(type + ">" + line);    
            } catch (IOException ioe)
	    {
                ioe.printStackTrace();  
	    }
    }
}