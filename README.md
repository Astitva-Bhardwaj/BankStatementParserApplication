Bank Statement Parser Application
Overview
This is a Spring Boot application that uses AI (OpenAI) to parse and extract structured information from bank statement PDFs. The application can handle PDF uploads with optional password protection and extract key financial details using advanced language models.
Features

Parse bank statements from uploaded PDF files
Support for password-protected PDFs
Extract key information using OpenAI's language model
Generate secure passwords based on user details
Flexible PDF text extraction

Prerequisites
Java 17 or higher
Maven
OpenAI API Key
Internet connection for AI processing

Technologies Used
Spring Boot
OpenAI API
PDFBox (PDF text extraction)
Lombok
Jackson (JSON processing)

Configuration
OpenAI Configuration

Obtain an OpenAI API key from OpenAI Platform
Set the API key in application.properties:
properties
openai.api.key=YOUR_API_KEY


Application Properties
Key configurations in application.properties:

server.port=8080: Application runs on port 8080
openai.model=gpt-4-turbo: OpenAI model used for parsing
openai.timeout=60: API request timeout
spring.servlet.multipart.max-file-size=10MB: Max file upload size

Installation
Clone the Repository
git clone https://github.com/yourusername/bank-statement-parser.git
cd bank-statement-parser

Build the Application
# Build with Maven
mvn clean install

# Skip tests (if needed)
mvn clean install -DskipTests

Running the Application
Using Maven
mvn spring-boot:run


API Endpoints
Parse Bank Statement
Upload PDF

Endpoint: POST /api/bank-statements/parse
Content-Type: multipart/form-data
Parameters:

file: Bank statement PDF file
password (optional): PDF password


Parse from File Path

Endpoint: POST /api/bank-statements/parse-from-path
Parameters:

filePath: Path to the PDF file
password (optional): PDF password

Generate Password
Endpoint: POST /api/bank-statements/generate-password
