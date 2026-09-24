# Banking Application

A full-stack banking web application built using **Java Servlets, JDBC, MySQL, HTML/CSS, and React**.

The application provides basic banking operations such as user registration, login, account management, deposits, withdrawals, money transfers, and transaction history.

## Features

* User registration
* Automatic bank account creation
* Secure password hashing using BCrypt
* User login and session management
* Account number generation
* View account balance
* Deposit money
* Withdraw money
* Transfer money between accounts
* Insufficient balance validation
* Receiver account validation
* Transaction history
* Logout
* React-based user interface
* MySQL database integration

## Technology Stack

### Backend

* Java 17
* Jakarta Servlets
* JDBC
* Apache Tomcat 10.1
* Maven
* MySQL 8.4
* BCrypt

### Frontend

* React
* JavaScript
* HTML
* CSS
* Vite

### Tools

* Visual Studio Code
* Git
* GitHub

## Architecture

```text
              React Frontend
                    |
                    | HTTP Requests
                    v
             Java Servlets
                    |
                    | JDBC
                    v
              MySQL Database
```

### Application Flow

```text
User
 |
 v
React UI
 |
 v
Java Servlet
 |
 v
JDBC
 |
 v
MySQL
```

## Project Structure

```text
banking-application-java-react/
│
├── banking-backend/
│   ├── pom.xml
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/banking/
│   │       │       ├── controller/
│   │       │       └── util/
│   │       └── resources/
│   │
│   └── .gitignore
│
├── banking-react/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── Dashboard.jsx
│   │   ├── Deposit.jsx
│   │   ├── Withdraw.jsx
│   │   ├── Transfer.jsx
│   │   ├── Transactions.jsx
│   │   └── Register.jsx
│   │
│   ├── package.json
│   └── ...
│
└── README.md
```

## Database

The application uses MySQL with the following main tables:

### Users

Stores registered user information.

### Accounts

Stores account numbers and account balances.

### Transactions

Stores deposit, withdrawal, and transfer transaction records.

## Security

* Passwords are stored using **BCrypt hashing**.
* Database credentials are stored outside the Java source code.
* `db.properties` is excluded from Git using `.gitignore`.
* Prepared statements are used for database queries.
* HTTP sessions are used for authenticated users.

## Running the Backend

Navigate to the backend directory:

```powershell
cd banking-backend
```

Build the application:

```powershell
mvn clean package
```

Deploy the generated WAR file to Apache Tomcat:

```text
target/banking-backend.war
```

Start Tomcat and access the backend through:

```text
http://localhost:8080/banking-backend/
```

## Running the Frontend

Navigate to the React application:

```powershell
cd banking-react
```

Install dependencies:

```powershell
npm install
```

Start the development server:

```powershell
npm run dev
```

The React application will be available at:

```text
http://localhost:5173/
```

## Important Configuration

Create the following file locally:

```text
banking-backend/src/main/resources/db.properties
```

Example:

```properties
db.url=jdbc:mysql://localhost:3306/banking_db
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Do **not** commit this file to GitHub.

## Future Improvements

Possible future enhancements include:

* JWT-based authentication
* Admin dashboard
* Account statement download
* Email notifications
* Password reset
* Input validation improvements
* Pagination for transaction history
* Deployment to AWS
* Docker containerization
* CI/CD using Jenkins and GitHub Actions

## Author

**Ravi Chandra Mouli**

GitHub: [Ravichandra938](https://github.com/Ravichandra938)
