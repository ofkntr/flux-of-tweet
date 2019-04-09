##fluxoftweet

fluxoftweet is a simple web application that written in java 8,project reactor,spring boot 2.0,spring web flux, spring data, reactive kafka, thymeleaf, embedded h2.
 
All unit tests are written in junit and mockito.

Simply you can run this app with "mvn spring-boot:run", but firstly be sure up and run kafka and zookeeper.

If you want to use docker to run this application.You can simply run "mvn clean package docker:build" command for creating docker image which name is fluxoftweet. 
After docker image is created, just run "docker run -p 9001:9001 fluxoftweet -t -d". Now you can access the app from docker machine ip.

##You can follow below the installation receip

- `docker-machine create default`If docker machine not exist on your os.Firstly create docker machine.
- `docker-machine start default` Start docker-machine
- `docker-machine env default` Expose environment variables
- `eval $(docker-machine env default)`  Add to environment variables on terminal
- `docker-compose up -d` Up and run kafka and zookeeper in flux-of-tweet-app.
- `docker-machine ip default` Learn your ip and change server host setting from application.properties.
- `mvn clean package docker:build` Create fluxoftweet docker image using with spotify maven plug-in.
- `docker-compose up -d` Up and run fluxoftweet app on docker or docker run -p 9001:9001 fluxoftweet -t -d.

You can use portainer to manage all docker container
- `docker volume create portainer_data`
- `docker run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer`

If you want to show relational tweet -> user data on H2 Database.You can open the browser below the link.
- Link :`http://DOCKER_HOST_IP:9098/login.jsp?jsessionid=413a6cb974d9382a307d720ddd82f789` on browser
- For Example`http://192.168.99.100:9098/login.jsp?jsessionid=413a6cb974d9382a307d720ddd82f789` on browser
- Connection URL : `jdbc:h2:mem:testdb`
- Username : `sa`
- Password :

Below an example of how to run the program, every argument will be used as a filter track, 
if no arguments are present a default filter by #trump :) He is very popular on twitter. 
Tracks can be hastags, twitter handler names or just simply some text.

`java -jar flux-of-tweet-app/target/flux-of-tweet-app-0.0.1-SNAPSHOT.jar "#bieber" "webflux" "@reactor"` 

Display the tweets, you can open it at `http://DOCKER_HOST_IP:9001`


