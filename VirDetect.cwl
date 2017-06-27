cwlVersion: v1.0

class: Workflow

requirements:
  - class: InlineJavascriptRequirement
    - class: MultipleInputFeatureRequirement
      - class: StepInputExpressionRequirement

      inputs:
        fastqs:
	    type:
	          type: array
		        items: File
			  referenceGenome: Directory
			    viralReferenceGenome: Directory
			      sampleName: string

			      outputs:
			        viral_counts:
				    type: File
				        outputSource: countStarViralAlignments/viralReadCountsFile
					  viral_alignment:
					      type: File
					          outputSource: star_second/star_sam_file


						  steps:

						    star_first:
						        in:
							      genomeDir: referenceGenome
							            readFilesCommand:
								            default: "zcat"
									          readFilesIn: fastqs
										        runThreadN:
											        default: 16
												      outFilterMultimapNmax:
												              default: 1080
													            outSAMunmapped:
														            default: "Within"
															          outFileNamePrefix:
																          default: "STAR_"
																	      out: [star_sam_file]
																	          run: STAR.cwl

																		    unalignedToFASTQs:
																		        in:
																			      samFile: star_first/star_sam_file
																			          out: [unaligned_1, unaligned_2]
																				      run: putUnalignedFromSAMIntoFASTQs.cwl

																				        star_second:
																					    in:
																					          genomeDir: viralReferenceGenome
																						        readFilesIn: [unalignedToFASTQs/unaligned_1,unalignedToFASTQs/unaligned_2]
																							      limitOutSAMoneReadBytes:
																							              default: 1000000
																								            outFilterMismatchNmax:
																									            default: 5
																										          outFilterMultimapNmax:
																											          default: 52
																												        outFileNamePrefix:
																													        default: "STAR_virus_"
																														      runThreadN:
																														              default: 16
																															          out: [star_sam_file]
																																      run: STAR.cwl

																																        countStarViralAlignments:
																																	    in:
																																	          sampleName: sampleName
																																		        inputSAM: star_second/star_sam_file
																																			      outputFileName:
																																			              default: viral_read_counts
																																				          out: [viralReadCountsFile]
																																					      run: countStarViralAlignments.cwl
																																					      