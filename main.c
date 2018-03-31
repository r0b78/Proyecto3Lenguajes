/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: Familia
 *
 * Created on 29 de marzo de 2018, 10:44 PM
 */
/*Required Headers*/
 
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>
#include<string.h>
#include <sys/unistd.h>
 
int main()
{
    int valor[5];
    int lenValor=1;
    int scClien[3];
    char str[100];
    int listen_fd, comm_fd;
    
    struct sockaddr_in servaddr;
 
    listen_fd = socket(AF_INET, SOCK_STREAM, 0);
 
    bzero( &servaddr, sizeof(servaddr));
 
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = htons(INADDR_ANY);
    servaddr.sin_port = htons(8081);
   printf("entro aca");
    bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
 
    listen(listen_fd, 10);
     printf("entro aca");
    for(int i=0;i<lenValor;i++){
    scClien[i] = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    printf("entro aca");
    }
     printf("entro aca\n");
     char contentBuffer[255];
    while(1)
    {
        printf("entro aca\n");
        bzero( str, 100);
 
        
        if(read(scClien[0],str,100)==-1){
            printf("conexion cerrada");
            break;
        }
        strncpy(contentBuffer,str,strlen(str));  
        printf("%s \n",contentBuffer);
        
        
        //valor[lenValor]=atoi(str);
        
       //printf("%d",valor[lenValor]);
       // printf("\n");
      //  lenValor++;
        printf("Echoing back - %s",str);
        printf("\n");
      
        
       // writev(scClien[0],str,strlen(str)+1);
       
        strcat(str,"\n");
       // printf("Este es test: %s",test);
        send(scClien[0], str, strlen(str), 0);
        
//        for(int i=0;i<lenValor;i++){
//            
//        write(scClien[i], str, strlen(str)+1);
// 
//        }
    }
}