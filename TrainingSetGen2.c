#include <stdio.h>
#include <stdlib.h>

double randDoubleInRangeX(float minX, float maxX)
{
    //Generate random value in range [min, max]
    //We know that min will always be non negative
    //Calculate max - min to get the range and add min to position correctly
    return (maxX - minX) * (double)rand() / (double)RAND_MAX + minX;
}
double randDoubleInRangeY(float minY, float maxY)
{
    //Generate random value in range [min, max]
    //We know that min will always be non negative
    //Calculate max - min to get the range and add min to position correctly
    return (maxY - minY) * (double)rand() / (double)RAND_MAX + minY;
}



int main(int argc, char* argv[]){

    FILE* fp;
    int numAmount[10] = {150, 150, 150, 150, 150, 75, 75, 75, 75, 150};
    float minXData[10] = {0.8, 0, 1.5, 0, 1.5, 0, 1.6, 0.8, 0.8, 0};
    float minYData[10] = {0.8, 0, 0, 1.5, 1.5, 0.8, 0.8, 0.3, 1.3, 0};
    float maxXData[10] = {1.2, 0.5, 2, 0.5, 2, 0.4, 2, 1.2, 1.2, 2};
    float maxYData[10] = {1.2, 0.5, 0.5, 2, 2, 1.2, 1.2, 0.7, 1.7, 2};
    
    srand(time(NULL));    
    fp = fopen("TrainingSet2.txt", "w");
    if(fp == NULL){
        printf("ERROR: Cannot create/open file.");
        exit(1);
    }

    float minX = 0, maxX = 0, minY = 0, maxY = 0;

    for(int n = 0; n < 10; n++){
        minX = minXData[n];
        maxX = maxXData[n];
        minY = minYData[n];
        maxY = maxYData[n];

        for(int i = 0; i < numAmount[n]; i++){
            double x = randDoubleInRangeX(minX,maxX);
            double y = randDoubleInRangeY(minY,maxY);

            //Write points to file
            fprintf(fp, "%lf %lf\n", x, y);
        }

    }
    
    fclose(fp);
    return 0;
}