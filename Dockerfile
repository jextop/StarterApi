FROM openjdk:8

# copy code
COPY ./ /code
WORKDIR /code

# package and copy files
RUN sh mvnw package -Dmaven.test.skip=true; \
    \
    mv ./deploy/ /deploy; \
    mv ./target/api-0.0.1-SNAPSHOT.jar /deploy

WORKDIR /deploy

# delete code
RUN rm -rf /code

# volume for data
VOLUME /tmp/file

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
