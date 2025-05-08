class=org.apache.commons.lang3.math.NumberUtils
project_cp=/home/gzhang/CS383/Project/bug1/target/classes
D4J_SEED=42
budget=60
DIR_OUTPUT=out/

java -cp evosuite-shaded-1.1.0.jar shaded.org.evosuite.EvoSuite \
    -class $class \
    -projectCP $project_cp \
    -seed $D4J_SEED \
    -Dsearch_budget=$budget \
    -Dtest_dir=$DIR_OUTPUT \
    -criterion branch \
    -Dshow_progress=false \
    -Djunit_check=false \
    -Dfilter_assertions=false \
    -Dtest_comments=false \
    -mem 1500