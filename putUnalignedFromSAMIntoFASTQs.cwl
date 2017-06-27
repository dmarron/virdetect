cwlVersion: v1.0

class: CommandLineTool

requirements:
 - class: DockerRequirement
    dockerPull: unclbg/linux:v1
     - class: InlineJavascriptRequirement

     hints:
       ResourceRequirement:
           ramMin: 50000

	   baseCommand: /home/crunch/putUnalignedFromSAMIntoFASTQs.sh

	   inputs:
	     samFile:
	         type: File
		     inputBinding:
		           position: 1

			   outputs:
			     unaligned_1:
			         type: File
				     outputBinding:
				           glob: unaligned_1.fastq
					     unaligned_2:
					         type: File
						     outputBinding:
						           glob: unaligned_2.fastq

							   