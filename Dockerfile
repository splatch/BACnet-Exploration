FROM oraclelinux:8

RUN yum upgrade \
	&& yum install -y java-17-openjdk \
	&& yum install -y vim net-tools
	
ADD target/BACnet-Exploration-0.0.1-SNAPSHOT.jar /
ADD src/main/resources/application.properties /

EXPOSE 47808:47808/udp
EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.config.location=/application.properties", "-jar", "BACnet-Exploration-0.0.1-SNAPSHOT.jar"]
