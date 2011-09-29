$Title dc opf model using gdx files
$EOLCOM //

   Sets
       k bus  index
       i generator index

   Alias(k,m);
   
   Parameters
      pl(k) real power load drawn at LSE k

      aCoeff(i) linear cost coefficient at generator i
      bCoeff(i) quantratic  const coefficient at generator i

      pgmax(i) max real power output at generator i(in pu unit)
      pgmin(i) min real power output at generator i(in pu unit);

   Parameters
      b(k,m)  equivalent to the B matrix of the network
      branchrating(k,m)  branch rating limit in pu unit
      busGeneratorAry(k,i) bus and generator relationship;

$GDXIN dcopf_input.gdx
$LOAD  k=BusIndex,i=GenIndex
$LOAD  pl=RealPower, pgmax=Pgmax, pgmin=Pgmin
$LOAD  aCoeff=ACoeff, bCoeff=BCoeff
$LOAD  b=BMatrix, branchrating=Rating, busGeneratorAry=BusGenRelation
$GDXIN

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
   refangle         ..   angle("Bus1") =e= 0;

   Model ieee3busdcopf /all/;

   Solve  ieee3busdcopf using nlp minimizing z;

   Display z.l, pg.l, angle.l;
   
// These lines execute during the execution phase
execute_unload 'results.gdx',pg=result;  // write variable x to the gdx file   
















