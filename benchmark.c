#include <stdio.h>
#include <stdlib.h>

#include <time.h>

int main(void)
{
    // clock_t start = clock();
    for (int i = 0; i < 10000; i++)
    {
        system("./md --addMolecule Molecules/acetylene.txt");
    }
    for (int i = 0; i < 10000; i++)
    {
        system("./md --findMolecule Molecules/biotin.txt");
    }
    // clock_t diff = clock() - start;
    // int m_sec = diff * 1000 / CLOCKS_PER_SEC;
    // printf("time taken %d seconds %d milliseconds\n", m_sec/1000, m_sec % 1000);
    return 0;
}
