package edu.scut;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.math.complex.Complex;
import org.interpss.display.AclfOutFunc;
import org.interpss.mapper.IEEEODMMapper;


import Jama.Matrix;

import com.interpss.common.datatype.Matrix_xy;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.msg.IPSSMsgHubImpl;
import com.interpss.common.msg.StdoutMsgListener;
import com.interpss.common.msg.TextMessage;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.JacobianMatrixType;

import com.interpss.core.aclf.AclfNetwork;

import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.core.sparse.SparseEqnMatrix2x2;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
import org.ieee.pes.odm.pss.adapter.ieeecdf.IeeeCDFAdapter;
import org.ieee.pes.odm.pss.model.IEEEODMPSSModelParser;

public class ClosestLimit {
  
   public static void loadflow(AclfNetwork net,IPSSMsgHub msg){
 
		// create the default loadflow algorithm
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		// use the loadflow algorithm to perform loadflow calculation
		algo.loadflow(msg);
	   // System.out.println(AclfOutFunc.loadFlowSummary(net));
   }
   
	 
	public static void main(String[] args) {
		AclfNetwork net =CoreObjectFactory.createAclfNetwork();
		
		IPSSMsgHub msg= new IPSSMsgHubImpl();
		msg.addMsgListener(new StdoutMsgListener(TextMessage.TYPE_WARN));
		IpssLogger.getLogger().setLevel(Level.WARNING);
		
		AclfNetwork objnet = ConvertIEEEtoInterPSS(net ,"ieee30.ieee",msg);  //ieee30.ieee
		// (List) index for the power-increase load buses 
		
		List<Integer> busL =Arrays.asList(20,29); // need perfect for NO 29,30 bus  
		List<Integer> buslist =new ArrayList<Integer>(busL);
		
		Iterator<Integer> it = buslist.iterator();
         while(it.hasNext()) {
			 int idx=it.next();
			 AclfBus objbus =(AclfBus) objnet.getBusList().get(idx);
			 if(!objbus.isLoad()){
			 print("the bus ,index of buslist _"+idx+" _is not a loadbus");
             it.remove();
			 }
		 }
		int Numofbus =buslist.size();
		// specify the load increase direction 
		
		
		//double[] Pele={}
		//double[] Qele={}
		//Matrix dirp =new Matrix(Pele,Pele.length());
		//Matrix dirq =new Matrix(Qele,Qele.length());
		Matrix dirp = ones(Numofbus,1).times(3); // Dimensions should be matched with buslist
		Matrix dirq = ones(Numofbus,1).times(1);
		// Normalization of the direction dirp and dirq
		Matrix dirpq =new Matrix(2*Numofbus,1);
		dirpq.setMatrix(0, Numofbus-1, 0, 0,dirp);
		dirpq.setMatrix(Numofbus, 2*Numofbus-1, 0, 0,dirq);
		dirpq.arrayTimesEquals(new Matrix(2*Numofbus,1,1/dirpq.normF()));
		dirp =dirpq.getMatrix(0, Numofbus-1, 0, 0);
		dirq =dirpq.getMatrix(Numofbus, 2*Numofbus-1, 0, 0);
		
		getClosestLimit(objnet,buslist,dirp,dirq);
	
        //loadflow(msg);
	}
	
