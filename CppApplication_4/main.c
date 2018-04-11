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

const char *port = "8081";
int numConnections = 0;
int scClien[100];
int lenClientes=0;

int matrizJugo []={1,2,3,1,2,3,1,23,123,13,32};
int lenMatriz=0;
///
//10 columnas 5 filas;
///
////matrz , Xposmatriz, Yposmatriz,Velmatriz,Xjug,Vjug,Puntaje,diparojug,murods
///bicho disparo/
/*
 private String matrix = 
                  "2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/"
                + "1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/"
                + "3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/3 0/"
                + "1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/1 0/"
                + "2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0/2 0,"
                + "390,600,1,"
                + "390,5,0,0,"
                + "1111111111111111111111111111111111111111111111111111111111111111111111,"
                + "1111111111111111111111111111111111111111111111111111111111111111111111,"
                + "1111111111111111111111111111111111111111111111111111111111111111111111";

Escribe un mensaje...
 */



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
int split (const char *str, char c, char ***arr);
void append(struct Node** head_ref, int new_data);
int getLargo(struct Node* node);
int parsearStringMatirz(struct Node** headd,struct Node** head2,char* string);
int matrizAString(struct Node* lista1,struct Node* lista2,char** stringSalida);
int armarEstructura(char* s,char*** arr,int lenArr);



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
    char buffer[700];
    wa = (struct workerArgs*) args;
    socket = wa->socket;

    pthread_detach(pthread_self());
    int splitLen=0;
    /* ADDED: Protect access to numConnections with the lock */
    #ifdef MUTEX
    pthread_mutex_lock (&lock);
    #endif

    numConnections++;
    /* ADDED: Unlock the lock when we're done with it. */
    #ifdef MUTEX
    pthread_mutex_unlock (&lock);
    #endif
      struct Node* listaPersonaje;
       struct Node* lista2Disparo;
       char* StringMandar[700];
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
        /* 
           
                                     */
        printf("\nEste es el buff: %s\n",buffer);
        #ifdef MUTEX
         pthread_mutex_lock (&lock);
        #endif
         for(int x=0;x<strlen(buffer);x++){
             printf("Letra: %c \n",buffer[x]);
         }
         //////////Recorta el string para compararlo
         buffer[strlen(buffer)-1]=0;
         char** spliit=NULL;
         splitLen= split(buffer,',',&spliit);
         
         printf("LennSpli:%d\n",splitLen>0);
         
         char* s=spliit[0];
         for(int i=0;i<splitLen;i++){
             printf("Spliita:%s \n",spliit[i]);
         
         }
                    
         ///////////////
         if(splitLen>0){
             printf("LenLista%d\n",splitLen);
             if(strcmp(spliit[0],"add")==0){
                 
                 
             }else{
                if(strcmp(spliit[0],"delete")==0){
                 printf(spliit[1]);
                 
                 
                }else{
                  if(splitLen==10){
                    printf("cayo al else\n");
                    listaPersonaje=crearLista();
                    lista2Disparo=crearLista();
                    printf("cayo al paso matri\n");
                    
                    parsearStringMatirz(&listaPersonaje,&lista2Disparo,s);
                    
                    printf("cpaso stri%sngMatri\n",s);
                    
                    memset(StringMandar,0,sizeof(StringMandar));
                    matrizAString(listaPersonaje,lista2Disparo,&StringMandar);
                    
                    armarEstructura(StringMandar,spliit,splitLen);
                    
                    strcat(StringMandar,"\n");
                    printf("LInea Mandar %s \n",StringMandar);
                    printList(listaPersonaje);
                    printf("Division\n");
                    printList(lista2Disparo);
                    
                    printf("Division\n");
                    
                    sendall(StringMandar);
            }else {
                printf("Esta en el ELSE ELSE \n");
                sendall(buffer);
                
            }
                }
                
             }
         }
         
                    printf("salio del if\n");
         
         
//        for(i=0; i< 9; i++){
//          //  char * str[10];
//            printf("Numero:%d \n",matrizJugo[i]); 
       //  strcat(buffer,"\n");
//            //sprintf(str, "%d ", matrizJugo[i]);
       // sendall(buffer);
//        }
        fprintf(stderr, "- Number of connections is %d\n", numConnections);
        
        #ifdef MUTEX
    pthread_mutex_unlock (&lock);
        #endif

    
    ///Concatena \n para que java detecte readline
