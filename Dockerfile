FROM openjdk:17-ea-17-oraclelinux8
RUN microdnf install git -y
RUN git clone https://github.com/spring-cloud/spring-cloud-config.git
RUN mkdir spring-cloud-config-local-copy
COPY . /spring-cloud-config-local-copy
ENTRYPOINT ["/bin/sh"]