/*
CPD 2022/2023
T08 - g14
up201709375 - Luis Filipe Pinto Cunha
up202211853 - Giulio Guarino
up202006950 - Vicente Salvador Martinez Lora
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixProduct {

    public static long onMult(int m_ar, int m_br) {

        System.out.printf("\nMatrix size: %d\n", m_ar);

        double[] pha = new double[m_ar*m_ar];
        double[] phb = new double[m_ar*m_ar];
        double[] phc = new double[m_ar*m_ar];

        for(int i=0; i<m_ar; i++) {
            for(int j=0; j<m_ar; j++) {
                pha[i*m_ar + j] = 1.0;
            }
        }

	    for(int i=0; i<m_br; i++) {
            for(int j=0; j<m_br; j++) {
                phb[i*m_br + j] = i+1;
            }
        }

        long time1 = System.currentTimeMillis();

        for(int i=0; i<m_ar; i++) {	
            for(int j=0; j<m_br; j++) {
		        double temp = 0;
			    for(int k=0; k<m_ar; k++) {	
				    temp += pha[i*m_ar+k] * phb[k*m_br+j];
			    }
			phc[i*m_ar+j]=temp;
		    }
        }

        long time2 = System.currentTimeMillis();
        long timeresult = time2 - time1;

        System.out.printf("Time: %3.3f seconds\n", (double)timeresult / 1000);

        System.out.println("Result matrix: ");
        for(int i=0; i<1; i++)
        {	for(int j=0; j<Math.min(10,m_br); j++)
                System.out.print(phc[j]+ " ");
        }

        System.out.println();

        return timeresult;

    }

    public static long onMultLine(int m_ar, int m_br) {

        System.out.printf("\nMatrix size: %d\n", m_ar);

        double[] pha = new double[m_ar*m_ar];
        double[] phb = new double[m_ar*m_ar];
        double[] phc = new double[m_ar*m_ar];

        for(int i=0; i<m_ar; i++) {
            for(int j=0; j<m_ar; j++) {
                pha[i*m_ar + j] = 1.0;
            }
        }

	    for(int i=0; i<m_br; i++) {
            for(int j=0; j<m_br; j++) {
                phb[i*m_br + j] = i+1;
            }
        }

        long time1 = System.currentTimeMillis();

        for(int i=0; i<m_ar; i++) {	
            for(int j=0; j<m_br; j++) {
                for(int k=0; k<m_ar; k++) {	
				    phc[i*m_ar+k] += pha[i*m_ar+j] * phb[j*m_br+k];
			    }
		    }
	    }

        long time2 = System.currentTimeMillis();
        long timeresult = time2 - time1;

        System.out.printf("Time: %3.3f seconds\n", (double)timeresult / 1000);

        System.out.println("Result matrix: ");
        for(int i=0; i<1; i++)
        {	for(int j=0; j<Math.min(10,m_br); j++)
                System.out.print(phc[j]+ " ");
        }

        System.out.println();

        return timeresult;


    }

    public static void main(String args[]) {

        try {

            File onMultFile = new File("onMultJava.txt");
            FileWriter onMultWriter = new FileWriter("onMultJava.txt");

            onMultWriter.write("matrix size,time (ms)\n");

            for(int i=600; i<3001; i += 400) {
                long time = onMult(i,i);
                String str = i+","+time+"\n";
                onMultWriter.write(str);
            }

            onMultWriter.close();

            System.out.println("\nSuccessfully created the OnMult file.");

        }   catch (IOException e) {
            System.out.println("\nOnMult File creation failed.");
            e.printStackTrace();
        }

        try {

            File onMultLineFile = new File("onMultLineJava.txt");
            FileWriter onMultLineWriter = new FileWriter("onMultLineJava.txt");

            onMultLineWriter.write("matrix size,time (ms)\n");

            for(int i=600; i<3001; i += 400) {
                long time = onMultLine(i,i);
                String str = i+","+time+"\n";
                onMultLineWriter.write(str);
            }

            onMultLineWriter.close();

            System.out.println("\nSuccessfully created the OnMultLine file.");

        }   catch (IOException e) {
            System.out.println("\nOnMultLine File creation failed.");
            e.printStackTrace();
        }
    }
}
