:: teste
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name teste-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name teste-queue.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:teste-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:teste-queue.fifo

:: zapi-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name zapi-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name zapi-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name zapi-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:zapi-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:zapi-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:zapi-requests-queue.fifo

:: zapi-requests-delay
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name zapi-requests-queue-delay-dlq
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name zapi-requests-queue-delay --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:zapi-requests-queue-delay-dlq\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"


:: memberkit-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name memberkit-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name memberkit-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name memberkit-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:memberkit-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:memberkit-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:memberkit-requests-queue.fifo

:: asaas-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name asaas-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name asaas-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name asaas-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:asaas-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:asaas-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:asaas-requests-queue.fifo

:: discord-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name discord-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name discord-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name discord-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:discord-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:discord-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:discord-requests-queue.fifo

:: progresso-wakander-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name progresso-wakander-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name progresso-wakander-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name progresso-wakander-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:progresso-wakander-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:progresso-wakander-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:progresso-wakander-requests-queue.fifo

:: xp-wakander-requests
aws --profile localstack --endpoint-url=http://localhost:4566 sns create-topic --name xp-wakander-requests-topic.fifo --attributes FifoTopic=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name xp-wakander-requests-queue-dlq.fifo --attributes FifoQueue=true
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name xp-wakander-requests-queue.fifo --attributes "{\"FifoQueue\":\"true\",\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:us-east-1:000000000000:xp-wakander-requests-queue-dlq.fifo\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\",\"VisibilityTimeout\":\"5\"}"
aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:xp-wakander-requests-topic.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:xp-wakander-requests-queue.fifo