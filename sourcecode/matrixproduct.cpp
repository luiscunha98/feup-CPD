#include <stdio.h>
#include <iostream>
#include <time.h>
#include <cstdlib>
#include <papi.h>
#include <iomanip>

using namespace std;

#define START1 600
#define INC1   400
#define ENDDIM1 3000

#define START2 4096
#define INC2   2048
#define ENDDIM2 10240

//#define name_file_out "data_results.txt"

FILE *fout;


#define SYSTEMTIME clock_t

void OnMult(int m_ar, int m_br)
{

    SYSTEMTIME Time1, Time2;

    char st[100];
    double temp;
    int i, j, k;

    double *pha, *phb, *phc;

    //it takes for granted that num_columns=num_rows
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    //initialization of two arrays to use as matrices
    for(i=0; i<m_ar; i++)
        for(j=0; j<m_ar; j++)
            pha[i*m_ar + j] = (double)1.0;

    for(i=0; i<m_br; i++)
        for(j=0; j<m_br; j++)
            phb[i*m_br + j] = (double)(i+1); //I don't remember why it's defined like this


    Time1 = clock();

    //multiplication: row x column
    for(i=0; i<m_ar; i++)
    {	for( j=0; j<m_br; j++) //each row of the first is multiplied
            //to all the columns of the second to form the corresponding row in the final matrix
        {	temp = 0;
            for( k=0; k<m_ar; k++)
            {
                temp += pha[i*m_ar+k] * phb[k*m_br+j];
            }
            phc[i*m_ar+j]=temp;
        }
    }


    Time2 = clock();

    sprintf(st,"%d,%3.3f\n",m_ar,(double)(Time2 - Time1) / CLOCKS_PER_SEC);
    fprintf(fout,"%s",st);
    cout << st; //obv we can comment these out


    /* cout << "Result matrix: " << endl;
     for(i=0; i<m_ar; i++)
     {	for(j=0; j<m_br; j++)
             cout << phc[i*m_ar+j] << " ";
         cout<<endl;
     }
     cout << endl;
     */

    free(pha);
    free(phb);
    free(phc);


}

// add code here for line x line matriz multiplication
void OnMultLine(int m_ar, int m_br)
{
    SYSTEMTIME Time1, Time2;
    double temp;
    char st[100];
    int i, j, k;

    double *pha, *phb, *phc;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    //initialization of the arrays to use as matrices
    //we're using m_br and m_ar with no specific criteria cause for now they're the same
    for(i=0; i<m_ar; i++)
        for(j=0; j<m_br; j++) {
            pha[i * m_br + j] = (double) 1.0;
            phb[i * m_br + j] = (double) (i + 1);
            phc[i * m_br + j] = (double) 0.0;
        }

    Time1 = clock();

    //multiplication: linexline (is it the one in slide 29?)
    for(i=0; i<m_ar; i++)
    {	for( j=0; j<m_br; j++)
        {
            for(k=0; k<m_ar; k++)
            {
                phc[i*m_ar+k]+= pha[i*m_ar+j] * phb[j*m_br+k];
            }

        }
    }

    Time2 = clock();

    sprintf(st,"%d,%3.3f\n",m_ar,(double)(Time2 - Time1) / CLOCKS_PER_SEC);
    fprintf(fout,"%s",st);
    cout << st;

    /*cout << "Result matrix: " << endl;
    for(i=0; i<m_ar; i++)
    {	for(j=0; j<m_br; j++)
            cout << phc[i*m_ar+j] << " ";
        cout<<endl;
    }
    cout << endl;*/

    free(pha);
    free(phb);
    free(phc);

}

// Function that block x block matriz multiplication
void OnMultBlock(int m_ar, int m_br, int bkSize)
{
    SYSTEMTIME Time1, Time2;
    double *pha, *phb, *phc;
    char st[100];

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for(int i=0; i<m_ar; i++){
        for(int j=0; j<m_ar; j++){
            pha[i*m_ar + j] = (double)1.0;
            phb[i*m_br + j] = (double)(i+1);
            phc[i*m_br + j] = (double)0.0;
        }
    }

    Time1=clock();
    for(int ii=0; ii<m_ar; ii+=bkSize){
        for(int jj=0; jj<m_br; jj+=bkSize ){
            for(int i=0; i<m_ar; i++){
                for(int k=ii; k<min(ii+bkSize,m_ar); k++){
                    for(int j=jj; j<min(jj+bkSize,m_br); j++){
                        phc[i*m_ar +j]+= pha[i*m_ar +k]*phb[k*m_br +j];
                    }
                }
            }
        }
    }
    Time2 = clock();

    sprintf(st,"%d,%3.3f\n",m_ar,(double)(Time2 - Time1) / CLOCKS_PER_SEC);
   // fprintf(fout,"%s",st);
    cout << st;

    //printMatrix(phc, m_br);

    free(pha);
    free(phb);
    free(phc);
}

