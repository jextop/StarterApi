FROM openjdk:8

# copy files
COPY ./deploy/ /deploy
WORKDIR /deploy

# volume for data
VOLUME /tmp/file

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
