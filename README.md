# Metal Jackets FRC Scouting App 🐝

A web-based scouting app for FRC competitions. Scouts select their event, team, and match from live dropdowns powered by [The Blue Alliance API](https://www.thebluealliance.com/apidocs), answer a set of admin-configured questions, and all responses are saved to an Excel spreadsheet.

---

## Prerequisites

- Java 17+ ([Download](https://adoptium.net))
- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))
- Apache Tomcat 10.1+ ([Download](https://tomcat.apache.org/download-10.cgi))
- **The Blue Alliance API key** — sign up free at [thebluealliance.com/account](https://www.thebluealliance.com/account), scroll to "Read API Keys" and generate one

NOTE: keep the api key handy as this will be pasted into your .env file in step two below.

> **Important:** Without a TBA API key the Event, Team, and Match dropdowns will not load.

---

## Quick Start

**1. Clone the repo**: Open a terminal then paste the below commands to create the scoutingapp folder with the app code:
```bash
git clone https://github.com/BeccaJoan/scoutingapp.git
cd scoutingapp
```

**2. Add your TBA API key**

Create or edit `~/.env` and add your key that you obtained from the TBA website:
```bash
# If the file doesn't exist yet:
echo "TBA_API_KEY=your_key_here" > .env

# Or manually edit ~/.env and add the line:
# TBA_API_KEY=your_key_here
```
Replace `your_key_here` with your actual key from The Blue Alliance.

**3. Build and deploy**
```bash
mvn clean package -q
cp target/scoutingapp-1.0-SNAPSHOT.war ~/tomcat/apache-tomcat-10.1.24/webapps/scoutingapp.war
```
> Replace `~/tomcat/apache-tomcat-10.1.24` with your actual Tomcat install path. Not sure where Tomcat is? Run this to find it:
> ```bash
> find ~ -name "catalina.sh" 2>/dev/null
> ```

**4. Start Tomcat**: if tomcat is not running start it with the following command:
```bash
~/tomcat/apache-tomcat-10.1.24/bin/startup.sh
```

**5. Open the app**

[http://localhost:8080/scoutingapp/](http://localhost:8080/scoutingapp/)

---
## That's It! The scouting app is loaded!

## How It Works

- **Scouting page** (`/scoutingapp/`) — Select event, team, and match from live TBA dropdowns, answer the pre-set questions, and submit. Responses are saved to `~/Documents/FRC_Scouting_App.xlsx`. 

NOTE: the Excel files is automatically created when you save your first question. 
- **Admin page** (`/scoutingapp/admin.html`) — Set the questions all scouts will see. Password protected (default: `2068`).
- All TBA API calls are proxied server-side so your API key is never exposed to the browser.

---

## Rebuild After Any Change

Any time you edit Java, HTML, or other source files:
```bash
mvn clean package -q && cp target/scoutingapp-1.0-SNAPSHOT.war ~/tomcat/apache-tomcat-10.1.24/webapps/scoutingapp.war
```

---

## More Details

See [`docs/MAVEN_SETUP.md`](docs/MAVEN_SETUP.md) for a full walkthrough of the Maven project setup and configuration.
