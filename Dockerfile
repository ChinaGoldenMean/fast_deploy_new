FROM fast-deploy:1.0

COPY target/fast_deploy.jar  /app/

RUN chown deploy.deploy /app/fast_deploy.jar

WORKDIR /app

USER deploy

CMD ["java","-jar","fast_deploy.jar","--spring.profiles.active=prod"]



#RUN cd /app && mkdir dubbo && tar xzf dubbo.tar.gz -C dubbo --strip-components 1 && rm -rf *.tar.gz && chmod 777 -R /app   && chown -R isale:isale /app
#RUN rm -rf /app && userdel -r isale && useradd -u 2223 -m -d /app deploy && chown -R deploy.deploy /app
#WORKDIR /app
#USER deploy
#ENV LANG en_US.UTF-8
#ENV TZ Asia/Shanghai

#CMD ["/app/dubbo/bin/start.sh"]