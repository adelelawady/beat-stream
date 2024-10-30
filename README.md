

# 🎶 BeatStream

<div align="center">
  <img src="https://github.com/user-attachments/assets/b83cf4a8-0c56-4943-8399-7bf039b4edba" alt="Release Image" width="150"/>
</div>






BeatStream is a powerful music streaming and management application built using **JHipster** and Java. It allows users to download tracks and playlists from popular platforms like YouTube, SoundCloud, and Spotify. BeatStream also lets you upload your own songs, create a personalized library of tracks and playlists, and manage your music collection seamlessly. 🎧


[DOWNLOAD RELEASE 2 BETA ](https://github.com/adelelawady/beat-stream/releases/download/2.0.0.0/BeatStream.2.0.Beta.exe)


<div align="center">
  
  ### Overview of BeatStream 🎶
  <img width="800" alt="Overview Screenshot" src="https://github.com/user-attachments/assets/9a7475d7-f5f6-4d3e-a629-b57a7176f8d3">
  <br><br>

  ### Full Library Management 📚
  <img width="800" alt="Library Management Screenshot" src="https://github.com/user-attachments/assets/ad1c80c1-2425-40d0-8c15-c76205b3e551">
  <br><br>

  ### Download Progress Tracking 📥
  <img width="800" alt="Download Tracking Screenshot" src="https://github.com/user-attachments/assets/b8acdb77-a9e0-43f2-b837-ff11231283d7">
  
</div>

## Features


- 📥 **Download Music:** Easily download tracks and playlists from YouTube ![youtube](https://github.com/user-attachments/assets/780e3019-56ed-43b5-aa62-c0d6ff2cf1c6), SoundCloud ![soundcloud](https://github.com/user-attachments/assets/dfdda467-e73f-4236-b8b3-f2a64dada9ee), and Spotify   <img src="https://upload.wikimedia.org/wikipedia/commons/8/84/Spotify_icon.svg" alt="Release Image" width="32"/>

- 📤 **Upload Your Own Songs:** Add your personal music tracks to your library.
- 📚 **Library Management:** Create and manage a comprehensive library of songs and playlists.
- 📊 **Download Tracking:** Keep track of your downloads, including their progress and status.
- 🔄 **Task Downloads:** Queue multiple downloads and manage them efficiently.
- 🛠️ **Powerful Tools:** Utilizes **yt-dl** for downloading, **ffmpeg** for audio processing, and **ChromeDriver** for web automation.

## Technologies Used

BeatStream is built using:

- **Java** for the backend.
- **Spring Boot** for building the application.
- **JHipster** for rapid development and scaffolding.
- **Angular** (if applicable) for the front end.
- **MongoDb** (or your preferred database) for data persistence.
- **electron** For Windows APP
- 
## Requirements

To run BeatStream, you will need:

- ☕ Java Development Kit (JDK) 11 or later
- 🎥 **yt-dl:** A command-line program to download videos from YouTube and other sites.
- 🎵 **ffmpeg:** A tool to handle multimedia files and streams.
- 🌐 **ChromeDriver:** For automating Chrome browser actions.
- **Node.js** and **npm**
- 🐱‍🏍 **MongoDB:** Ensure that you have MongoDB running, as BeatStream uses it for data storage.

## Installation

1. 🛠️ **Clone the repository:**
   ```bash
   git clone https://github.com/adelelawady/beatstream.git
   cd beatstream
   ./mvnw
   ```

# Usage

🚀 Access the application:
Open your web browser and navigate to http://localhost:8080.
📖 Follow the on-screen instructions to download tracks, upload songs, and manage your library.

# API Documentation

For API documentation, visit http://localhost:8080/swagger-ui/.

# Contributing

🤝 Contributions are welcome! If you have suggestions for improvements or new features, feel free to fork the repository and submit a pull request.

# License

📝 This project is licensed under the MIT License - see the LICENSE file for details.


> **Important Note on Spotify Downloads**:  
> The BeatStream app provides a **limited integration with Spotify** that allows users to download songs by **grabbing the song title from Spotify and searching for it on YouTube**.  
> - **Single-Track Limitation**: Downloads are currently limited to one song per request for any Spotify track.  
> - **Educational Use Only**: This functionality is designed for **educational and personal research purposes** only. BeatStream aims to serve as a **learning tool** for those interested in understanding how multimedia streaming technologies work, promoting digital literacy, and encouraging responsible, ethical use of software.
>

# beatStream

This application was generated using JHipster 8.7.1, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v8.7.1](https://www.jhipster.tech/documentation-archive/v8.7.1).

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if omitted) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files

- `npmw` - wrapper to use locally installed npm.
  JHipster installs Node and npm locally using the build tool by default. This wrapper makes sure npm is installed locally and uses it avoiding some differences different versions can cause. By using `./npmw` instead of the traditional `npm` you can configure a Node-less environment to develop or test your application.
- `/src/main/docker` - Docker configurations for the application and services that the application depends on

## Development

### Doing API-First development using openapi-generator-cli

[OpenAPI-Generator]() is configured for this application. You can generate API code from the `src/main/resources/swagger/api.yml` definition file by running:

```bash
./mvnw generate-sources
```

Then implements the generated delegate classes with `@Service` classes.

To edit the `api.yml` definition file, you can use a tool such as [Swagger-Editor](). Start a local instance of the swagger-editor using docker by running: `docker compose -f src/main/docker/swagger-editor.yml up -d`. The editor will then be reachable at [http://localhost:7742](http://localhost:7742).

Refer to [Doing API-First development][] for more details.
The build system will install automatically the recommended version of Node and npm.

We provide a wrapper to launch npm.
You will only need to run this command when dependencies change in [package.json](package.json).

```
./npmw install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw
./npmw start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `./npmw update` and `./npmw install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `./npmw help update`.

The `./npmw run` command will list all the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code in `src/main/webapp/app/app.config.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false });
```

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

```
./npmw install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

```
./npmw install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/app.config.ts](src/main/webapp/app/app.config.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import 'leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

```
ng generate component my-component
```

will generate few files:

```
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.config.ts
```

## Building for production

### Packaging as jar

To build the final jar and optimize the beatStream application for production, run:

```
./mvnw -Pprod clean verify -DskipTests
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker compose -f src/main/docker/jhipster-control-center.yml up
```

## Testing

### Spring Boot tests

To launch your application's tests, run:

```
./mvnw verify
```

### Client tests

Unit tests are run by [Jest][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

```
./npmw test
```

## Others

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured from [sonar-project.properties](sonar-project.properties) as shown below:

```
sonar.login=admin
sonar.password=admin
```

For more information, refer to the [Code quality page][].

### Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a database in a docker container, run:

```
docker compose -f src/main/docker/mongodb.yml up -d
```

To stop it and remove the container, run:

```
docker compose -f src/main/docker/mongodb.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
npm run java:docker
```

Or build a arm64 docker image when using an arm64 processor os like MacOS with M1 processor family running:

```
npm run java:docker:arm64
```

Then run:

```
docker compose -f src/main/docker/app.yml up -d
```

When running Docker Desktop on MacOS Big Sur or later, consider enabling experimental `Use the new Virtualization framework` for better processing performance ([disk access performance is worse](https://github.com/docker/roadmap/issues/7)).

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: https://www.jhipster.tech
[JHipster 8.7.1 archive]: https://www.jhipster.tech/documentation-archive/v8.7.1
[Using JHipster in development]: https://www.jhipster.tech/documentation-archive/v8.7.1/development/
[Using Docker and Docker-Compose]: https://www.jhipster.tech/documentation-archive/v8.7.1/docker-compose
[Using JHipster in production]: https://www.jhipster.tech/documentation-archive/v8.7.1/production/
[Running tests page]: https://www.jhipster.tech/documentation-archive/v8.7.1/running-tests/
[Code quality page]: https://www.jhipster.tech/documentation-archive/v8.7.1/code-quality/
[Setting up Continuous Integration]: https://www.jhipster.tech/documentation-archive/v8.7.1/setting-up-ci/
[Node.js]: https://nodejs.org/
[NPM]: https://www.npmjs.com/
[OpenAPI-Generator]: https://openapi-generator.tech
[Swagger-Editor]: https://editor.swagger.io
[Doing API-First development]: https://www.jhipster.tech/documentation-archive/v8.7.1/doing-api-first-development/
[Webpack]: https://webpack.github.io/
[BrowserSync]: https://www.browsersync.io/
[Jest]: https://facebook.github.io/jest/
[Leaflet]: https://leafletjs.com/
[DefinitelyTyped]: https://definitelytyped.org/
[Angular CLI]: https://cli.angular.io/

### Angular Core Libraries

- **@angular/common**: Core common utilities 🌐, includes common directives like NgIf and NgFor, as well as pipes for handling dates, currency, and more.

- **@angular/compiler**: Essential for Angular template compilation 🛠️, allowing HTML and Angular directives to render as interactive elements.

- **@angular/core**: The heart of Angular's framework 🧩, including essential services and modules.

- **@angular/forms**: Handles forms and form validation 📝, managing both reactive and template-driven forms.

- **@angular/localize**: Localization support 🌍, enabling the app to support multiple languages.

- **@angular/platform-browser** and **@angular/platform-browser-dynamic**: Provide DOM interactions 🖱️ and template compilation on the browser.

- **@angular/router**: Routing and navigation 🧭, handling URL routing for a single-page application structure.

### Capacitor Plugins

- **@capacitor/android**, **@capacitor/cli**, **@capacitor/core**, **@capacitor/ios**: Facilitate mobile functionality for both iOS and Android 📱, enabling native functionality access, like file storage, notifications, and more.

### FontAwesome Libraries

- **@fortawesome/angular-fontawesome**: Angular component library for FontAwesome icons 🎨, allowing easy integration of scalable icons.

- **@fortawesome/fontawesome-svg-core** and **@fortawesome/free-solid-svg-icons**: Core FontAwesome SVG support 🖼️, providing a vast collection of icons for UI components.

### Translation and Localization

- **@ngx-translate/core**: Core translation module 🌐, enabling dynamic translation handling.

- **@ngx-translate/http-loader**: Allows loading translation files via HTTP requests 📂, making it simple to manage and update language files.

### UI Frameworks

- **@ng-bootstrap/ng-bootstrap**: Integrates Bootstrap into Angular 🖌️, enabling styled components like modals, alerts, and accordions.

- **bootstrap** and **bootswatch**: Bootstrap library and themes 🎨, providing pre-designed styles and UI components.

- **@popperjs/core** and **popper.js**: Handle positioning of tooltips and pop-ups 🗂️, essential for creating interactive UI elements.

### File Handling

- **ngx-file-drop**: Allows drag-and-drop file upload functionality 📥, enabling users to upload files by dragging them into a specified area.

### Media Playback

- **ngx-plyr** and **plyr**: Video and audio player support 🎶, providing customizable and lightweight media player features.

### Utilities and Functional Libraries

- **dayjs**: Lightweight date library 🗓️, ideal for date and time manipulation.

- **jquery**: General DOM manipulation library ✨, enhancing the functionality of traditional JavaScript.

- **rxjs**: Reactive programming support 🔄, offering observables that handle asynchronous data streams.

- **tslib**: Utility library for TypeScript 🔧, minimizing bundle size and supporting helper functions.

- **zone.js**: Execution context manager 🔍, crucial for handling async operations and change detection in Angular.

### Infinite Scrolling

- **ngx-infinite-scroll**: Provides infinite scroll functionality 🔄, enabling content to load dynamically as the user scrolls.

### WebSocket and Real-time Communication

- **@stomp/rx-stomp**: Manages STOMP messaging over WebSockets 🔗, allowing real-time data communication.

- **sockjs-client**: Provides a WebSocket fallback 🌐, maintaining connection stability even when WebSocket isn’t supported.
