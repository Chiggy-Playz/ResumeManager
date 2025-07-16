
# 📄 Resume Manager

**Resume Manager** is a lightweight Java Spring Boot application that lets you upload, manage, and serve your resume at a public URL — like [`resume.chiggydoes.tech`](https://resume.chiggydoes.tech). It supports versioned uploads and allows setting one version as "current", making sharing your resume seamless and up-to-date.

---

## ✨ Features

- 🔐 Auth-protected PDF upload interface
- 📄 Serve and view your current resume
- 🕓 Track upload timestamps
- 📦 Self-contained — no database required
- 🚀 One-command deployment via Docker Compose

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot
- **Frontend:** HTML, Tailwind CSS
- **Containerization:** Docker
- **Reverse Proxy:** (Optional) NGINX / Traefik for custom domains like `resume.chiggydoes.tech`

---

## 🚀 Deployment

### 1. Clone the Repo

```bash
git clone https://github.com/Chiggy-Playz/resume-manager.git
cd resume-manager
```

### 2. Configure Environment

Copy the example file and set a username/password for uploads:

```bash
cp .env.example .env
```

Edit `.env`:

```env
UPLOAD_AUTH_USERNAME=yourusername
UPLOAD_AUTH_PASSWORD=yourpassword
```

> These credentials are required to upload or modify resumes.

---

### 3. Run with Docker Compose

```bash
docker-compose up --build -d
```

Visit: [http://localhost:8338](http://localhost:8338)

---

## 🔧 Production Setup (Optional)

To serve it at `resume.chiggydoes.tech`, set up a reverse proxy (Traefik or NGINX) and point your domain’s DNS to your server.

Example NGINX config:

```nginx
server {
    listen 80;
    server_name resume.chiggydoes.tech;

    location / {
        proxy_pass http://localhost:8338;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Enable HTTPS with Let’s Encrypt (e.g., via Certbot or Traefik).

---

## 📁 Folder Structure

```
ResumeManager/
├── uploads/              # PDF files are stored here
├── src/                  # Java source code
├── Dockerfile
├── docker-compose.yaml
├── .env.example
└── README.md
```
