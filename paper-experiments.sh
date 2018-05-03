#! /bin/sh
PREFIX="taskset -c 0 java -XX:+UnlockDiagnosticVMOptions -XX:-TieredCompilation -XX:+PrintCompilation -jar out/nearly-optimal-mergesort.jar "
SEED=248442268

echo Running study 1 - Random Permutations
$PREFIX 1001 10_000,100_000,1_000_000 $SEED rp        times-rp-small       > times-rp-small.out
$PREFIX 9001          10_000          $SEED rp        times-rp-10k         > times-rp-10k.out
$PREFIX  201      10_000_000          $SEED rp        times-rp-10m         > times-rp-10m.out
$PREFIX   21     100_000_000          $SEED rp        times-rp-100m        > times-rp-100m.out

echo Running study 2 - sqrt-n runs
$PREFIX  201      10_000_000          $SEED runs3000    times-runs3k-10m   > times-runs3k-10m.out

echo Running study 3 - timdrag input
$PREFIX  201      16_777_216          $SEED timdrag32   times-timdrag32-16m > times-timdrag32-16m.out
