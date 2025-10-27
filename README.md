# Film Finder

A Spring Boot application that helps you find and manage movie information by integrating with the Watchmode API, MongoDB for data persistence, and Google's Gemini AI for enhanced search capabilities.

## Features

- Search for movies by title using the Watchmode API
- Store and retrieve movie information from MongoDB
- AI-powered search using Google's Gemini model
- RESTful API endpoints for movie search and management
- Built with Spring Boot 3.5.6 and Java 17
- Reactive programming model with WebFlux
- Environment-based configuration

## Prerequisites

- Java 17 or later
- Maven 3.6.3 or later
- MongoDB (local or remote instance)
- Watchmode API key (register at [Watchmode](https://www.watchmode.com/))
- Google Gemini API key (for AI features)

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
# MongoDB Configuration
MONGO_URL=localhost
MONGO_PORT=27017
MONGO_USERNAME=your_username
MONGO_PASSWORD=your_password

# Watchmode API
WATCHMODE_API_KEY=your_watchmode_api_key

# Google Gemini API
GEMINI_API_KEY=your_gemini_api_key
```

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/film-finder.git
   cd film-finder
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The application will be available at `http://localhost:8080`

## Configuration

### MongoDB
- Configured to connect to MongoDB using environment variables
- Auto-index creation is enabled
- Database name: `film_finder`

### AI Configuration
- Using Google's Gemini 2.5 Flash model
- Temperature: 1.0 (for creative responses)
- Max output tokens: 8192

## API Endpoints

- `GET /api/data/load` - Upload the movies.csv file
- `GET /api/movies/find?query=...` - Get movie details e.g. Find me a film about rebels and tell me where to stream it in GB

## Project Structure

```
src/main/java/com/softteco/filmfinder/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── model/            # Domain models
├── repository/       # MongoDB repositories
├── service/          # Business logic and services
└── FilmFinderApplication.java  # Main application class
```

## Dependencies

- Spring Boot 3.5.6
- Spring Data MongoDB
- Spring WebFlux
- Google Cloud AI (Gemini)
- Watchmode API Client
- Lombok
- OpenCSV
- LangChain
- Huggin Face

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
Based on the original article from here https://dev.to/mongodb/build-your-first-ai-agent-with-mongodb-and-langchain4j-4i5l