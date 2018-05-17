# Nearly optimal Natural Mergesort &mdash; Code
Code for experiments with nearly optimally adaptive mergesort variants 
peeksort and powersort.

## Reproducing the results from the paper

To reproduce the running time study from the paper,
execute 

    ant package
    ./paper-experiments.sh

The build requires a recent JDK 8, Oracle's version is recommended.

Make sure to use the paper release:  
[![DOI](https://zenodo.org/badge/132030229.svg)](https://zenodo.org/badge/latestdoi/132030229)



This produces several files in the current directory.

 * The *.out files show the progress made in the individual runs 
and contain debug output from JVM's just-in-time compiler. 
It can be used to check that no massive deoptimization steps happened during the timed
experiments. (Endless output during the warmup phase and occasional printed lines 
during timed runs are normal.)
 * The *.csv files contain one line per executed sort and report the individual
running time. These files were used in the paper to compute average and standard deviations
of running times.


## Unit Tests

To run harness tests for correctness of the sorting methods, run

    ant test

