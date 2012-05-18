package org.interpss.ed;


public class EDAlgorithm {
	public static enum LossType {LossForm, NoLoss}
	public static enum ScheduleType {TotalGen, TotalLoad}
	
	public static double Alpha = 0.5;
	public static double TotalGenTolerance = 0.01;
	public static double IhrTolerance = 0.000001 ;
    
	private LossType losstype;
	private ScheduleType schedtype;
	
	private IBCoeffMatrix bCoef;

	public EDAlgorithm(LossType losstype, ScheduleType schedtype) {
		this.losstype = losstype;
		this.schedtype = schedtype;
	}
	
	public void LambdaSearchDispatch(int ngen, IEDGenUnit[] genAry, double schedmw)  throws Exception  {
		for ( int i = 0; i < ngen; i++ ) {                 // Set unit output to midrange}
			genAry[i].setP( ( genAry[i].getPmin() + genAry[i].getPmax()) / 2.0 );
		}

		int lossiter = 0;
		boolean endloop = false;

		do {   //                                    {Top of iterative loop with losses}
			double lambdamin = 10000.0;
			double lambdamax = 0.0;

			if (losstype == LossType.LossForm ) {  // Calc losses and pen factors}
				calPenaltyFactor(ngen, Alpha, genAry);
			}

			for ( int i = 0; i < ngen; i++ ) {  // Calculate max and min lambdas}
				IEDGenUnit gen = genAry[i];
				double lambda = gen.getIhrmax() * gen.getPenaltyFactor() * gen.getFuelCost();
		    	if (lambda > lambdamax) 
		    		lambdamax = lambda;
		    	lambda = gen.getIhrmin() * gen.getPenaltyFactor() * gen.getFuelCost();
		    	if (lambda < lambdamin) 
		    		lambdamin = lambda;
		    }

			double lambdastart = ( lambdamax + lambdamin ) / 2.0;
			double deltalambda = ( lambdamax - lambdamin ) / 2.0;

		    //Set up total generation target}
		    double targetgen = schedmw;
		    if (schedtype == ScheduleType.TotalLoad) { 
		    	targetgen += calLoss(ngen, genAry);
		    }

		    // Lambda search}
		    double lambda = lambdastart;

	    	double totalgen = 0.0;
		    int n = 0;
		    do {         //         {Top of lambda search loop}
		    	n++;
		    	totalgen = 0.0;
		    	for ( int i = 0; i < ngen; i++ ) {
					IEDGenUnit gen = genAry[i];
		    		double unitihr = lambda / ( gen.getPenaltyFactor() * gen.getFuelCost() );
		    		gen.setP(gen.inverserIhr(unitihr));
		         	totalgen = totalgen + gen.getP();
		    	}

		    	if ( Math.abs( totalgen - targetgen ) >= TotalGenTolerance) {
		    		if (totalgen > targetgen)  
		        		lambda = lambda - deltalambda;
		    		if (totalgen < targetgen) 
		        		lambda = lambda + deltalambda;
		    		deltalambda = deltalambda / 2.0;
		        }
		    } while ( Math.abs( totalgen - targetgen ) >= TotalGenTolerance && n <= 20 ) ;

		    // See if another loss iteration is needed}
		    if (losstype != LossType.LossForm) 
		      	endloop = true;
		    lossiter = lossiter + 1;
		    if (lossiter > 10) 
		      	endloop = true;

		} while (!endloop);
	}
	
