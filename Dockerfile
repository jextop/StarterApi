FROM openjdk:8

# copy files
COPY ./deploy/ /root/api/
WORKDIR /root/api/

# volume for data
VOLUME /tmp/file

# do sth
CMD ["./launch.sh"]

EXPOSE 8011
