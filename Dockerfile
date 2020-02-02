FROM openjdk:8

# copy files
COPY ./deploy/ /
WORKDIR /

# volume for data
VOLUME /tmp/file

# do sth
CMD ["sh", "launch.sh"]

EXPOSE 8011