	public static  AclfNetwork ConvertIEEEtoInterPSS(AclfNetwork net, String IEEEFile, IPSSMsgHub msg){
		
		// 1. Convert IEEE data format to intermediary XML file
		LogManager logMgr = LogManager.getLogManager();
		Logger logger = Logger.getLogger("IEEE ODM Logger");
		logger.setLevel(Level.INFO);
		logMgr.addLogger(logger);
		try{
			IeeeCDFAdapter adapter = null;
			adapter = new IeeeCDFAdapter(logger);
			if (!adapter.parseXmlFile(IEEEFile)) {
				logger.severe("Error: model parsing error, " + adapter.errMessage());
				System.err.println("Error: model parsing error, " + adapter.errMessage());
			}
			// convert the model to a XML document string
			String xmlStr = adapter.getModel().toXmlDoc(true);
			// modify the XML output to validate the result
			String str1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><pss:PSSStudyCase xmlns:pss=\"http://www.ieee.org/cmte/psace/oss/odm/pss/Schema/v1\" " +
			 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
			String str2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><pss:PSSStudyCase xmlns:pss=\"http://www.ieee.org/cmte/psace/oss/odm/pss/Schema/v1\" " +
			 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
			 "xsi:schemaLocation=\"http://www.ieee.org/cmte/psace/oss/odm/pss/Schema/v1 ./schema/ODM_StudyCase.xsd\" ";
			xmlStr = xmlStr.replace(str1, str2);
			// output the XML document to the output file
			FileOutputStream outfile = null;
			outfile = new FileOutputStream("intermediary.xml");
			OutputStream out = new BufferedOutputStream(outfile);
			out.write(xmlStr.getBytes());
			out.flush();			
			out.close();
		}
		catch(Exception e){
			System.err.println(e.toString());
			e.printStackTrace();
		}
		// 2. Convert intermediary XML file to InterPSS model
		try{
			IEEEODMPSSModelParser parser = new IEEEODMPSSModelParser(new File("intermediary.xml"));
			IEEEODMMapper mapper = new IEEEODMMapper();
			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
			mapper.mapping(parser, simuCtx, SimuContext.class);
			net = simuCtx.getAclfNet();
		}
		catch(Exception e){
			System.err.println(e.toString());
			e.printStackTrace();
		}
		return net;
		}
	
