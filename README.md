# lebenslauf-creator

A javafx app to create a cv. Users can register and log in, fill out personal info, experience, education, generate a pdf (and have a live preview during editing). The app uses a MySQL database to store user data and resume versions. A private API is called to convert the resume data (in markdown format) to a PDF (or HTML for the preview).

This project is organized into several packages for the app logic, database management, services to generate the PDF/HTML, UI controllers, and util classes.

## Project details/planning

https://docs.google.com/spreadsheets/d/1_z0Bm3uBuh3AvUEDR5XX6PvmlrxpSi7X/edit?usp=sharing&ouid=102118861502683245070&rtpof=true&sd=true

## Features

### User auth

- users can register and login (fake email is ok)
- passwords are stored hashed in the database

### Resume mangaement

- create multiple versions of a resume with personal details, edu, work experience
- stored and retrieved from a MySQL database

### Image support

- upload a profile picture which is then converted to base64

### PDF & HTML generation

- uses a private API to convert markdown resume data to a PDF or HTML (for previews)
- configurable fonts, colors, sizes, and custom themes
- optional translation (currently only supports English and German using the same API)

### Live preview

- automatically generates a HTML preview in a webview while editing (updates every 5 seconds if changes detected)

### Light/dark mode

- toggle between light and dark mode

## Requirements

1) Java JDK 23 and Maven

2) MySQL

3) External API (private)

## Installation and Setup

1) Clone the repository

2) Env file

Create a .env file in the root directory with the following variables:

```txt
DB_USER=my_db_user
DB_PASS=my_db_password
PDF_API_KEY=my_secret_api_key
```

3) Database

- load the lebenslauf-creator.sql file into your MySQL database

4) Build/run

Run `org.lebenslauf.app.MainApp` to start the app (we use intellij)

## Usage

1) Start application

2) Register or log in

3) Fill out resume details, upload a picture

4) Configure the PDF

- choose a theme, font, color, size in the menu of the application
- you can also translate the resume

5) Preview

- the right side of the screen shows a HTML preview of the resume in real time
- the preview updates automatically every 5 seconds if changes are detected

6) Generate PDF and save

- click the submit button to generate a PDF of the resume
- the PDF is saved in the root directory of the app and the name will be `resume_FirstName_LastName.pdf`

7) Toggle light/dark mode

- use the toggle button in the menu

## Project structure

```txt
.env
lebenslauf-creator.sql
pom.xml
src/main
├── java
│   ├── module-info.java
│   └── org
│       └── lebenslauf
│           ├── app
│           ├── manager
│           ├── model
│           ├── service
│           └── ui
└── resources
    └── org
        └── lebenslauf
            ├── css
            └── fxml
```

module-info.java: module declaration such as controls, fxml, web, and exports the org.lebenslauf.app package
