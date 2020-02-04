FROM openjdk:8

# copy files
COPY ./ /code
WORKDIR /code

# package
RUN sh mvnw package -Dmaven.test.skip=true

# copy files
RUN mv ./deploy/ /deploy/
RUN mv ./target/api-0.0.1-SNAPSHOT.jar ./deploy/
WORKDIR /deploy/

# delete code
RUN rm -rf /code/

# volume for data
VOLUME /tmp/file

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
