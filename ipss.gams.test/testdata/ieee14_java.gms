$Title trnsport model using gdx files
$EOLCOM //
        Sets
                 i   generating unit  / 0001, 0002, 0003, 0006,0008 /
                 j   load bus         / 0002, 0003,0004,  0005, 0006, 0009,
                                        0010, 0011,  0012, 0013, 0014 /

        Parameters
                lower(i) lower  bound of each generating unit
                         /       0001        0.02
                                 0002        0.03
                                 0003        0.05
                                 0006        0.06
                                 0008        0.07   /
                upper(i) upper bound of each generating unit
                         /       0001        2.7
                                 0002        0.5
                                 0003        0.5
                                 0006        0.5
                                 0008        0.5/

                 d(j) demand at load bus j
                         /       0002         0.217
                                 0003         0.942
                                 0004         0.478
                                 0005         0.076
                                 0006         0.112
                                 0009         0.295
                                 0010         0.09
                                 0011         0.035
                                 0012         0.061
                                 0013         0.135
                                 0014         0.149/ ;

        Parameter A(i) weighting factor A for generation cost function

                 /0001           500
                 0002            300
                 0003            400
                 0006            100
                 0008            50       / ;
        Parameter    B(i) weighting factor B for generation cost function

                 /0001            30
                 0002            10
                 0003            70
                 0006            50
                 0008            40 /;
         Parameter  C(i) weighting factor C for generation cost function

                 /0001            0.01
                 0002             0.005
                 0003             0.03
                 0006             0.06
                 0008             0.09 /;
                 
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
execute_unload 'results.gdx',p=result;  // write variable x to the gdx file