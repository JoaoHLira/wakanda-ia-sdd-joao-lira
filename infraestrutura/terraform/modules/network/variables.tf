variable "env" {
  description = "prod"
  type        = string
}

variable "region" {
  default = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR da VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  description = "CIDR da subnet pública"
  type        = string
  default     = "10.0.1.0/24"
}

variable "private_subnet_cidr" {
  description = "CIDR da subnet privada"
  type        = string
  default     = "10.0.2.0/24"
}

variable "availability_zone" {
  description = "Zona de disponibilidade"
  type        = string
  default     = "us-east-1a"
}

variable "cloudflare_ips" {
  description = "Lista de IPs da Cloudflare que podem acessar o túnel"
  type        = list(string)
  default = [
    # Faixas oficiais Cloudflare (podem ser ajustadas)
    "173.245.48.0/20",
    "103.21.244.0/22",
    "103.22.200.0/22",
    "103.31.4.0/22",
    "141.101.64.0/18",
    "108.162.192.0/18",
    "190.93.240.0/20",
    "188.114.96.0/20",
    "197.234.240.0/22",
    "198.41.128.0/17"
  ]
}