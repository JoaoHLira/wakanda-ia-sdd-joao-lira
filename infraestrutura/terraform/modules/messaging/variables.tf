variable "env" {
  description = "Environment name (e.g., dev, staging, prod)"
  type        = string
}

variable "region" {
  default = "us-east-1"
}

variable "messaging_services" {
  description = "Map of messaging services (SNS + SQS + subscription) to create"
  type = map(object({
    # SNS Topic configuration
    topic_name = string
    fifo_topic = bool
    content_based_deduplication = optional(bool, false)

    # SQS Queue configuration
    queue_name = string
    fifo_queue = bool
    delay_seconds = optional(number, 0)
    max_receive_count = optional(number, 5)
    visibility_timeout = optional(number, 300)
    message_retention_seconds = optional(number, 1209600) # 14 days
    has_dlq = optional(bool, true)

    # Subscription configuration
    create_subscription = optional(bool, true)
  }))
  default = {
    teste = {
      topic_name = "teste-topic"
      fifo_topic = true
      queue_name = "teste-queue"
      fifo_queue = true
      has_dlq    = false
    }
    zapi_requests = {
      topic_name = "zapi-requests-topic"
      fifo_topic = true
      queue_name = "zapi-requests-queue"
      fifo_queue = true
    }
    zapi_requests_delay = {
      topic_name = "zapi-requests-topic"
      fifo_topic          = true
      queue_name          = "zapi-requests-queue-delay"
      fifo_queue          = false
      create_subscription = false  # This is a delay queue, no direct subscription
    }
    memberkit_requests = {
      topic_name = "memberkit-requests-topic"
      fifo_topic = true
      queue_name = "memberkit-requests-queue"
      fifo_queue = true
    }
    asaas_requests = {
      topic_name = "asaas-requests-topic"
      fifo_topic = true
      queue_name = "asaas-requests-queue"
      fifo_queue = true
    }
    discord_requests = {
      topic_name = "discord-requests-topic"
      fifo_topic = true
      queue_name = "discord-requests-queue"
      fifo_queue = true
    }
    progresso_wakander_requests = {
      topic_name = "progresso-wakander-requests-topic"
      fifo_topic = true
      queue_name = "progresso-wakander-requests-queue"
      fifo_queue = true
    }
    xp_wakander_requests = {
      topic_name = "xp-wakander-requests-topic"
      fifo_topic = true
      queue_name = "xp-wakander-requests-queue"
      fifo_queue = true
    }
    clint_contato_requests = {
      topic_name = "clint-contato-requests-topic"
      fifo_topic = true
      queue_name = "clint-contato-requests-queue"
      fifo_queue = true
    }
    clint_requests = {
      topic_name = "clint-requests-topic"
      fifo_topic = true
      queue_name = "clint-requests-queue"
      fifo_queue = true
    }
  }
}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type = map(string)
  default = {}
}
