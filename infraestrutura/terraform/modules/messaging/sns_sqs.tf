locals {
  common_tags = merge(var.common_tags, {
    Environment = var.env
    ManagedBy   = "terraform"
  })
}

locals {
  unique_topics = {
    for name in distinct([for s in var.messaging_services : s.topic_name]) : name =>
    ([for s in var.messaging_services : s if s.topic_name == name])[0]
  }
}

# SNS Topics
resource "aws_sns_topic" "topics" {
  for_each = local.unique_topics

  name                        = each.value.fifo_topic ? "${each.key}.fifo" : each.key
  fifo_topic                  = each.value.fifo_topic
  content_based_deduplication = each.value.fifo_topic ? try(each.value.content_based_deduplication, false) : null

  tags = merge(local.common_tags, {
    Name = each.value.fifo_topic ? "${each.key}.fifo" : each.key
    Type = "SNS-Topic"
  })
}

# SQS Queues (main queues)
resource "aws_sqs_queue" "queues" {
  for_each = var.messaging_services

  name = each.value.fifo_queue ? "${each.value.queue_name}.fifo" : each.value.queue_name

  fifo_queue = each.value.fifo_queue

  delay_seconds             = each.value.delay_seconds
  max_message_size          = 262144
  message_retention_seconds = each.value.message_retention_seconds
  receive_wait_time_seconds = 0
  visibility_timeout_seconds = each.value.visibility_timeout

  # Redrive policy for queues with DLQ
  redrive_policy = each.value.has_dlq ? jsonencode({
    deadLetterTargetArn = try(aws_sqs_queue.dlq_queues[each.key].arn, null)
    maxReceiveCount = try(each.value.max_receive_count, 5)
  }) : null

  tags = merge(local.common_tags, {
    Name = each.value.fifo_queue ? "${each.value.queue_name}.fifo" : each.value.queue_name
    Type = "SQS-Queue"
  })
}

# SQS Dead Letter Queues
resource "aws_sqs_queue" "dlq_queues" {
  for_each = {for k, v in var.messaging_services : k => v if v.has_dlq}

  name = each.value.fifo_queue ? "${each.value.queue_name}-dlq.fifo" : "${each.value.queue_name}-dlq"

  fifo_queue = each.value.fifo_queue

  message_retention_seconds = 1209600 # 14 days for DLQ

  tags = merge(local.common_tags, {
    Name = each.value.fifo_queue ? "${each.value.queue_name}-dlq.fifo" : "${each.value.queue_name}-dlq"
    Type = "SQS-DLQ"
  })
}

# SQS Queue Policies for SNS subscriptions
resource "aws_sqs_queue_policy" "queue_policies" {
  for_each = {for k, v in var.messaging_services : k => v if v.create_subscription}

  queue_url = aws_sqs_queue.queues[each.key].id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "sns.amazonaws.com"
        }
        Action   = "sqs:SendMessage"
        Resource = aws_sqs_queue.queues[each.key].arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = try(aws_sns_topic.topics[each.value.topic_name].arn, null)
          }
        }
      }
    ]
  })
}

# SNS Topic Subscriptions
resource "aws_sns_topic_subscription" "subscriptions" {
  for_each = {for k, v in var.messaging_services : k => v if v.create_subscription}

  topic_arn = try(aws_sns_topic.topics[each.value.topic_name].arn, null)
  protocol = "sqs"
  endpoint = try(aws_sqs_queue.queues[each.key].arn, null)
}
