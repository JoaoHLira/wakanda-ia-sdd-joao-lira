terraform {
  backend "s3" {
    bucket = "terraform-state-wakanda-ai-gaming"
    key = "wakanda-ai/terraform.tfstate"
    region = "us-east-1"
    encrypt = true
  }
}