	public  static void getClosestLimit(AclfNetwork net,List<Integer> buslist,Matrix dir_p,Matrix dir_q ){  
	    /*
	     * 1.call the load_increase_model by  the "buslist" type
	     * 
	     * 2.remember: the dir_p and dir_q here are then passed to dir_g and dir_b respectively
	     * as the p+jq=v^2*(g+jb), the ratio of p to q is the same as that of g to b
	     * and this ratio can be put as a way to express  direction 
	     * 
	     * 3. now this method is only suitable for direction without constP or constQ 
	     * further update will be made
	     */
			
		// be care:  bus buslist =( bus num -1��;
			 IPSSMsgHub msg = new IPSSMsgHubImpl();
	
		// get the total bus number	 
			  int n = buslist.size();
			  print(" n =" + n +"\n");
			  
	    //  initialize  the necessary bus INFO  (bus v ,p q )
			 
			 Matrix  busV= new Matrix(n,1),
			         busP= new Matrix(n,1),
			         busQ= new Matrix(n,1),
			        busP0= new Matrix(n,1),
			        busQ0= new Matrix(n,1),
	               deltaP= new Matrix(n,1),
		           GenP0 = new Matrix(n,1),
			         oldP= new Matrix(n,1),
			         oldQ= new Matrix(n,1),
			            g= new Matrix(n,1),
			            b= new Matrix(n,1),
			           g0= new Matrix(n,1),
			           b0= new Matrix(n,1),
			        dir_g= new Matrix(n,1),
			        dir_b= new Matrix(n,1),
			      shuntYg= new Matrix(n,1),
			      shuntYb= new Matrix(n,1);
			 
			 // pv curve slope by the near two point;
			 Matrix  oldV =new Matrix(n,1);
			 Matrix  busV0 =new Matrix(n,1);
			 //Matrix  deltaV =new Matrix(n,1);
			
			// Matrix  deltaL =new Matrix(n,1);
			 Matrix  slope =new Matrix(n,1);
			 Matrix  scant =new Matrix(n,1); // get the scant by the two near piont in PV 
			 
			double sumofdeltaP =0,
			       sumofGenP=0;      
			 // define the tolerance
			 double tol=0.001; //e-3
			 Matrix delta_dir =new Matrix(n,1); 
			 
			 //��ʼ������
			 double  lambda =0.1,
			         delta=0.05; //��ʼ�������仯��
		     // define tempt variants	 
			 double v=1,v1=1,p=1,q=1,G,B,newq,newp,tan; // tan for slop
			 
			 boolean interpolation =false; // ��ֵ
			 boolean flag =false; // flag for critical point, default is false 
		         // change it to true when getting the critical point 
			  
			 double findCLtimes =0;

			  /* 0. ��ʼ�������������� �� P+jQ ��ʽ��������ת����G+jB��ʽ
			   * �Գ���㶨�������أ�������һ�µ�
			   * �Ժ����ر��ǶԺ㶨�й����޹�
			   */
		        dir_g =dir_p.copy();
		        dir_b =dir_q.copy();
		        

//����������������������������1 ��ó�ʼ�ĸ��ɵ��ɡ���������������������������������������������������
		 for (int idx:buslist) {
               // print(" bus id "+idx+1);
			   AclfBus objbus=(AclfBus) net.getBusList().get(idx);
			   int id = buslist.indexOf(idx);
			 // 1.1.ѭ������ �ڵ��ѹ arraylist  busV ���ɸ��ɳ�ֵ ������g0,b0
			   if (objbus.isLoad()){
				   v =objbus.getVoltageMag();
				   p =objbus.getLoadP();
				   q =objbus.getLoadQ();
				  
				   busV.set(id,0,v);
				   busP.set(id,0,p);
				   busQ.set(id,0,q);
				   
				// ���ɹ���������Ϊ0
				   objbus.setLoadP(0);
				   objbus.setLoadQ(0);
			       
		   //1.2.get the equivalent admittance 
				  Complex shunt =objbus.getShuntY();  // �˴�������ԭ�еĽڵ�������Ĳ�������
				  shuntYg.set(id, 0, shunt.getReal()); 
				  shuntYb.set(id, 0, shunt.getImaginary()); 
			      G =p/(v*v);
			      B =-q/(v*v);
			      
			      g0.set(id, 0, G);
			      b0.set(id,0, B);
			  
			      //  equivalence taken effect, the  G+jB should be added to shuntY 
			      G+=shunt.getReal();
			      B+=shunt.getImaginary();
			      
			      objbus.setShuntY(new Complex(G,B));  // 
			      }	   
		  }    // end of for-loop 
		 //print("g0");
		 //printMatrix(g0);

		    // save the normal operation point 
		   busP0 =busP.copy();
		   busQ0 =busQ.copy();
		   busV0 =busV.copy();
		   // initialize the oldV;
		   oldV= busV.copy;
		   
		    // get the sum p and q of GENs
		   for (Bus bus:net.getBusList()){
		    	 AclfBus thisbus =(AclfBus) bus;
		    	 int id =0;
		    	 if(thisbus.isGenPV()||thisbus.isGenPQ()){
		    	    double genp =thisbus.getGenP();
		    	    GenP0.set(id,0,genp);
		    		sumofGenP+=genp;
                    id++;
		    	 }
		    }
  
// ������������������������get the closest limit ��������������������������������������������       
   do{	  
		  findCLtimes++;
		  print( "������������findCLtimes"+findCLtimes);

//���������������������������� 2 Calculate the critical condition of a certain direction
			 //ע��Ŀǰ�ݲ����Ǵ��ڽڵ�Q��P Ϊ �㶨 ��� 
	  int count =0;
      loop1:// ��һ��ѭ����־
        do{
			  // ����������־
			    count++;
		    	print("    count =  "+count);

		      //2.1 ��ѭ�����ӵ��ɣ��˴�Ϊ�㹦��ģ�ͣ�
		
		      // increase load power in the dir_g and dir_b
		       print("   lambda = "+lambda);
		       g =g0.plus (dir_g.times(lambda)); // increase by lambda 
		       b= b0.minus(dir_b.times(lambda));//ע��˴��ļ������㣬Ϊ����ģ�͵��ص�
		       
		      // get the sum of load power increment ��save them to sumofdeltaP and sumofdeltaQ respectively;
		      //�����ܵĸ��ɹ�������
		  /*     
			  Matrix V2 =oldV.arrayTimes(oldV);
			  deltaP= dir_g.times(delta).arrayTimes(V2);

		      sumofdeltaP =sumOfElement(deltaP);
		     

		       // dispatch the load increment to GENs that still have power margin;
		      for (Bus bus:net.getBusList()){
		    	  AclfBus thisbus =(AclfBus) bus;
		    	  if(thisbus.isGenPQ()||thisbus.isGenPV()){
		    		  double genp =thisbus.getGenP();
		    		  genp +=sumofdeltaP*genp/sumofGenP;
		    		  // if there is a Plimit ,here we should compare genP with Plimit first to decide the next step;
		    		  thisbus.setGenP(genp);
		    		 
		    	  }
		    	  
		      }
		    */  
		       
		      /* print("g");
			   printMatrix(g);
			   print("b");
			   printMatrix(b);
			   */
			   
			    for (int id1: buslist){
			    	AclfBus object_bus=(AclfBus) net.getBusList().get(id1);
			    	int id = buslist.indexOf(id1);
			    	G = g.get(id,0)+shuntYg.get(id, 0);
				    B = b.get(id,0)+shuntYb.get(id, 0);  
				    object_bus.setShuntY(new Complex(G,B));
			        }  // end of for-loop
			   
			   //2.2 ���㳱��
			    loadflow(net,msg);

			   //2.3.   �õ��������Ӻ�ĳ����Ľ��
			    for (int idx:buslist) {
					   AclfBus objbus=(AclfBus) net.getBusList().get(idx);
					   int id = buslist.indexOf(idx);
					   G =g.get(id,0);
					   B =b.get(id,0);
					   v1 =objbus.getVoltageMag();
					   print(" id is   "+id+  "  new v"+v1+"  new G"+G);
					   v= oldV.get(id, 0);
					   newp=G*v1*v1; // get the LF result p and q of this step 
					   newq=-B*v1*v1;
						
					   p=oldP.get(id, 0);//p of the last step
					   q=oldQ.get(id, 0);//q of the last step
					   tan =(v1-v)/(newp-p);  // get the slope, and save it in Matrix slope
					   
					   slope.set(id, 0, tan);
						// print("      tan = "+tan);
					   scant.set(id, 0, Math.abs(1/tan));  // ��б�ʵ���Ϊscant Ԫ��
					   
			  // compare the last two step to see whether we get the critical point or not	   
					   if(newp<p||newq<q){
						    flag =true; // ���ֹ��ʼ�С��˵���Ѿ��ﵽ��Խ��PV nose �����ǻص�ǰһ��������С�������൱�ڲ�ֵ����
						   
						    if (p-newp>0.001) {  //ȷ�����޵�ľ���  ||q-newp>0.001

						         print(" bus   "+objbus.getId()+  "  load power less than before:"); 
						         print("new p+jq:    "+newp+"  +j*"+newq);
						         print("old p+jq:    " +p  +"  +j*"+q);
							     print("have to minize the step length ");
							     break; //get out of this for-loop 
						     }// end the inner if
						    else{
							   
						         print("bus :"+objbus.getName()+" get the critical point first");
							     print("this step new v="+v1);
							     print("this step new p="+ newp);
							     print("this step new q="+ newq);
							     print("last step old v="+ v);
							     print("the critical p ="+ p);
							     print("the critical q ="+ q);
							
							    // print("result:");
                                // printMatrix(oldP);
                                // printMatrix(oldQ);
                                // printMatrix(oldV);
							    break loop1; // get the critical point and exit loop1 
						    }
				       } //end the outer if
					   
					   busP.set(id, 0, newp);
					   busQ.set(id, 0, newq);
					   busV.set(id, 0,  v1);
						
					   print("bus  "+objbus.getId()+  "  load: p+jq    "
							   +newp+"  +j*"+newq+"\n");
					
		        } // end of this for-loop (get the loadflow result)
			    
			    if(flag ==false){ // δ�ﵽ���޵㣬�����µĹ��ʺ͵�ѹ
			         oldP=busP.copy();
			         oldQ=busQ.copy();
			         oldV=busV.copy();
			     }
			    
	//������������������������2.3  ������һ���Ĳ����Ľ��� ��������������������������������
				  
			   // call the min(Matrix a) to the get the largest number ;
			   print( "and flag is :"+ flag);

			   if(flag ==false&&interpolation==false ){  // δ�ﵽpv nose, ��������ʽȷ������������
			        delta = min(scant); // scant Ϊ �����ɽڵ� V��P�� �仯���ĵ�����Сֵ����PV����б�ʵĵ�����
			        print("delta=" +delta);
			            if (delta> 1) delta =0.3;
			            else if (delta>0.5) delta =0.1;
			            else delta =0.05;
			   }

			   if (flag==true){   // flag =true  get the PV near-nose 
			    	 
			    	 lambda=lambda-delta;   // the first time get pv nose ,then  back to where higher than the oldP  now in PV curve
			    	 delta =delta/2;// smallest step length;
			    	 if (delta<0.001) delta=0.001;
			    	 interpolation=true;
			    	
		       }
			  // update the 
			   lambda +=delta;
			       
			   // reset the flag to defult, to search the critical piont in next step
	           flag=false;
	           print("^����������������������������������������������������");
		
	   }while (count<20);  // for the while-loop;

		//����Pcr�� P0�ľ���
		double Length = oldP.minus(busP0).normF();
		print("the Length from P0 to Pcr: "+Length);
		
   // 2.5 �Ѹ��ɵ�Ч��P+jQ��ʽ��ʹ���������YMatrix�븺�ɵ�Чǰһ��,���鵱ǰ���޵�Jacobi �����
		
		for(int idx:buslist){
			AclfBus objbus =(AclfBus) net.getBusList().get(idx);
			int id =buslist.indexOf(idx);
			Complex y0=new Complex(shuntYg.get(id, 0),shuntYb.get(id, 0));
			objbus.setShuntY(y0);
			objbus.setLoadP(oldP.get(id, 0));
			objbus.setLoadQ(oldQ.get(id, 0));
			
		}
		
//������������������������3.  get the next step power increase direction��������������������������������
	    SparseEqnMatrix2x2 S = 
			        net.formJMatrix(JacobianMatrixType.FULL_POLAR_COORDINATE, msg);
	    Matrix jacobi =Sparse2Matrix(net,S);
	     
      //printMatrix(jacobi);
      // jacobi.print(jacobi.getColumnDimension(), 3);
      //jacobi.print(java.text.NumberFormat.INTEGER_FIELD, 4);
    
        Matrix norm =getLvector(jacobi);
        print("Lvector:");  
        norm.print(norm.getColumnDimension(), 3);
		Matrix unitNorm =norm.times(1/norm.normF());
	  //unitNorm.print(unitNorm.getColumnDimension(), 3);
		
		List<Matrix> NewDir =getNewDir(net,unitNorm,buslist); 
		
		dir_p =NewDir.get(0);
		dir_q =NewDir.get(1);
		
		if (dir_p.get(0, 0)<0){           // ȷ��һֱ��������״̬
			dir_p =dir_p.uminus();
		}
		
		if(dir_q.get(0, 0)<0){
			dir_q =dir_q.uminus();
		}
		
		//print("the new dir_p");
		//printMatrix(dir_p);
		
		//print("the new dir_q");
		//printMatrix(dir_q);
		
		// ���´�busP0 ��busQ0 ״̬��ʼ���������������Ӧ�ö�Ӧ�ĳ�ֵ״̬
		for(int idx:buslist){
			AclfBus objbus =(AclfBus) net.getBusList().get(idx);
			int id =buslist.indexOf(idx);
			objbus.setLoadP(busP0.get(id, 0));
			objbus.setLoadQ(busQ0.get(id, 0));
			
		}
		
		for(Bus bus:net.getBusList()){
			    AclfBus thisbus =(AclfBus) bus;
			    int id =0;
			    if(thisbus.isGenPV()||thisbus.isGenPQ()){
			    	 thisbus.setGenP(GenP0.get(id,0));
	                 id++;
			    }
		}
		
		// �������������� ��������ε���������ӽ��̶�����Ҫ��
		//�Է�����ģ������
		
		 // pass the new dir

		print("------new dir_g");
		printMatrix(dir_p);
		print("this step dir_g");
		printMatrix(dir_g);
		
		print("------new dir_b");
		printMatrix(dir_q);
		print("------old dir_b");
		printMatrix(dir_b);
		delta_dir =dir_p.minus(dir_g);
		print("norm of delta_p"+delta_dir.normF());
		
		dir_g =dir_p.copy();
		dir_b =dir_q.copy();

	   if(delta_dir.normF()>tol){
	
			oldP =busP0;
			oldQ =busQ0;
			oldV =busV0;
				
			 // ���³�ʼ����������
			 lambda =0.1;
			 delta=0.05;
			 //���¶����־��
			 flag =false;
			 interpolation =false;
			print("change back to normal operation condition");
	   }
	   else break; // get out of the outter loop 
		 
  }while(findCLtimes<10);//findCLtimes<10
	 
	 print("get the closest limit");
	 
		
    }
	
	
	
	
	/*
	 * ������й����䣺(������ƽ�⹦������)
	 * 1. get the gen margin of every generator PGi,and calculate the total PG
	 * 
	 * 2. under the condition now ,get the increment of total net active power PLk , if PLk>PG, 
	 * set every generator to Gmaxi and turn next , else turn to next step directly
	 * 
	  
	 * 3. get the sum GEN and Load of every area ,
	 * PGarea[a] = sum of PGi (i belongs to a) ,
	 * PLarea[a] = sum of PLi (i belongs to a) 
	 * COMPARISION : Carea[a] =PGarea[a]-PLarea[a]
	 * 
	 * 4. if Carea[a]>0, dispatch the load increment PLk in proportion to their GEN margin among 
	 * the GEN in this area , 
	 *  else , make the GENs in araa a to meet their GENmax ,and calculate the power shortage again
	 *  Pdefi=sum of Carea[a](a is only Carea[a]<0) , supply Pdefi by the near area GENs
	 *  
	 *  Pgi =Pgi0+ PLarea * PGi/PG
	 * 
	
	*/
	
	
	private static Matrix Sparse2Matrix (AclfNetwork net,SparseEqnMatrix2x2 S){

		 // get sortIndex
		 int[] sortNumberToMatrixIndex = new int[net.getNoBus()+1];
		 int[] sortPQNumberToMatrixIndex = new int[net.getNoBus()+1];
		 // get the number of non-swing buses and PQ buses
        int n = 0;  //non-swing
        int m = 0;  //PQ
        int mPQ=0,nPQ=0;
     for (Bus bus : net.getBusList()) {
         AclfBus aclfBus = (AclfBus)bus;
         if (!aclfBus.isSwing()) {
             sortNumberToMatrixIndex[bus.getSortNumber()] = n++;  // your matrix index range [0 ... n-1)
             if (!aclfBus.isGenPV()) {
                 sortPQNumberToMatrixIndex[bus.getSortNumber()] = m++;  // PQ submatrix index range [0 ... m-1)
             
             }
         }
         
     }
    // print("n=" +n+"  m="+m +"\n");
   
     
     //initialize of the conventional NR jacobi matrix
     
     Matrix H = new Matrix(n,n);
     Matrix N = new Matrix(n,m);
     Matrix K = new Matrix(m,n);
     Matrix L=  new Matrix(m,m);
     Matrix Jnr =new Matrix (n+m,n+m);
     
     
     //int index=0;  index for Matrix rows 
       
   for (Bus busi : net.getBusList()) {
        AclfBus aclfBusi = (AclfBus)busi;
        if (!aclfBusi.isSwing()) {
       	 
              for (Bus busj : net.getBusList()) {
                  AclfBus aclfBusj = (AclfBus)busj;
                  
                  if (!aclfBusj.isSwing()) {
         
                      int i = busi.getSortNumber();
                      int j = busj.getSortNumber();
                      Matrix_xy elem = S.getElement(i, j);
               
                        
	               // the following variant is chosen like PQ bus, but it is also suitable for PV bus
	                  double dPdVang = elem.xx;
	                  double dPdVmag = elem.xy;
	                  double dQdVang = elem.yx;
	                  double dQdVmag = elem.yy;
	                   
	                    int m1 = sortNumberToMatrixIndex[i];
                        int n1 = sortNumberToMatrixIndex[j];
                   
                         if (!aclfBusi.isGenPV()) { 
                          mPQ = sortPQNumberToMatrixIndex[i]; // JUST FOR PQ sort 
                           }
                         if (!aclfBusj.isGenPV()) {
                          nPQ = sortPQNumberToMatrixIndex[j]; // JUST FOR PQ sort
                           }
                
                          H.set(m1, n1, dPdVang);// n-1*n-1 ,suitable of PQ & PV bus
                   
                         if(!aclfBusj.isGenPV()){
                            N.set(m1,nPQ,dPdVmag); //n-1*m
                            } //end of this -if
                   
                          // matrix element corresponding to PQ bus 
                          if(!aclfBusi.isGenPV() ){
            	             	 K.set(mPQ,n1,dQdVang);  // m*n-1
	                             if(!aclfBusj.isGenPV()){
	                                L.set(mPQ,nPQ,dQdVmag);//m*m
	                               } 
	                        }
                    }// end of if-busj
                  
	             }//end of for busj
             
	        }//end  if-busi
    
		 }  //end of for busi
 
	   Jnr.setMatrix(0,n-1,0,n-1,H);
	   Jnr.setMatrix(0,n-1,n,n+m-1,N);
	   Jnr.setMatrix(n,n+m-1,0,n-1,K);
	   Jnr.setMatrix(n,n+m-1,n,n+m-1,L);
	   return Jnr;
	 
	 }//end this method
	
	
	
