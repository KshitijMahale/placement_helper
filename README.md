# **Placement Helper**
SpringBoot + Thymeleaf Project

## **Configuration (`application.yml`)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/placement_helper
    username: postgres
    password: <YOUR_DB_PASSWORD>
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  thymeleaf:
    cache: false
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: <YOUR_CLIENT_ID>
            client-secret: <YOUR_CLIENT_SECRET>
            scope: profile, email
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
```

## **Get Your Google OAuth Client ID & Secret**
Follow these steps to obtain your credentials:

### **Step 1: Open Google Cloud Console**
Go to [Google Cloud Console](https://console.cloud.google.com).

### **Step 2: Create or Select a Project**
- Click the project dropdown in the top bar.
- Either **select an existing project** or click **“New Project”**.
- Name it something like `Placement Helper`.

### **Step 3: Enable Required APIs**
1. Navigate to `APIs & Services > Library`.
2. Enable the following APIs:
    - **OAuth 2.0 API**
    - **People API** _(for fetching user profile information)_
    - _Google+ API (optional, if needed)_

### **Step 4: Configure OAuth Consent Screen**
1. Go to `APIs & Services > OAuth consent screen`.
2. Choose **“External”** (if users outside your organization will sign in).
3. Fill in the required fields:
    - App Name: `Placement Helper`
    - User Support Email: _your email_
    - Developer Contact Info: _your email again_
4. Click **Save & Continue** _(no scopes needed for now)_.
5. Under **Test Users**, add your `@yourcollege.edu` email _(optional if restricting sign-ins)_.

### **Step 5: Create OAuth Credentials**
1. Go to `APIs & Services > Credentials`.
2. Click **“Create Credentials” → “OAuth client ID”**.
3. Choose **Web Application** as the application type.
4. Name it `Placement Helper Login`.
5. **Authorized Redirect URIs:**
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
6. Click **Create**,copy and paste your credentials.

