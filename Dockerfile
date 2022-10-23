FROM node:18-alpine3.15 as node
WORKDIR /node
ENV PATH /node/node_modules/.bin:$PATH
COPY /csh-pings-frontend/package.json ./
COPY /csh-pings-frontend/package-lock.json ./
RUN npm ci --silent --production
COPY /csh-pings-frontend/src/ ./src/
COPY /csh-pings-frontend/public/ ./public/
COPY /csh-pings-frontend/tsconfig.json ./
COPY .git/ .git/
COPY ./csh-pings-frontend/git-parse.sh git-parse.sh
RUN apk add git
RUN sh ./git-parse.sh
RUN npm run build

FROM maven:3.8.6-openjdk-18 as maven
COPY csh-pings-backend/src/ ./src/
COPY csh-pings-backend/pom.xml .
COPY --from=node /node/build/ ./src/main/resources/static/
RUN mvn package -e -B

FROM openjdk:18-jdk-alpine
COPY --from=maven target/*.jar app.jar
RUN apk add zip
CMD java -jar app.jar