	public void TableLookupDispatch(double lambda, int ngen,
			IEDGenUnit[] genAry, double schedmw) throws Exception {
		// check data and calcualate curveorder
		/*
		if curvetype <> pio then
	      begin
	      writeln;
	      writeln(' ERROR -- must have piecewize i/o curves to use table lookup ');
	      goto return
	      end;
	    */  
	    int curveorder = 0;  

		if (losstype == LossType.LossForm ) {  // Calc losses and pen factors}
			calPenaltyFactor(ngen, Alpha, genAry);
		}

	    double targetgen = schedmw;
	    if (schedtype == ScheduleType.TotalLoad) { 
	    	targetgen += calLoss(ngen, genAry);
	    }

	    int kseg = 0;                  //      {Build segment tables}
      	double[] ordvalue = new double[curveorder];
      	double[] seginccost = new double[curveorder];
      	double[] segmw = new double[curveorder];
      	int[] segunit = new int[curveorder];
      	int[] order = new int[curveorder];
	    for ( int i = 0; i < ngen; i++ ) {
			IEDGenUnit gen = genAry[i];
	    	for ( int j = 0; j < curveorder; j++ ){
	    		kseg++;
	    		double segihr = (gen.getIOCost(j) - gen.getIOCost(j-1)) /
	                        (gen.getIOMwPoint(j)-gen.getIOMwPoint(j-1));
	      		seginccost[ kseg ] = segihr * gen.getFuelCost() * gen.getPenaltyFactor();
	      		segunit[ kseg ] = i;
	      		segmw[ kseg ] = gen.getIOMwPoint(j) - gen.getIOMwPoint(j-1);
	    	  }
	      	}
	      	int numsegments = kseg;

	      	//{Set up for ordering routine}
	      	for ( int k = 0; k < numsegments; k++) 
	      		ordvalue[k] = seginccost[k];
	      	orderRoutine( numsegments, ordvalue, order );    // Call ordering routine}

	      	double ptotal = 0.0;
	      	for ( int i = 0; i < ngen; i++) {
				IEDGenUnit gen = genAry[i];
				gen.setP(gen.getPmin());
				ptotal = ptotal + gen.getP();
	      }

	      boolean done = false;
	      int k = 0;
	      do {
	         k++;
	         kseg = order[ k ];
	         int kunit = segunit[ kseg ];
	         if ( ( ptotal + segmw[ kseg ] ) < targetgen ) {
	            genAry[kunit].setP(genAry[kunit].getP() + segmw[ kseg ]);
	            ptotal = ptotal + segmw[ kseg ];
	         }
	         else {
	        	 genAry[kunit].setP(genAry[kunit].getP() + ( targetgen - ptotal ));
	            done = true;
	         }
	      } while (!done);

	      lambda = seginccost[ kseg ];
	}
	
	private double calLoss(int ngen, IEDGenUnit[] genAry) {
		double mwlosses = bCoef.getB00();
		for (int i = 0; i < ngen; i++) {
			IEDGenUnit geni = genAry[i];
			mwlosses = mwlosses + bCoef.getB0(i) *
                      ( geni.getP()/100.0) + bCoef.getB(i,i) * Math.sqrt( geni.getP()/100.0 );
			for ( int j = i+1; j < ngen; j++ ) {
				IEDGenUnit genj = genAry[j];
				mwlosses = mwlosses + 2.0 * ( geni.getP()/100.0) * ( genj.getP()/100.0 ) 
							* bCoef.getB(i,j);
			}
		}	
		return mwlosses;
	}

	private void calPenaltyFactor(int ngen, double alpha, IEDGenUnit[] genAry) {
		for ( int i = 0; i < ngen; i++) {
			IEDGenUnit geni = genAry[i];
			double penfac_old = geni.getPenaltyFactor();
	        double incloss = bCoef.getB0(i);
	        for ( int j = 0; j < ngen; j++) {
				IEDGenUnit genj = genAry[j];
	        	incloss = incloss + 2.0 * ( genj.getP()/100.0 ) * bCoef.getB(i,j);
	        }
	        double penfac_new = 1.0 / ( 1.0 - incloss );
	        geni.setPenaltyFactor( penfac_old + alpha * (penfac_new - penfac_old));
	    }
	}

	private static void orderRoutine(int numorder, double[] ordertable, int[] orderindex) {
/*
		{ subroutine to order a list, least first                             }
		{                                                                     }
		{ input numorder = the number of items to be ordered                  }
		{ input ordertable = the items to be ordered                          }
		{ output orderindex = pointer to order value table                    }
 */		
	    int top = 0, j = 0, last = 0;
	    int[] nxt = new int[numorder]; 
		for ( int i = 0; i < numorder; i++) {
	    	if (i <= 1) {
	        	top = 1;
	        	nxt[1] = 0;
	    	}
	    	else {
	    		j = top;
	        	last = 0;
	        	boolean stop = false;
	        	do {
	        		stop = true;
	          		if (ordertable[ i ] > ordertable[ j ]) {
	          			last = j;
	          			j = nxt[ j ];
	          			stop  = (j == 0);
	          			if (stop) {
	          				nxt[last] = i;
	          				nxt[i] = 0;
	          			}
	    			}
	          		else {
	          			if (j != top) {
	          				nxt[ last ] = i;   //          { j not = top }
	          				nxt[ i ] = j;
	          			}
	          			else {
	          				top = i;                  //   { j = top }
	          				nxt[ i ] = j;
	          			}
	          		}
	    		} while (!stop);
	    	}
		}	
	    
		int indx = 0;
	    j = top;
	    do {
	      orderindex[ indx ] = j;
	      j = nxt[ j ];
	      indx = indx + 1;
	    } while (j != 0);
	}
}
