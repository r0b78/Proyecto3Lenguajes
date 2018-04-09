/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: cp
 *
 * Created on 7 de abril de 2018, 11:23 PM
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <pthread.h>
#include <signal.h>
#include <errno.h>
#include <time.h>



#define NLOOPS 1000000

const char *port = "8080";
int numConnections = 0;
int scClien[100];
int lenClientes=0;

int matrizJugo []={1,2,3,1,2,3,1,23,123,13,32};
int lenMatriz=0;

/* Added: A lock to access numConnections.
   Note that this file needs to be compiled
   with -DMUTEX for the lock to actually be used */
#ifdef MUTEX
pthread_mutex_t lock;
#endif


struct workerArgs
{
    int socket;
};
struct Node
{
  int data;
  struct Node *next;
};
int largoLista=0;

void *accept_clients(void *args);
void *service_single_client(void *args);
void push(struct Node** head_ref, int new_data);
int get(int n,struct Node *lista);
struct Node* crearLista();
int deletee(struct Node** lista,int n);
void printList(struct Node *node);



//Funcion Principal del server
int main(int argc, char *argv[])

{
    
    
    
    pthread_t server_thread;

    sigset_t new;
    sigemptyset (&new);
    sigaddset(&new, SIGPIPE);
    if (pthread_sigmask(SIG_BLOCK, &new, NULL) != 0) 
    {
        perror("Unable to mask SIGPIPE");
        exit(-1);
    }

    /* ADDED: Initialize the lock */
    #ifdef MUTEX
    pthread_mutex_init(&lock, NULL);
    #endif

    if (pthread_create(&server_thread, NULL, accept_clients, NULL) < 0)
    {
        perror("Could not create server thread");
        exit(-1);
    }
    
    
    pthread_join(server_thread, NULL);
    
    /* ADDED: Destroy the lock */
    #ifdef MUTEX
    pthread_mutex_destroy(&lock);
    #endif

    pthread_exit(NULL);
}

