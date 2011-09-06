   Parameter numofbus number of buses in the network;
             numofbus=3;
   Sets
       k bus  index    / bus1, bus2, bus3/

       i generator index /gen1, gen2, gen3/;

   Alias(k,m);

   Parameters

      pl(k) real power load drawn at LSE k
            /bus1  1.3266
             bus2  0.4422
             bus3  0.4422/

      aCoeff(i) linear cost coefficient at generator i
             /gen1 10.694
              gen2 18.100
              gen3 37.8896/

      bCoeff(i) quantratic  const coefficient at generator i
            /gen1 0.00463
             gen2 0.00612
             gen3 0.01433/

      pgmax(i) max real power output at generator i(in pu unit)
              /gen1 2
               gen2 1.5
               gen3 0.2/

      pgmin(i) min real power output at generator i(in pu unit)
              /gen1 0.2
               gen2 0.1
               gen3 0.05/;

   Table b(k,m)  equivalent to the [B] matrix of the network
             bus1     bus2       bus3
      bus1   7.5       5          2.5
      bus2    5        9          4
      bus3   2.5       4         6.5;

   Table branchrating(k,m)  branch rating limit in pu unit
              bus1     bus2       bus3
      bus1     0       0.55      0.55
      bus2    0.55      0        0.55
      bus3    0.55     0.55       0;
   Table busGeneratorAry(k,i) bus and generator relationship table
             gen1     gen2    gen3
      bus1     1       0        0
      bus2     0       1        0
      bus3     0       0        1;

   Variables angle(k)  the bus voltage angle
             pg(i)     real power generation of generator i
             z         the total variable generation cost;

   equations
          tvc                 total variable cost
          nodepower(k)        node power balance
          pguplim(i)          upper limit for generator i
          pglowlim(i)         lower limit for generator i
          fkm_uplim(k,m)      upper limit for branch  k->m
          fkm_lowlim(k,m)     lower limit for branch k->m
          refangle            reference bus angle set to ZERO;

            tvc     ..   z =e= sum(i,aCoeff(i)*pg(i)+bCoeff(i)*pg(i)*pg(i));
       nodepower(k) ..   pl(k)-sum(i,busGeneratorAry(k,i)*pg(i))+sum(m,b(k,m)*(angle(k)-angle(m))) =e= 0;
   fkm_uplim(k,m)   ..   b(k,m)*(angle(k)-angle(m))  =g= -branchrating(k,m);
   fkm_lowlim(k,m)  ..   b(k,m)*(angle(k)-angle(m)) =l=  branchrating(k,m);
   pguplim(i)       ..   pg(i) =l= pgmax(i);
   pglowlim(i)      ..   pg(i) =g= pgmin(i);
   refangle         ..   angle('bus1') =e= 0;

   Model ieee3busdcopf /all/;

   Solve  ieee3busdcopf using nlp minimizing z;

   Display z.l, pg.l, angle.l;

// These lines execute during the execution phase
execute_unload 'results_dcopf.gdx', pg=result;  














