# Spotify Stats Backend
Spotify Stats - Backend is a Spring Boot backend that is responsible for storing the audio features for a particular artists discography. Audio features refer to the metrics that Spotify stores for each song that is uploaded. Some of these audio features are danceability, loudness, and acousticness. The artists that can be searched for are artists working on music in the 'dubstep' genre. Requests to the backend are responsible for: 
 <ul>
 <li>Returning all artists that can be searched for</li>
 <li>Returning all songs and their audio features (which are displayed in a line graph on the React frontend)</li>
 <li>Adding new artists to the database and getting their song/audio features via AWS Serverless Lambda Function. The lambda function responds to a DynamoDB trigger which occurs when new artists are created. Once the artist is created, the lambda function will talk to the Spotify API to find out the discography and metrics for a particular artist. </li>
 </ul>

# Installation
1) Clone the repository or download the zip file from the 'Releases' menu
2) Users can also choose to pull from DockerHub via https://hub.docker.com/repository/docker/shayder426/spotify-stats-01.
3) The following are the required command line arguments

<ul>
  <li>$PORT - Port used to run the backend</li>
  <li>AWS_REGION - Region of AWS account</li>
  <li>AWS_SECRET_KEY - Secret key associated with user</li>
  <li>AWS_ACCESS_KEY - Access key associated with user</li>
  <li>PRODUCTION_OR_DEVELOPMENT - If value is set to 'production', environment will refer to DynamoDB Web Service. If value is set to 'development', environment will refer to local running instance of DynamoDB.</li>
  <li>$DYNAMODB_PORT - Port where DynamoDB is running on. Value is ignored for production environments.</li>
  <li>SPOTIFY_CLIENT_ID - ClientID for Spotify Application</li>
  <li>SPOTIFY_CLIENT_SECRET - ClientSecret for Spotify Application</li>
</ul>

4) To run via docker container, run the following command.

```shell               
docker run -d -p $PORT:$PORT shayder426/spotify-stats-01:latest --aws.region="AWS_REGION" --aws.env="PRODUCTION_OR_DEVELOPMENT" --aws.endpoint="http://localhost:$DYNAMODB_PORT" --aws.accessKey="AWS_ACCESS_KEY" --aws.secretKey="AWS_SECRET_KEY" --spotifyClientId="SPOTIFY_CLIENT_ID" --spotifyClientSecret="SPOTIFY_CLIENT_SECRET" --server.port=$PORT
```
5) To run via command line, run the following command.

```shell
mvn spring-boot:run -Dspring-boot.run.arguments="--aws.region="AWS_REGION" --aws.env="PRODUCTION_OR_DEVELOPMENT" --aws.endpoint="http://localhost:$DYNAMODB_PORT" --aws.accessKey="AWS_ACCESS_KEY" --aws.secretKey="AWS_SECRET_KEY" --spotifyClientId="SPOTIFY_CLIENT_ID" --spotifyClientSecret="SPOTIFY_CLIENT_SECRET" --server.port=$PORT"
```

# Built With
<ul>
 <li>Java/Spring Boot - Java Framework </li>
 <li>DynamoDB - Database</li>
 <li>AWS Lambda - Serverless function used to add the music for a new artist</li>
 <li>Github Actions - CI/CD Tool</li>
 <li>Docker - Container that runs the Spring Boot backend</li>
 <li>Nginx - Reverse proxy for DNS</li>
</ul>

# Authors
Syed Haider
