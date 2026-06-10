terraform {
  required_version = ">= 1.9.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.81.0"
    }
  }
}

provider "aws" {
  region = var.region
}

module "network" {
  source = "./modules/network"
  env    = var.env
  region = var.region
}

module "messaging" {
  source = "./modules/messaging"
  env    = var.env
  region = var.region
}

module "database" {
  source       = "./modules/database"
  env          = var.env
  region       = var.region
  vpc          = module.network.vpc_id
  ec2_sg       = [module.network.ec2_sg_id]
  subnet_group = module.network.public_subnets_ids
  db_username = var.db_username
  db_password = var.db_password
}

module "compute" {
  env       = var.env
  source    = "./modules/compute"
  subnet_id = module.network.public_subnets_ids[0]
  security_group_ids = [module.network.ec2_sg_id]
  key_name  = var.ec2_key_name
}