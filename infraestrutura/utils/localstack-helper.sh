# Script para auxílio no desenvolvimento, para setup, use os scripts de localstack init!

# variáveis de ambiente
LOCALSTACK_TOPIC_NAME="test-topic.fifo"
LOCALSTACK_DLQ_LOCALSTACK_QUEUE_NAME="test-dlq-queue.fifo"
LOCALSTACK_QUEUE_NAME="test-queue.fifo"

# Cria um tópico SNS FIFO
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name $LOCALSTACK_TOPIC_NAME --attributes FifoTopic=true

# Cria uma fila DLQ(Dead-Letter Queue) SQS FIFO para armazenar mensagens que falharam
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name $LOCALSTACK_DLQ_LOCALSTACK_QUEUE_NAME --attributes FifoQueue=true

# Cria uma fila SQS e configura para enviar mensagens para a DLQ após falhas
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name $LOCALSTACK_QUEUE_NAME  --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:$LOCALSTACK_DLQ_LOCALSTACK_QUEUE_NAME\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"

# Inscreve a fila SQS FIFO no tópico SNS FIFO
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:$LOCALSTACK_TOPIC_NAME --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:$LOCALSTACK_QUEUE_NAME

# Verifica redrive policy
aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://localstack:4566/000000000000/$LOCALSTACK_QUEUE_NAME --attribute-name RedrivePolicy

# Verifica se SNS e SQS estão inscritos
aws --endpoint-url=http://localhost:4566 sns list-subscriptions-by-topic --topic-arn arn:aws:sns:us-east-1:000000000000:$LOCALSTACK_TOPIC_NAME

# Verifica mensagens na fila
aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/$LOCALSTACK_QUEUE_NAME --attribute-names ApproximateNumberOfMessages

# Verifica mensagens na DLQ
aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/$LOCALSTACK_DLQ_LOCALSTACK_QUEUE_NAME --attribute-names ApproximateNumberOfMessages

# Publica notificação no tópico
aws --endpoint-url=http://localhost:4566 sns publish  --topic-arn arn:aws:sns:us-east-1:000000000000:$LOCALSTACK_TOPIC_NAME --message "Esta é uma mensagem de teste para o tópico FIFO" --message-group-id "group1" --message-deduplication-id "uniqueId1"

# Consome mensagens da fila SQS
aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/$LOCALSTACK_QUEUE_NAME --max-number-of-messages 10

