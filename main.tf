terraform {
  required_providers {
    google = {
      source = "hashicorp/google"
      version = "4.56.0"
    }
  }
}

provider "google" {
	project     = "cs202-380320"
    region      = "us-central1"
}

resource "google_cloud_run_service" "password_login" {
  name     = "password-login"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "us-central1-docker.pkg.dev/cs202-380320/password-login/main"
		resources {
		  limits = {
			memory = "128Mi"
		  }
		}
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }
}
