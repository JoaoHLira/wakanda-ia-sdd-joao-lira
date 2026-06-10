locals {
  user_data = <<-EOF
              #!/bin/bash
              set -xe

              # Atualiza pacotes e instala Docker
              dnf update -y
              dnf install -y docker
              systemctl enable docker
              systemctl start docker

              mkdir -p /opt/${var.app_name}
              EOF
}

resource "aws_instance" "app_server" {
  ami                    = var.operational_system
  instance_type          = var.instance_type
  key_name               = var.key_name
  subnet_id              = var.subnet_id
  vpc_security_group_ids = var.security_group_ids
  user_data              = local.user_data

  tags = merge(var.common_tags, {
    Name = "ec2-${var.env}-wakanda-ai"
  })
}

# Cria o Elastic IP
resource "aws_eip" "ec2_eip" {
  domain = "vpc"

  tags = merge(var.common_tags, {
    Name = "eip-${var.env}-wakanda-ai"
  })
}

# Associa o Elastic IP à instância
resource "aws_eip_association" "ec2_eip_assoc" {
  instance_id   = aws_instance.app_server.id
  allocation_id = aws_eip.ec2_eip.id
}
