output "vpc_id" {
  description = "The ID of the VPC"
  value = aws_vpc.vpc.id
}

output "public_subnets_ids" {
  description = "List of IDs of public subnets"
  value = [for s in aws_subnet.public : s.id]
}

output "ec2_sg_id" {
  description = "The ID of the EC2 Security Group"
  value = aws_security_group.ec2_sg.id
}