	 private static Matrix getLvector(Matrix jacobi){
		 
	      Matrix Vector=jacobi.eig().getV();
	      Matrix Diag  =jacobi.eig().getD();
	      // search the zero eigen value and its index 
	      double eig_val =Math.abs( Diag.get(0, 0));
	      int col =0;
	      
	      for (int i=1;i<Diag.getColumnDimension();i++){
	    	  if (eig_val > Math.abs(Diag.get(i, i)) ){
	    	   eig_val =Math.abs(Diag.get(i, i));
	    	   col = i;
	    	   } //end of if
	        } //end of for
	      print("eig value min"+eig_val);
	      // then find out the corresponding eigen vector leftVector
	      
	      int[]objcol={col}; // set the vector corresponding to zero eigen vector
	      Matrix leftVector =Vector.getMatrix(
	    		  0, Vector.getRowDimension()-1, objcol).uminus();
	      return leftVector;
	      
	 }
	
		 
		 private static List<Matrix> getNewDir(AclfNetwork net,Matrix lVector, List<Integer>buslist){
				
				/*
				 * get the new dirP and dirQ for next step
				 */
				int busNum=buslist.size();
				
				// initialize the dirP and dirQ
				Matrix dirP=new Matrix(busNum,1);
				Matrix dirQ=new Matrix(busNum,1);
				Matrix dirpq= new Matrix(2*busNum,1);
				
				// get sortIndex
				int[] sortIndex = new int[net.getNoBus()+1];
				int[] sortPQIndex = new int[net.getNoBus()+1];
				 // get the number of non-swing buses and PQ buses
		        int n = 0;  //non-swing
		        int m = 0;  //PQ
		        int mPQ=0,nPQ=0;
		     for (Bus bus : net.getBusList()) {
		         AclfBus aclfBus = (AclfBus)bus;
		             sortIndex[aclfBus.getSortNumber()] = n++;  // your matrix index range [0 ... n)
		             if (!aclfBus.isGenPV()&&!aclfBus.isSwing()) {
		                 sortPQIndex[aclfBus.getSortNumber()] = m++;  // your matrix index range [0 ... m-1)
		             }
		         
		     }	//end of for
				
		    
		     for(int idx:buslist){
		    	 int id=buslist.indexOf(idx);
		    	 AclfBus thisbus = (AclfBus) net.getBusList().get(idx);
		    	 int np =sortIndex[thisbus.getSortNumber()];
		    	 int nq =sortPQIndex[thisbus.getSortNumber()]+n-1; // n here is the total bus number
		    	 
		    	 double p_dir =lVector.get(np, 0);
		    	 double q_dir =lVector.get(nq, 0);
		    	 
		    	 dirP.set(id, 0, p_dir);
		    	 dirQ.set(id, 0, q_dir);
		    	 
		      }// end of for
		     
		     dirpq.setMatrix(0, busNum-1, 0, 0,dirP);
		     dirpq.setMatrix(busNum, 2*busNum-1, 0, 0, dirQ);
		     
		     dirpq =dirpq.times(1/dirpq.normF());
		     
		     dirP =dirpq.getMatrix(0, busNum-1, 0, 0);
		     dirQ =dirpq.getMatrix(busNum, 2*busNum-1, 0, 0);
		     
		     List<Matrix> newdir = Arrays.asList(dirP,dirQ);
			 return newdir;	
				
		} //end this getNewDir method 
		 