void *service_single_client(void *args) {
    struct workerArgs *wa;
    int socket, nbytes, i;
    char buffer[100];
    wa = (struct workerArgs*) args;
    socket = wa->socket;

    pthread_detach(pthread_self());

    /* ADDED: Protect access to numConnections with the lock */
    #ifdef MUTEX
    pthread_mutex_lock (&lock);
    #endif

    /* The following two loops will result in numConnections
       being ultimately incremented by just one, but we do
       this with these loops to increase the chances of a
       race condition happening */
    for(i=0; i< NLOOPS; i++)
        numConnections++;
    for(i=0; i< NLOOPS-1; i++)
        numConnections--;
    fprintf(stderr, "+ Number of connections is %d\n", numConnections);

    /* ADDED: Unlock the lock when we're done with it. */
    #ifdef MUTEX
    pthread_mutex_unlock (&lock);
    #endif

    while(1)
    {
        memset(buffer,0,strlen(buffer));
        nbytes = recv(socket, buffer, sizeof(buffer), 0);
        if (nbytes == 0)
            break;
        else if (nbytes == -1)
        {
            perror("Socket recv() failed");
            close(socket);
            pthread_exit(NULL);
        }
        /* Ignore anything that's actually recv'd. We just want
           to keep the connection open until the client disconnects */
        printf(buffer);
        #ifdef MUTEX
         pthread_mutex_lock (&lock);
        #endif
         for(int x=0;x<strlen(buffer);x++){
             printf("Letra: %c \n",buffer[x]);
         }
         //////////Recorta el string para compararlo
         buffer[strlen(buffer)-1]=0;
         ///////////////
        
         if(strcmp(buffer,"esto")==0){
             printf("entro\n");
             matrizJugo[1]=300;
         }
          if(strcmp(buffer,"aque")==0){
             printf("entro\n");
             matrizJugo[4]=400;
         }
         
//        for(i=0; i< 9; i++){
//          //  char * str[10];
//            printf("Numero:%d \n",matrizJugo[i]);
//        
//            //sprintf(str, "%d ", matrizJugo[i]);
//        //sendall(str);
//        }
        fprintf(stderr, "- Number of connections is %d\n", numConnections);
        
        #ifdef MUTEX
    pthread_mutex_unlock (&lock);
        #endif
    strcat(buffer,"\n");    
    sendall(buffer);
        //sendall(buffer);
    }
    
    

    /* ADDED: Same as the above loops, but decrementing numConnections by one */
    #ifdef MUTEX
    pthread_mutex_lock (&lock);
    #endif
    for(i=0; i< NLOOPS; i++)
        numConnections--;
    for(i=0; i< NLOOPS-1; i++)
        numConnections++;
    fprintf(stderr, "- Number of connections is %d\n", numConnections);
    
    #ifdef MUTEX
    pthread_mutex_unlock (&lock);
    #endif

    close(socket);
    pthread_exit(NULL);
}
int sendall(char* str){
    for (int i=0;i<lenClientes;i++){
         send(scClien[i], str, strlen(str), 0);
        
    }
}
//int sendall(char* str,int sockActual){
//    for (int i=0;i<lenClientes;i++){
//        if(scClien[i]!=sockActual){
//         send(scClien[i], str, strlen(str), 0);
//        }
//    }
//}
void *accept_clients(void *args)
{
    int serverSocket;
    int clientSocket;
    pthread_t worker_thread;
    struct addrinfo hints, *res, *p;
    struct sockaddr_storage *clientAddr;
    socklen_t sinSize = sizeof(struct sockaddr_storage);
    struct workerArgs *wa;
    int yes = 1;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    if (getaddrinfo(NULL, port, &hints, &res) != 0)
    {
        perror("getaddrinfo() failed");
        pthread_exit(NULL);
    }

    for(p = res;p != NULL; p = p->ai_next) 
    {
        if ((serverSocket = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1) 
        {
            perror("Could not open socket");
            continue;
        }

        if (setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1)
        {
            perror("Socket setsockopt() failed");
            close(serverSocket);
            continue;
        }

        if (bind(serverSocket, p->ai_addr, p->ai_addrlen) == -1)
        {
            perror("Socket bind() failed");
            close(serverSocket);
            continue;
        }

        if (listen(serverSocket, 5) == -1)
        {
            perror("Socket listen() failed");
            close(serverSocket);
            continue;
        }

        break;
    }
    
    freeaddrinfo(res);

    if (p == NULL)
    {
        fprintf(stderr, "Could not find a socket to bind to.\n");
        pthread_exit(NULL);
    }

    while (1)
    {
        clientAddr = malloc(sinSize);
        if ((clientSocket = accept(serverSocket, (struct sockaddr *) clientAddr, &sinSize)) == -1) 
        {
            free(clientAddr);
            perror("Could not accept() connection");
            continue;
        }

        wa = malloc(sizeof(struct workerArgs));
        wa->socket = clientSocket;
        #ifdef MUTEX
        pthread_mutex_lock (&lock);
        #endif
        scClien[lenClientes]=clientSocket;
        lenClientes++;
        #ifdef MUTEX
        pthread_mutex_unlock (&lock);
        #endif

        if (pthread_create(&worker_thread, NULL, service_single_client, wa) != 0) 
        {
            perror("Could not create a worker thread");
            free(clientAddr);
            free(wa);
            close(clientSocket);
            close(serverSocket);
            pthread_exit(NULL);
        }
    }

    pthread_exit(NULL);
}

#include <stdio.h>
#include <stdlib.h>

/*
 * 
 */
// A linked list node

void push(struct Node** head_ref, int new_data)
{
    
    struct Node* new_node = (struct Node*) malloc(sizeof(struct Node));
  
   
    new_node->data  = new_data;
  
   
    new_node->next = (*head_ref);
  
    (*head_ref)    = new_node;
    largoLista++;
}
void printList(struct Node *node)
{
  while (node != NULL)
  {
     printf("%d ", node->data);
     node = node->next;
  }
}
struct Node* crearLista(){
    struct Node* head = NULL;
    return head;
}
int get(int n,struct Node *lista){
    int cont=0;
   
    while (lista!=NULL){
        if(cont==n){
            printf("Esto");
            return lista->data;
           
        }
        
        lista=lista->next;
        cont++;
    }
    return 100000;
}

int deletee(struct Node** lista,int n){
    int cont=0;
    struct Node* tmp=*lista;
    struct Node* aux;
     if(cont==n){
           *lista=tmp->next;
            return 1;
        }
    while(tmp!=NULL){
        if(cont==n){
            aux->next=tmp->next;
            return 1;
        }
        aux=tmp;
        tmp =tmp->next;
        cont++;
        printf("asdf\n");
        
    }
    return 0;
}




