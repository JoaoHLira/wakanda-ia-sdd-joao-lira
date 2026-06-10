resource "aws_security_group" "rds_sg" {
  name        = "rds-sg-${var.env}-wakanda-ai"
  description = "Permite acesso da EC2 ao RDS"
  vpc_id      = var.vpc

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = var.ec2_sg
    description     = "Permite acesso PostgreSQL a partir da EC2"
  }

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Permite acesso PostgreSQL da internet (APENAS TESTE)"
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "rds-sg-${var.env}"
    Environment = var.env
  }
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group-${var.env}"
  subnet_ids = var.subnet_group

  tags = {
    Name        = "rds-subnet-group-${var.env}"
    Environment = var.env
  }
}

resource "aws_db_instance" "rds_postgres" {
  identifier            = "wakanda-db-${var.env}"
  engine                = "postgres"
  engine_version        = "16.8"
  instance_class        = "db.t3.micro"
  allocated_storage     = 20
  max_allocated_storage = 25
  storage_type          = "gp2"
  username              = var.db_username
  password              = var.db_password
  db_subnet_group_name  = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  skip_final_snapshot   = true
  publicly_accessible   = true
  multi_az              = false

  tags = {
    Name        = "wakanda-rds-${var.env}"
    Environment = var.env
  }
}