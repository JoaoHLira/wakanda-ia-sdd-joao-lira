# SNS Topics outputs
output "sns_topics" {
  description = "Map of SNS topics created"
  value = {
    for k, topic in aws_sns_topic.topics : k => {
      id   = topic.id
      arn  = topic.arn
      name = topic.name
    }
  }
}

# SQS Queues outputs
output "sqs_queues" {
  description = "Map of SQS queues created"
  value = {
    for k, queue in aws_sqs_queue.queues : k => {
      id   = queue.id
      arn  = queue.arn
      name = queue.name
      url  = queue.url
    }
  }
}

# SQS Dead Letter Queues outputs
output "sqs_dlq_queues" {
  description = "Map of SQS dead letter queues created"
  value = {
    for k, queue in aws_sqs_queue.dlq_queues : k => {
      id   = queue.id
      arn  = queue.arn
      name = queue.name
      url  = queue.url
    }
  }
}

# SNS Subscriptions outputs
output "sns_subscriptions" {
  description = "Map of SNS subscriptions created"
  value = {
    for k, subscription in aws_sns_topic_subscription.subscriptions : k => {
      id      = subscription.id
      arn     = subscription.arn
      topic_arn = subscription.topic_arn
      endpoint = subscription.endpoint
    }
  }
}

# Messaging services outputs (grouped by service)
output "messaging_services" {
  description = "Map of complete messaging services (topic + queue + subscription)"
  value = {
    for k, service in var.messaging_services : k => {
      topic_arn = aws_sns_topic.topics[service.topic_name].arn
      queue_url = aws_sqs_queue.queues[k].url
      queue_arn = aws_sqs_queue.queues[k].arn
      dlq_url   = service.has_dlq ? aws_sqs_queue.dlq_queues[k].url : null
      dlq_arn   = service.has_dlq ? aws_sqs_queue.dlq_queues[k].arn : null
      has_subscription = service.create_subscription
    }
  }
}

# Specific topic ARNs for easy reference
output "topic_arns" {
  description = "Map of topic ARNs by name for easy reference"
  value = {
    for k, topic in aws_sns_topic.topics : k => topic.arn
  }
}

# Specific queue URLs for easy reference
output "queue_urls" {
  description = "Map of queue URLs by service key for easy reference"
  value = {
    for k, queue in aws_sqs_queue.queues : k => queue.url
  }
}

# DLQ URLs for easy reference
output "dlq_urls" {
  description = "Map of DLQ URLs by service key for easy reference"
  value = {
    for k, queue in aws_sqs_queue.dlq_queues : k => queue.url
  }
}
