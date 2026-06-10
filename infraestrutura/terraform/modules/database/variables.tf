variable "env" {
  description = "Environment name (e.g. dev, staging, prod)"
  type        = string
}

variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "vpc" {
  description = "AWS VPC"
  type = string
}

variable "ec2_sg" {
  description = "AWS EC2 Security Group ID"
  type = list(string)
}

variable "subnet_group" {
  description = "AWS Subnet Group"
  type = list(string)
}

variable "db_username" {
  description = "DataBase Username"
  type = string
  sensitive = true
}

variable "db_password" {
  description = "DataBase Password"
  type = string
  sensitive = true
}