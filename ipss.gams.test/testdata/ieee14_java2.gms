
$Title trnsport model using gdx files
$EOLCOM //

$include 'ieee14_java.inc'

        Parameter A(i) weighting factor A for generation cost function;
        Parameter B(i) weighting factor B for generation cost function;
        Parameter C(i) weighting factor C for generation cost function;
                 
// These lines execute during the compilation phase
$GDXIN inputdata.gdx                  // open data file
$LOAD  A=FactorA, B=FactorB, c=FactorC                       
$GDXIN   

        Variables
                 p(i)     generation output for each generating unit
                 ft       total generation cost ;

        Equations
                 cost            define objective function
                 powerBalance    real power balance of the whole network
                 lowerLimit(i)   Lower generation output bound
                 upperLimit(i)   upper generation output bound ;

        cost..               ft =e= sum(i,  A(i)+B(i)*p(i)+C(i)*p(i)*p(i)) ;

        powerBalance ..      sum(i, p(i)) =g= sum(j,d(j)) ;
        lowerLimit(i)..      p(i) =g= lower(i) ;
        upperLimit(i)..      p(i) =l= upper(i);


        Model transport /all/ ;

        solve transport using nlp minimizing ft ;
        
        display p.l, p.m ;
        
// These lines execute during the execution phase
execute_unload 'ieee14_results.gdx',p=result;  // write variable x to the gdx file