void handle_error (int retval)
{
    printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
    exit(1);
}

//Is this function useful for us?
void init_papi() {
    int retval = PAPI_library_init(PAPI_VER_CURRENT);
    if (retval != PAPI_VER_CURRENT && retval < 0) {
        printf("PAPI library version mismatch!\n");
        exit(1);
    }
    if (retval < 0) handle_error(retval);

    std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
              << " MINOR: " << PAPI_VERSION_MINOR(retval)
              << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}


int main (int argc, char *argv[])
{

    char c,st[100];
    int lin, col, blockSize;
    int op,startDim,endDim,inc;
    long long values[4];
    int start=0;
    int EventSet = PAPI_NULL;
    int ret;


    ret = PAPI_library_init( PAPI_VER_CURRENT );
    if ( ret != PAPI_VER_CURRENT )
        std::cout << "FAIL" << endl;


    ret = PAPI_create_eventset(&EventSet);
    if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;

    ret = PAPI_add_event(EventSet,PAPI_TOT_CYC );
    if (ret != PAPI_OK) cout << "ERROR: PAPI_TOT_CYC" << endl;


    ret = PAPI_add_event(EventSet,PAPI_L1_DCM );
    if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << endl;

    ret = PAPI_add_event(EventSet,PAPI_L2_DCM );
    if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << endl;


    ret = PAPI_add_event(EventSet,PAPI_L2_DCA);
    if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCA" << endl;

    //fout= fopen(name_file_out,"w");
    op=1;
    do {
        cout << endl << "1. Multiplication" << endl;
        cout << "2. Line Multiplication" << endl;
        cout << "3. Block Multiplication" << endl;
        cout << "Selection?: ";
        cin >>op;
        if (op == 0)
            break;
        /* printf("Dimensions: lins=cols ? ");
         cin >> lin;*/


        cout <<endl << "Starting dimension" << endl;
        cout << "1. 600x600" << endl;
        cout << "2. 4096x4096" << endl;
        cout << "Selection?: ";
        cin>>startDim;

        if(startDim==1){
            endDim=ENDDIM1;
            inc=INC1;
            col=START1;
        }
        else{
            endDim=ENDDIM2;
            inc=INC2;
            col=START2;
        }



        while(col<=endDim) {
            lin=col;
            // Start counting
            ret = PAPI_start(EventSet);
            if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

            switch (op) {
                case 1:
                    if(!start) {
                        sprintf(st,"\nChosen option: Multiplication\nMatrix size,time(s)\n");
                        fprintf(fout,"%s",st);
                        cout<<st;
                    }
                    OnMult(lin, col);
                    break;
                case 2:
                    if(!start) {
                        sprintf(st,"\nChosen option: Line Multiplication\nMatrix size,time(s)\n");
                        fprintf(fout,"%s",st);
                        cout<<st;
                    }
                    OnMultLine(lin, col);
                    break;
                case 3:
                    if(!start) { //I decide the block size only the first time
                        cout << "Block Size? ";
                        cin >> blockSize;
                        sprintf(st,"\nChosen option: Block Multiplication->Block size:%d\nMatrix size,time(s)\n",blockSize);
                        cout<<st;
                    }
                    OnMultBlock(lin, col, blockSize);
                    break;

            }
            ret = PAPI_stop(EventSet, values);
            if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
            printf("TOTAL CYLES: %lld \n", values[0]);
            printf("L1 DCM: %lld \n", values[1]);
            printf("L2 DCM: %lld \n", values[2]);
            printf("L2 DCA: %lld \n", values[3]);

            ret = PAPI_reset(EventSet);
            if (ret != PAPI_OK)
                std::cout << "FAIL reset" << endl;


            start=1;
            col += inc;
        }
        start=0;


    }while (op != 0);

    ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
    if ( ret != PAPI_OK )
        std::cout << "FAIL remove event" << endl;
    
    ret = PAPI_remove_event( EventSet, PAPI_TOT_CYC );
    if ( ret != PAPI_OK )
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
    if ( ret != PAPI_OK )
        std::cout << "FAIL remove event" << endl;
    
    ret = PAPI_remove_event( EventSet, PAPI_L2_DCA );
    if ( ret != PAPI_OK )
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_destroy_eventset( &EventSet );
    if ( ret != PAPI_OK )
        std::cout << "FAIL destroy" << endl;

}