//    strcat(buffer,"\n");    
//    ///
//    sendall(buffer);
        //sendall(buffer);
    }
    
    

   
    #ifdef MUTEX
    pthread_mutex_lock (&lock);
    #endif
    numConnections--;
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
  //  largoLista++;
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
int split (const char *str, char c, char ***arr)
{
  int count = 1;
  int token_len = 1;
  int i = 0;
  char *p;
  char *t;

  p = str;
  while (*p != '\0')
    {
      if (*p == c)
        count++;
      p++;
    }

  *arr = (char**) malloc(sizeof(char*) * count);
  if (*arr == NULL)
    exit(1);

  p = str;
  while (*p != '\0')
    {
      if (*p == c)
      {
        (*arr)[i] = (char*) malloc( sizeof(char) * token_len );
        if ((*arr)[i] == NULL)
          exit(1);

        token_len = 0;
        i++;
      }
      p++;
      token_len++;
    }
  (*arr)[i] = (char*) malloc( sizeof(char) * token_len );
  if ((*arr)[i] == NULL)
    exit(1);

  i = 0;
  p = str;
  t = ((*arr)[i]);
  while (*p != '\0')
    {
      if (*p != c && *p != '\0')
      {
        *t = *p;
        t++;
      }
      else
      {
        *t = '\0';
        i++;
        t = ((*arr)[i]);
      }
      p++;
    }

  return count;
}
int getLargo(struct Node* node){
    int largo=0;
    while (node != NULL)
  {
        largo++;
        node = node->next;
  }
    return largo; 
}
int parsearStringMatirz(struct Node** headd,struct Node** head2,char* string){
    
    struct Node*tmpL=*headd;
    struct Node* tmpL2=*head2;
    
    
    char** tmp;
    int lenSplit=split(string,'/',&tmp);
    
    printf("este es Len%d",lenSplit);
    int lenInterno;
    for(int i=0;i<lenSplit-1;i++){
        printf("Este es el array: %s\n",tmp[i]);
        char**tmp2;
        lenInterno=split(tmp[i],' ',&tmp2);
        char* n1=tmp2[0];
        char* n2=tmp2[1];
        printf("as1df %s,\n",n1);
       // printList(tmpL);
        printf("as2df %s,\n",n2);
        // printList(tmpL2);
        int nuevoInt=atoi(n1);
        int nuevoInt2=atoi(n2);
        append(&tmpL,nuevoInt);
        append(&tmpL2,nuevoInt2);
       
       
        
        
        
    }
    *headd=tmpL;
    *head2=tmpL2;
    return 0;
    
}

int matrizAString(struct Node* lista1,struct Node* lista2,char** stringSalida){
    
    memset(stringSalida,0,sizeof(stringSalida));
    for (int i=0;i<getLargo(lista1);i++){
        printf("LenListEssdfdfff: %dsdfsdfsdfsdf %d\n",i,get(i,lista1));
        char buf[500];
        memset(buf,0,sizeof(buf));
        sprintf(buf,"%d",get(i,lista1));
        strcat(stringSalida,buf);
        
        strcat(stringSalida," ");
        char buf2[500];
        memset(buf2,0,sizeof(buf2));
        sprintf(buf2,"%d",get(i,lista2));
        strcat(stringSalida,buf2);
        
        strcat(stringSalida,"/");
        
        
      //  strcat(stringSalida,"f ");
      //  strcat(stringSalida,get(i,lista2)+'0');
      //  strcat(stringSalida,"/");
        
        
    }
    
    return stringSalida;
    
}
int armarEstructura(char* s,char*** arr,int lenArr){
    for (int i=0;i<lenArr;i++){
        strcat(s,arr[i]);
        if(i!=lenArr-1){
            strcat(s,",");
        }
    }
}
void append(struct Node** head_ref, int new_data)
{
    /* 1. allocate node */
    struct Node* new_node = (struct Node*) malloc(sizeof(struct Node));

    struct Node *last = *head_ref;  /* used in step 5*/
 
    /* 2. put in the data  */
    new_node->data  = new_data;

    /* 3. This new node is going to be the last node, so make next 
          of it as NULL*/
    new_node->next = NULL;

    /* 4. If the Linked List is empty, then make the new node as head */
    if (*head_ref == NULL)
    {
       *head_ref = new_node;
       return;
    }  
     
    /* 5. Else traverse till the last node */
    while (last->next != NULL)
        last = last->next;
 
    /* 6. Change the next of last node */
    last->next = new_node;
    return;    
}

