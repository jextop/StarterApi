FROM registry.cn-shanghai.aliyuncs.com/jext/starter_api_base:latest

# copy files
COPY ./ /code
WORKDIR /code

# package
RUN sh mvnw package -Dmaven.test.skip=true

# copy files
RUN cp ./target/api-0.0.1-SNAPSHOT.jar ./deploy
RUN cp -r ./deploy/ /deploy
WORKDIR /deploy/

# delete code
RUN rm -rf /code/

# volume for data
VOLUME /tmp/file

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
