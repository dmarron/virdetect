cwlVersion: v1.0

class: CommandLineTool

requirements:
 - class: DockerRequirement
    dockerPull: unclbg/star
     - class: InlineJavascriptRequirement

     hints:
       ResourceRequirement:
           coresMin: 8
	       ramMin: 50000

	       baseCommand: /STAR-STAR_2.4.2a/bin/Linux_x86_64_static/STAR

	       inputs:
	         genomeDir:
		     type: Directory
		         inputBinding:
			       position: 1
			             prefix: "--genomeDir"
				       limitOutSAMoneReadBytes:
				           type: ["null", int]
					       inputBinding:
					             position: 1
						           prefix: "--limitOutSAMoneReadBytes"
							     outFileNamePrefix:
							         type: string
								     inputBinding:
								           position: 1
									         prefix: "--outFileNamePrefix"
										   outSAMtype:
										       type:
										             type: array
											           items: string
												       default: [SAM,Unsorted]
												           inputBinding:
													         position: 1
														       prefix: "--outSAMtype"
														         outFilterMismatchNmax:
															     type: ["null", int]
															         inputBinding:
																       position: 1
																             prefix: "--outFilterMismatchNmax"
																	       outFilterMultimapNmax:
																	           type: ["null", int]
																		       inputBinding:
																		             position: 1
																			           prefix: "--outFilterMultimapNmax"
																				     outSAMunmapped:
																				         type: ["null", string]
																					     inputBinding:
																					           position: 1
																						         prefix: "--outSAMunmapped"
																							   quantMode:
																							       type: ["null", string]
																							           inputBinding:
																								         position: 1
																									       prefix: "--quantMode"
																									         readFilesCommand:
																										     type: ["null", string]
																										         inputBinding:
																											       position: 1
																											             prefix: "--readFilesCommand"
																												       readFilesIn:
																												           type:
																													         type: array
																														       items: File
																														           inputBinding:
																															         position: 1
																																       prefix: "--readFilesIn"
																																         runThreadN:
																																	     type: ["null", int]
																																	         inputBinding:
																																		       position: 1
																																		             prefix: "--runThreadN"

																																			     outputs:
																																			       star_bam_file:
																																			           type: ["null", File]
																																				       outputBinding:
																																				             glob: $(inputs.outFileNamePrefix)Aligned.out.bam
																																					       star_sam_file:
																																					           type: ["null", File]
																																						       outputBinding:
																																						             glob: $(inputs.outFileNamePrefix)Aligned.out.sam
																																							       star_transcriptome_bam_file:
																																							           type: ["null",File]
																																								       outputBinding:
																																								             glob: $(inputs.outFileNamePrefix)Aligned.toTranscriptome.out.bam
																																									     