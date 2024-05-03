# Awesome Pizza BE
___
### Requirement:
Come pizzeria "Awesome Pizza" voglio creare il mio nuovo portale per gestire gli ordini dei miei clienti. 
Il portale non richiede la registrazione dell'utente per poter ordinare le sue pizze. 
Il pizzaiolo vede la coda degli ordini e li può prendere in carico uno alla volta. 
Quando la pizza è pronta, il pizzaiolo passa all'ordine successivo. 
L'utente riceve il suo codice d'ordine e può seguire lo stato dell'ordine fino all'evasione.

Come team, procediamo allo sviluppo per iterazioni. 
Decidiamo che nella prima iterazione sarà disponibile un'interfaccia grafica, e verranno create delle API al fine di ordinare le pizze e aggiornarne lo stato. 
Decidiamo di utilizzare il framework Spring e Java (versione a tua scelta). 
Decidiamo di progettare anche i test di unità sul codice oggetto di sviluppo.

___
### How to start the application:
From the project folder, run the following commands: 
   - docker image rm awesomepizza-be [DO THIS ONLY IF THE IMAGE WAS ALREADY PRESENT]
   - docker build -t awesomepizza-be .
   - docker compose up -d

    CAUTION: the first command requires the final point in order to work!
