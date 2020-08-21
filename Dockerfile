FROM registry.cn-shanghai.aliyuncs.com/jext/starter_api_base:latest

LABEL maintainer="Jext Community, https://github.com/jextop"

# copy code
COPY ./ /code
WORKDIR /code

# package and copy files
RUN sh mvnw package -Dmaven.test.skip=true; \
    \
    mv ./deploy/ /deploy; \
    mv ./target/api-0.0.1-SNAPSHOT.jar /deploy; \
    \
    cd ..; \
    rm -rf /code; \
    ls -al

WORKDIR /deploy

# volume for data
VOLUME /tmp/files

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
