# Metal Jackets FRC Scouting App 🐝# Scouting App: A Beginner's Guide to Setting Up and Running a Maven Project in VS Code



A web-based scouting app for FRC competitions. Scouts select their event, team, and match from live dropdowns powered by [The Blue Alliance API](https://www.thebluealliance.com/apidocs), answer a set of admin-configured questions, and all responses are saved to an Excel spreadsheet.This tutorial will guide you through creating a new Maven project in Visual Studio Code, configuring it to use Apache POI for Excel file handling, and running the application. It is designed for beginners.



------



## Prerequisites## Quick Start



- Java 17+ ([Download](https://adoptium.net))To access the Scouting App, make sure Tomcat is running and then open this link in your browser:

- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))

- Apache Tomcat 10.1+ ([Download](https://tomcat.apache.org/download-10.cgi))```

- A **The Blue Alliance API key** — sign up free at [thebluealliance.com/account](https://www.thebluealliance.com/account) → scroll to "Read API Keys" → generate onehttp://localhost:8080/scoutingapp/

```

---

If Tomcat is not running, start it first with this command:

## Quick Start

```

**1. Clone the repo**~/tomcat/apache-tomcat-10.1.24/bin/startup.sh

```bash```

git clone https://github.com/BeccaJoan/scoutingapp.git

cd scoutingapp### Admin Page

```

To set the scouting questions (admin only):

**2. Add your API key**

```

Create a `.env` file in the project root:http://localhost:8080/scoutingapp/admin.html

```bash```

echo "TBA_API_KEY=your_key_here" > .env

```---

> Without this the Event/Team/Match dropdowns will not load.

## Rebuild & Redeploy

**3. Create the Excel file**

```bashRun this command any time you change Java, HTML, or any other source file to rebuild the WAR and redeploy it to Tomcat:

mkdir -p ~/Documents && touch ~/Documents/FRC_Scouting_App.xlsx

``````

> Open it once in Excel/Numbers and save it so it's a valid `.xlsx` file, then close it.cd ~/Documents/Sources/scoutingapp/scoutingapp && ~/maven/apache-maven-3.9.6/bin/mvn clean package -q && cp target/scoutingapp-1.0-SNAPSHOT.war ~/tomcat/apache-tomcat-10.1.24/webapps/scoutingapp.war

```

**4. Build and deploy**

```bash> **Note:** You must rebuild after every change — edits to source files are not live until the WAR is rebuilt and copied to Tomcat.

mvn clean package -q

cp target/scoutingapp-1.0-SNAPSHOT.war ~/tomcat/apache-tomcat-10.1.24/webapps/scoutingapp.war---

```

> Replace `~/tomcat/apache-tomcat-10.1.24` with your actual Tomcat install path.## Step 1: Create a New Maven Project in VS Code



**5. Start Tomcat**1. Open Visual Studio Code.

```bash2. Go to **File > New Window** to start fresh.

~/tomcat/apache-tomcat-10.1.24/bin/startup.sh3. Open the Command Palette (Ctrl+Shift+P or Cmd+Shift+P on macOS).

```4. Search for and select **Maven: Create Maven Project**.

5. Choose **archetype-quickstart** from the list of archetypes. choose 1.4

**6. Open the app**6. Follow the prompts to configure your project:

```   - **Group ID**: `com.scout`

http://localhost:8080/scoutingapp/   - **Artifact ID**: `scoutingapp`

```   - **Version**: `1.0-SNAPSHOT`

   - **Package**: `com.scout`

---7. Open the newly created project in VS Code.



## How It Works---



- **Scouting page** (`/scoutingapp/`) — Select event, team, and match from live TBA dropdowns, then answer the pre-set questions and submit. Responses are written to `~/Documents/FRC_Scouting_App.xlsx`.## Step 2: Configure the `pom.xml`

- **Admin page** (`/scoutingapp/admin.html`) — Set the scouting questions that all scouts will see. Password protected (default: `2068`).

- The app proxies all TBA API calls server-side so your API key is never exposed to the browser.### Add Apache POI Dependencies

Apache POI is a library for working with Excel files. Add the following dependencies to the `<dependencies>` section of your `pom.xml`:

---

```xml

## Rebuild After Changes<dependency>

  <groupId>org.apache.poi</groupId>

Any time you edit source files, rebuild and redeploy with:  <artifactId>poi</artifactId>

```bash  <version>5.5.0</version>

mvn clean package -q && cp target/scoutingapp-1.0-SNAPSHOT.war ~/tomcat/apache-tomcat-10.1.24/webapps/scoutingapp.war</dependency>

```<dependency>

  <groupId>org.apache.poi</groupId>

---  <artifactId>poi-ooxml</artifactId>

  <version>5.5.0</version>

## More Details</dependency>

```

See [`docs/MAVEN_SETUP.md`](docs/MAVEN_SETUP.md) for a full walkthrough of the Maven project setup and configuration.

### Add the `exec-maven-plugin`
The `exec-maven-plugin` allows you to run your Java application directly from Maven. Add the following plugin **after** the `<pluginManagement>` section in the `<plugins>` section:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
  <configuration>
      <mainClass>com.scout.App</mainClass>
  </configuration>
</plugin>
```

#### Why Not in `<pluginManagement>`?
The `<pluginManagement>` section is used to define plugin versions and configurations for inheritance by child projects. However, it does not execute plugins directly. To use the `exec-maven-plugin`, it must be placed in the `<plugins>` section.

---

## Step 3: Set the Java Version

### Check Your Java SDK Version
Run the following command to check your Java SDK version:

```bash
java -version
```

Ensure you have the Java SDK (not just the JRE) installed. The SDK includes development tools like `javac` for compiling Java code, while the JRE is only for running Java applications.

### Update the Java Version in `pom.xml`
Set the Java version to 17 in the `<properties>` section of your `pom.xml`:

```xml
<properties>
  <maven.compiler.source>17</maven.compiler.source>
  <maven.compiler.target>17</maven.compiler.target>
</properties>
```

This ensures compatibility with your Java SDK version.

---

## Step 4: Build and Run the Application

### Clean the Project
Remove any previously compiled files:

```bash
mvn clean
```

### Compile the Project
Compile the Java code:

```bash
mvn compile
```

### Run the Application
Run the application and print the output to the terminal:

```bash
mvn exec:java
```

---

## Expected Output
If everything is set up correctly, the application will:
1. Open the `excersize.xlsx` file.
2. Print the number of sheets in the workbook.
3. Display the content of each cell in the first sheet, along with its type (e.g., STRING, NUMERIC, BOOLEAN).

---

## Troubleshooting

### File Not Found
If you see a `FileNotFoundException`, ensure the `excersize.xlsx` file is located in the correct directory. Update the `fileLocation` variable in `App.java` to the correct path if necessary.

### Java Version Issues
Ensure your Java SDK version matches the version specified in the `pom.xml`. If you encounter issues, verify your Java installation and update the `pom.xml` accordingly.

---

Happy coding!