	   static void print(String s){
		   System.out.println(s);
	   }
	   
	    static double max(Matrix a){
		   // 
		   int i =0;
		   int j =0;
		   double max =0;
		   if  ( i< a.getRowDimension()){
			   if (j< a.getColumnDimension()){
				   if (a.get(i, j)>max) max=a.get(i, j);
				   
				   j+=1;
				   
			   }
			   i+=1;
		   }
		   return max;
	   } //end of max method
	   
	   
	   static double min(Matrix a){
		   // 
		   int i =0;
		   int j =0;
		   double min =99999;
		   if  ( i< a.getRowDimension()){
			   if (j< a.getColumnDimension()){
				   if (a.get(i, j)<min) min =a.get(i, j);
				   
				   j+=1;
				   
			   }
			   i+=1;
		   }
		   return min;
	   }  
	  
	 private static double sumOfElement(Matrix m){
		 double sum=0;
		    for (int i= 0;i<m.getRowDimension();i++){
				 
				 for (int j= 0;j<m.getColumnDimension();j++){
					 sum+=m.get(i, j);
				   }
		     }
		 return sum;
	 }
	   
	 private  static Matrix ones(int n,int m ){
	    	  Matrix a =new Matrix (n,m);
			     for (int i= 0;i<n;i++){
					 
					 for (int j= 0;j<m;j++){
						 a.set(i, j, 1);
					   }
			     }
			   return a;
		     }  // end of this method
	    
	   static void printMatrix(Matrix m){
		   
			 for (int i= 0;i<m.getRowDimension();i++){
				 
				 for (int j= 0;j<m.getColumnDimension();j++){
					 System.out.print(m.get(i, j)+"  ");
				 } 
				 System.out.print("\n");
			 }  // end of for i
		}  // end of printMatrix
	 } // end of newgb
	    
	  
	   
	


