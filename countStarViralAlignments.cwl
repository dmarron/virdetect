cwlVersion: v1.0

class: CommandLineTool

requirements:
 - class: DockerRequirement
    dockerPull: unclbg/java
     - class: InlineJavascriptRequirement

     hints:
       ResourceRequirement:
           coresMin: 8
	       ramMin: 50000

	       baseCommand: java

	       arguments:
	         - -Xmx4G
		   - prefix: -cp
		       valueFrom: /home/crunch:/home/crunch/*
		         - countStarViralAlignments

			 inputs:
			   sampleName:
			       type: string
			           inputBinding:
				         position: 1
					   inputSAM:
					       type: File
					           inputBinding:
						         position: 2
							   outputFileName:
							       type: string
							           inputBinding:
								         position: 3

									 outputs:
									   viralReadCountsFile:
									       type: File
									           outputBinding:
										         glob: $(inputs.outputFileName)
											 