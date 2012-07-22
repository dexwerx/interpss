
import java.io.*;

public class OutputResult{
	
	public static void readAndWrite(File read, BufferedWriter bw){
		try {
			BufferedReader br = new BufferedReader(new FileReader(read));
			String temp = null;
			temp = br.readLine();
			while(temp != null){
			//写文件
			bw.write(temp + "\r\n"); //只适用Windows系统
			//继续读文件
			temp = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println(e);
		}catch (IOException e){
			System.out.println(e);
		}
		
	}	
	
	public static void IntegerResult(String outputFile){
		try {
			File readBus = new File(outputFile+"bus.txt");
			File readLoad = new File(outputFile+"load.txt");
			File readGen = new File(outputFile+"gen.txt");
			File readSwitShunt = new File(outputFile+"switchShunt.txt");
			File readBranch = new File(outputFile+"branch.txt");
			File readTrans = new File(outputFile+"transformer.txt");
			File readAreaInterchange = new File(outputFile+"areaInterchange.txt");
			File readInterareaTransfer = new File(outputFile+"interareaTransfer.txt");			
			File readZx= new File(outputFile+"zx.txt");
			File writeRaw = new File(outputFile+"raw.txt");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeRaw));
			readAndWrite(readBus, bw);
			readAndWrite(readLoad, bw);
			bw.write("0 /END OF LOAD DATA, BEGIN GENERATION DATA"+ "\r\n");
			Helper.readModifyAndWrite(readGen, bw, readZx);			
			readAndWrite(readBranch, bw);
			readAndWrite(readTrans, bw);
			bw.write("0 /END OF TRANSFORMER DATA, BEGIN AREA DATA"+ "\r\n");
			readAndWrite(readAreaInterchange,bw);
			bw.write("0 /END OF AREA DATA, BEGIN TWO-TERNMINAL DC DATA"+ "\r\n");
			bw.write("0 /END OF TWO-TERNMINAL DC DATA, BEGIN VSC DC LINE DATA"+ "\r\n");
			bw.write("0 /END OF VSC DC LINE DATA DATA, BEGIN SWITCHED SHUNT DATA"+ "\r\n");
			readAndWrite(readSwitShunt,bw);
			bw.write("0 /END OF SWITCHED SHUNT DATA, BEGIN IMPEDANCE CORRECTION DATA"+ "\r\n");
			bw.write("0 /END OF IMPEDANCE CORRECTION DATA, BEGIN MULTI-TERNMINAL DC DATA"+ "\r\n");
			bw.write("0 /END OF MULTI-TERNMINAL DC DATA, BEGIN MULTI-SECTION LINE DATA"+ "\r\n");
			bw.write("0 /END OF MULTI-SECTION LINE  DATA, BEGIN ZONE DATA"+ "\r\n");
			readAndWrite(readInterareaTransfer,bw);
			bw.write("0 /END OF ZONE DATA, BEGIN INTER-AREA TRANSFER DATA"+ "\r\n");
			bw.write("0 /END OF INTER-AREA TRANSFER DATA, BEGIN OWNER DATA"+ "\r\n");
			bw.write("0 /END OF OWNER DATA, BEGIN FACTS DEVICE DATA"+ "\r\n");
			bw.write("0 /END OF FACTS DEVICE DATA");
			bw.close();
			
			// read and write dyre
			File readGenerator = new File(outputFile+"generator.txt");
			File readExciter = new File(outputFile+"exciter.txt");
			File readPSS = new File(outputFile+"pss.txt");
			File readTurbineGovernor = new File(outputFile+"tg.txt");
			File writeDYRE= new File(outputFile+"DYRE.txt");
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(writeDYRE));
			readAndWrite(readGenerator, bw1);
			readAndWrite(readExciter, bw1);
			readAndWrite(readPSS, bw1);
			readAndWrite(readTurbineGovernor, bw1);
			bw1.close();
			
			// read and write SEQ
			File readPosGen = new File(outputFile+"posGen.txt");
			File readNegGen = new File(outputFile+"NegGen.txt");
			File readZerGen = new File(outputFile+"ZerGen.txt");
			File readNegLoad = new File(outputFile+"NegLoad.txt");
			File readZerLoad = new File(outputFile+"ZerLoad.txt");
			File readZerLine = new File(outputFile+"ZerLine.txt");
			File readZerXfr = new File(outputFile+"ZerXfr.txt");
			File writeSEQ = new File(outputFile+"SEQ.txt");
			BufferedWriter bw2= new BufferedWriter(new FileWriter(writeSEQ));
			bw2.write("0     /Initial input data"+ "\r\n");
			readAndWrite(readPosGen,bw2);
			bw2.write("0   "+ "\r\n");
			readAndWrite(readNegGen,bw2);
			bw2.write("0   "+ "\r\n");
			readAndWrite(readZerGen,bw2);
			bw2.write("0   "+ "\r\n");
			readAndWrite(readNegLoad,bw2);
			bw2.write("0   "+ "\r\n");
			readAndWrite(readZerLoad,bw2);
			bw2.write("0   "+ "\r\n");
			readAndWrite(readZerLine,bw2);
			bw2.write("0   "+ "\r\n");
			bw2.write("0   "+ "\r\n");// zero sequence mutual impedance
			readAndWrite(readZerXfr,bw2);
			bw2.write("0   "+ "\r\n");
			bw2.write("0   "+ "\r\n");// zero sequence switched shunt data
			bw2.close();
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
}