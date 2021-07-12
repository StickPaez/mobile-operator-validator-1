FROM java:8

RUN wget https://download-gcdn.ej-technologies.com/jprofiler/jprofiler_linux_10_1_5.tar.gz
RUN tar xzf jprofiler_linux_10_1_5.tar.gz

CMD ["mkdir", "/home/app/"]
COPY Files/app/__jarName__ /home/app/

EXPOSE __port__