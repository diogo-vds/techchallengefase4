FROM public.ecr.aws/lambda/java:21

# Copy Maven-built JAR
COPY target/avaliacao-function.jar ${LAMBDA_TASK_ROOT}/

# Handler
CMD [ "br.com.postech.techchallenge.fase4.function.AvaliacaoFunction::handleRequest" ]
